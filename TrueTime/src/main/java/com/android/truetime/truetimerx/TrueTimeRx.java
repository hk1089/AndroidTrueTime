package com.android.truetime.truetimerx;

import android.content.Context;
import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.reactivestreams.Publisher;

import com.android.truetime.CacheInterface;
import com.android.truetime.SntpClient;
import com.android.truetime.TrueTime;

public class TrueTimeRx extends TrueTime {

    private static final TrueTimeRx RX_INSTANCE = new TrueTimeRx();
    private static final String TAG = TrueTimeRx.class.getSimpleName();

    private int _retryCount = 50;

    public static TrueTimeRx build() {
        return RX_INSTANCE;
    }
    public static void initRxTrueTime(Context context) {
        DisposableSingleObserver<Date> disposable = TrueTimeRx.build()
                .withConnectionTimeout(31_428)
                .withRetryCount(100)
                .withSharedPreferencesCache(context)
                .withLoggingEnabled(true)
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Date>() {
                    @Override
                    public void onSuccess(Date date) {
                        Log.d(TAG, "Success initialized TrueTime :" + date.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "something went wrong when trying to initializeRx TrueTime", e);
                    }
                });
    }
    public TrueTimeRx withSharedPreferencesCache(Context context) {
        super.withSharedPreferencesCache(context);
        return this;
    }

    /**
     * Provide your own cache interface to cache the true time information.
     * @param cacheInterface the customized cache interface to save the true time data.
     */
    public TrueTimeRx withCustomizedCache(CacheInterface cacheInterface) {
        super.withCustomizedCache(cacheInterface);
        return this;
    }

    public TrueTimeRx withConnectionTimeout(int timeout) {
        super.withConnectionTimeout(timeout);
        return this;
    }

    public TrueTimeRx withRootDelayMax(float rootDelay) {
        super.withRootDelayMax(rootDelay);
        return this;
    }

    public TrueTimeRx withRootDispersionMax(float rootDispersion) {
        super.withRootDispersionMax(rootDispersion);
        return this;
    }

    public TrueTimeRx withServerResponseDelayMax(int serverResponseDelayInMillis) {
        super.withServerResponseDelayMax(serverResponseDelayInMillis);
        return this;
    }

    public TrueTimeRx withLoggingEnabled(boolean isLoggingEnabled) {
        super.withLoggingEnabled(isLoggingEnabled);
        return this;
    }

    public TrueTimeRx withRetryCount(int retryCount) {
        _retryCount = retryCount;
        return this;
    }

    /**
     * Initialize TrueTime
     * See {@link #initializeNtp(String)} for details on working
     *
     * @return accurate NTP Date
     * @param s
     */
    public Single<Date> initializeRx(String s) {
        return isInitialized()
                ? Single.just(now())
                : initializeNtp(s).map(new Function<long[], Date>() {
            @Override
            public Date apply(long[] longs) throws Exception {
                return now();
            }
        });
    }


    public Single<long[]> initializeNtp(String ntpPool) {
        return Flowable
                .just(ntpPool)
                .compose(resolveNtpPoolToIpAddresses())
                .compose(performNtpAlgorithm())
                .firstOrError();
    }


    public Single<long[]> initializeNtp(List<InetAddress> resolvedNtpAddresses) {
        return Flowable.fromIterable(resolvedNtpAddresses)
                .compose(performNtpAlgorithm())
                .firstOrError();
    }


    private FlowableTransformer<InetAddress, long[]> performNtpAlgorithm() {
        return new FlowableTransformer<InetAddress, long[]>() {
            @Override
            public Flowable<long[]> apply(Flowable<InetAddress> inetAddressObservable) {
                return inetAddressObservable
                        .map(new Function<InetAddress, String>() {
                            @Override
                            public String apply(InetAddress inetAddress) {
                                return inetAddress.getHostAddress();
                            }
                        })
                        .flatMap(bestResponseAgainstSingleIp(5))  // get best response from querying the ip 5 times
                        .take(5)                                  // take 5 of the best results
                        .toList()
                        .toFlowable()
                        .filter(new Predicate<List<long[]>>() {
                            @Override
                            public boolean test(List<long[]> longs) throws Exception {
                                return longs.size() > 0;
                            }
                        })
                        .map(filterMedianResponse())
                        .doOnNext(new Consumer<long[]>() {
                            @Override
                            public void accept(long[] ntpResponse) {
                                cacheTrueTimeInfo(ntpResponse);
                                saveTrueTimeInfoToDisk();
                            }
                        });
            }
        };
    }

    private FlowableTransformer<String, InetAddress> resolveNtpPoolToIpAddresses() {
        return new FlowableTransformer<String, InetAddress>() {
            @Override
            public Publisher<InetAddress> apply(Flowable<String> ntpPoolFlowable) {
                return ntpPoolFlowable
                        .observeOn(Schedulers.io())
                        .flatMap(new Function<String, Flowable<InetAddress>>() {
                            @Override
                            public Flowable<InetAddress> apply(String ntpPoolAddress) {
                                try {
                                   // Log.d(TAG, "---- resolving ntpHost : " + ntpPoolAddress);
                                    return Flowable.fromArray(InetAddress.getAllByName(ntpPoolAddress));
                                } catch (UnknownHostException e) {
                                    return Flowable.error(e);
                                }
                            }
                        });
            }
        };
    }

    private Function<String, Flowable<long[]>> bestResponseAgainstSingleIp(final int repeatCount) {
        return new Function<String, Flowable<long[]>>() {
            @Override
            public Flowable<long[]> apply(String singleIp) {
                return Flowable
                        .just(singleIp)
                        .repeat(repeatCount)
                        .flatMap(new Function<String, Flowable<long[]>>() {
                            @Override
                            public Flowable<long[]> apply(final String singleIpHostAddress) {
                                return Flowable.create(new FlowableOnSubscribe<long[]>() {
                                    @Override
                                    public void subscribe(@NonNull FlowableEmitter<long[]> o)
                                            throws Exception {

                                       /* Log.d(TAG,
                                                "---- requestTime from: " + singleIpHostAddress);*/
                                        try {
                                            o.onNext(requestTime(singleIpHostAddress));
                                            o.onComplete();
                                        } catch (IOException e) {
                                            o.tryOnError(e);
                                        }
                                    }
                                }, BackpressureStrategy.BUFFER)
                                        .subscribeOn(Schedulers.io())
                                        .doOnError(new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) {
                                             //   Log.e(TAG, "---- Error requesting time", throwable);
                                            }
                                        })
                                        .retry(_retryCount);
                            }
                        })
                        .toList()
                        .toFlowable()
                        .map(filterLeastRoundTripDelay()); // pick best response for each ip
            }
        };
    }

    private Function<List<long[]>, long[]> filterLeastRoundTripDelay() {
        return new Function<List<long[]>, long[]>() {
            @Override
            public long[] apply(List<long[]> responseTimeList) {
                Collections.sort(responseTimeList, new Comparator<long[]>() {
                    @Override
                    public int compare(long[] lhsParam, long[] rhsLongParam) {
                        long lhs = SntpClient.getRoundTripDelay(lhsParam);
                        long rhs = SntpClient.getRoundTripDelay(rhsLongParam);
                        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
                    }
                });

                //Log.d(TAG, "---- filterLeastRoundTrip: " + responseTimeList);

                return responseTimeList.get(0);
            }
        };
    }

    private Function<List<long[]>, long[]> filterMedianResponse() {
        return new Function<List<long[]>, long[]>() {
            @Override
            public long[] apply(List<long[]> bestResponses) {
                Collections.sort(bestResponses, new Comparator<long[]>() {
                    @Override
                    public int compare(long[] lhsParam, long[] rhsParam) {
                        long lhs = SntpClient.getClockOffset(lhsParam);
                        long rhs = SntpClient.getClockOffset(rhsParam);
                        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
                    }
                });

              //  Log.d(TAG, "---- bestResponse: " + Arrays.toString(bestResponses.get(bestResponses.size() / 2)));

                return bestResponses.get(bestResponses.size() / 2);
            }
        };
    }

}
