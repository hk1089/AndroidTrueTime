# AndroidTrueTime

[![JitPack](https://jitpack.io/v/hk1089/AndroidTrueTime.svg)](https://jitpack.io/#hk1089/AndroidTrueTime)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

A robust Android library for accurate time synchronization using the Network Time Protocol (NTP). Get precise time from NTP servers instead of relying on potentially inaccurate device system clocks.

## Features

- 🕐 **Accurate Time Synchronization** - Get precise time from NTP servers
- ⚡ **Efficient Caching** - Minimize network calls with intelligent caching
- 🔄 **Reactive Support** - Full RxJava2 integration for asynchronous operations
- 🚀 **Boot Awareness** - Automatically handles device reboots
- 🛠️ **Highly Configurable** - Customizable timeouts, servers, and validation parameters
- 📱 **Android 5.0+** - Supports API level 21 and above

## Installation

### Gradle

Add JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.hk1089:AndroidTrueTime:v1.0")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.hk1089</groupId>
    <artifactId>AndroidTrueTime</artifactId>
    <version>v1.0</version>
</dependency>
```

## Quick Start

### 1. Initialize TrueTime

```kotlin
// In your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with caching
        TrueTimeRx.build()
            .withConnectionTimeout(31_428)
            .withRetryCount(100)
            .withSharedPreferencesCache(this)
            .withLoggingEnabled(true)
            .initializeRx("time.google.com")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ date ->
                Log.d("TrueTime", "Initialized: $date")
            }, { error ->
                Log.e("TrueTime", "Initialization failed", error)
            })
    }
}
```

### 2. Get Accurate Time

```kotlin
// Synchronous usage
if (TrueTime.isInitialized()) {
    val accurateTime = TrueTime.now()
    Log.d("Time", "Accurate time: $accurateTime")
}

// Reactive usage
TrueTimeRx.build()
    .initializeRx("time.google.com")
    .subscribeOn(Schedulers.io())
    .subscribe({ date ->
        Log.d("Time", "Accurate time: $date")
    }, { error ->
        error.printStackTrace()
    })
```

## Advanced Usage

### Configuration Options

```kotlin
TrueTimeRx.build()
    .withNtpHost("time.google.com")                    // NTP server
    .withConnectionTimeout(30_000)                      // Connection timeout
    .withRetryCount(50)                                 // Retry attempts
    .withRootDelayMax(100f)                            // Max root delay
    .withRootDispersionMax(100f)                       // Max root dispersion
    .withServerResponseDelayMax(750)                   // Max server response delay
    .withSharedPreferencesCache(context)               // Enable caching
    .withLoggingEnabled(true)                          // Enable logging
    .initializeRx("time.google.com")
```

### Custom Cache Implementation

```kotlin
class CustomCache : CacheInterface {
    override fun put(key: String, value: Long) {
        // Your custom caching logic
    }
    
    override fun get(key: String, defaultValue: Long): Long {
        // Your custom retrieval logic
        return defaultValue
    }
    
    override fun clear() {
        // Your custom clear logic
    }
}

// Use custom cache
TrueTimeRx.build()
    .withCustomizedCache(CustomCache())
    .initializeRx("time.google.com")
```

### Multiple NTP Servers

```kotlin
// The library automatically handles multiple servers for better accuracy
TrueTimeRx.build()
    .initializeRx("pool.ntp.org")  // This resolves to multiple IPs
```

## How It Works

1. **NTP Protocol**: Implements the Network Time Protocol (RFC 1305) for accurate time synchronization
2. **Clock Synchronization**: Uses the NTP algorithm to calculate clock offset and round-trip delay
3. **Caching**: Stores NTP time and device uptime to avoid repeated network calls
4. **Boot Detection**: Automatically clears cache on device reboot to ensure accuracy
5. **Validation**: Validates NTP responses for root delay, dispersion, and stratum

## Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
```

## Requirements

- **Android API Level**: 21+ (Android 5.0)
- **Java Version**: 8+
- **Dependencies**: RxJava2, AndroidX

## Sample App

This repository includes a sample application demonstrating various usage patterns. Run the app to see TrueTime in action.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Based on the original TrueTime library concept
- Uses the Network Time Protocol (NTP) for accurate time synchronization
- Built with RxJava2 for reactive programming support

## Support

If you find this library helpful, please consider giving it a ⭐ on GitHub!

For issues and feature requests, please use the [GitHub Issues](https://github.com/hk1089/AndroidTrueTime/issues) page.