# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0] - 2026-01-29

### Added
- **BREAKING**: Type-safe API with `Optional<T>` return types
- **BREAKING**: `DataValue` integration for accessor v2.0.0 compatibility
- Change listener system with `addChangeListener()` and `removeChangeListener()`
- `ConfigChangeEvent` record for change notifications
- `getAsDataValue()` method for seamless DataValue interoperability
- Comprehensive unit test suite (90%+ coverage)
- SLF4J logging integration
- Thread-safe implementation using `ConcurrentHashMap`
- GSON dependency for future JSON configuration support

### Changed
- **BREAKING**: `ConfigurationManager.get()` now returns `Optional<T>` instead of nullable T
- **BREAKING**: `ConfigurationManager.set()` now accepts `DataValue` instead of `Object`
- **BREAKING**: `ConfigurationManager.getValues()` now returns `Map<String, DataValue>`
- Improved error handling with detailed logging
- README updated with comprehensive usage examples

### Removed
- **BREAKING**: `ConfigLibPlugin.java` - removed Hytale API dependency leak
- **BREAKING**: Direct Hytale SDK dependency from pom.xml
- **BREAKING**: All `Object` type usage replaced with type-safe alternatives
- Eliminated all `System.err.println` calls in favor of SLF4J

### Fixed
- Silent `return null` statements replaced with `Optional.empty()`
- Type mismatch handling now logs warnings instead of silently failing
- Path validation added to prevent null/empty path issues

### Security
- No security vulnerabilities identified

## [1.0.0] - 2026-01-25

### Added
- Initial release
- Basic YAML configuration loading
- Type-safe config factory
- Hierarchical configuration support
- Default value merging

[Unreleased]: https://github.com/Argonath-Systems/03-framework-config/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/Argonath-Systems/03-framework-config/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/Argonath-Systems/03-framework-config/releases/tag/v1.0.0
