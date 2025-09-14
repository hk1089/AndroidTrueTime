# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-XX

### Added
- Initial release of AndroidTrueTime library
- NTP time synchronization using Network Time Protocol
- Support for multiple NTP servers (default: time.google.com)
- Reactive programming support with RxJava2 integration
- Caching mechanism using SharedPreferences
- Boot detection to clear cache on device reboot
- Configurable parameters (timeouts, root delay, dispersion)
- Custom cache interface support
- Comprehensive error handling and validation
- Sample application demonstrating usage
- Full documentation and examples

### Features
- **TrueTime.java**: Core synchronous NTP operations
- **TrueTimeRx.java**: Reactive wrapper with RxJava2 support
- **SntpClient.java**: NTP protocol implementation
- **SharedPreferenceCacheImpl.java**: Default caching implementation
- **BootCompletedBroadcastReceiver.java**: Boot detection for cache clearing

### Technical Details
- Minimum Android API level: 21 (Android 5.0)
- Target Android API level: 33 (Android 13)
- Java 8 compatibility
- RxJava2 integration
- MIT License

### Dependencies
- `io.reactivex.rxjava2:rxjava:2.2.21`
- `io.reactivex.rxjava2:rxandroid:2.1.1`

## [Unreleased]

### Planned Features
- Support for additional NTP servers
- Enhanced error reporting
- Performance optimizations
- Additional caching strategies
- Unit test coverage improvements
- Documentation enhancements

---

## Version History

- **v1.0.0**: Initial release with core NTP functionality and RxJava2 support

## Migration Guide

### From v0.x to v1.0.0

This is the initial release, so no migration is needed. However, if you're migrating from a similar library:

1. Update your dependencies to use the new library
2. Replace initialization calls with the new API
3. Update any custom cache implementations to use the new `CacheInterface`
4. Review and update any error handling code

### Example Migration

**Old (hypothetical):**
```java
// Old library usage
OldTrueTime.init("time.google.com");
Date time = OldTrueTime.getTime();
```

**New:**
```java
// New AndroidTrueTime usage
TrueTimeRx.build()
    .withSharedPreferencesCache(context)
    .initializeRx("time.google.com")
    .subscribeOn(Schedulers.io())
    .subscribe(date -> {
        // Handle accurate time
    });
```

## Support

For questions, issues, or contributions, please visit our [GitHub repository](https://github.com/hk1089/AndroidTrueTime).
