# Implementation Plan: 03-framework-config

**Module**: `03-framework-config`  
**Generated**: 2026-01-29  
**Architect**: HytaleArchitect  
**Specification Coverage**: CL-L1-001, CL-L2-001, CL-L2-002, CL-L2-003, CL-L3-001, CL-L3-002, CL-L3-003

---

## Executive Summary

The Configuration Framework provides YAML configuration management for all Argonath modules. Currently at **~25% completion** with only basic YAML loading implemented. **Critical issues found**: 1 Hytale API leak in `ConfigLibPlugin.java`, 4 `return null` statements that should be addressed, and pervasive `Object` type usage throughout the API that conflicts with the accessor v2.0.0 `DataValue` type system. Major gaps exist in validation, hot-reload, and migration features per spec CL-L2-002/003.

---

## Critical Issues Found

### Violations (MUST FIX)

| ID | Location | Type | Description | Severity |
|----|----------|------|-------------|----------|
| V-001 | [ConfigLibPlugin.java:3-4](src/main/java/com/argonathsystems/framework/config/ConfigLibPlugin.java#L3-L4) | Hytale API Leak | Direct `import com.hypixel.hytale.server.core.plugin.*` imports in non-adapter module | 🔴 CRITICAL |
| V-002 | [YamlConfigurationManager.java:88](src/main/java/com/argonathsystems/framework/config/YamlConfigurationManager.java#L88) | Silent Return Null | `return null;` on type mismatch without throwing exception | 🟡 MEDIUM |
| V-003 | [YamlConfigurationManager.java:160](src/main/java/com/argonathsystems/framework/config/YamlConfigurationManager.java#L160) | Silent Return Null | `return null;` on empty path without throwing exception | 🟡 MEDIUM |
| V-004 | [YamlConfigurationManager.java:169](src/main/java/com/argonathsystems/framework/config/YamlConfigurationManager.java#L169) | Silent Return Null | `return null;` on missing nested path | 🟡 MEDIUM |
| V-005 | [ConfigurationManager.java:26](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L26) | Object Type | `Object get(String path)` should return `Optional<DataValue>` | 🟡 MEDIUM |
| V-006 | [ConfigurationManager.java:52](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L52) | Object Type | `void set(String path, Object value)` should accept `DataValue` | 🟡 MEDIUM |
| V-007 | [ConfigurationManager.java:79](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L79) | Object Type | `Map<String, Object> getValues()` should return `Map<String, DataValue>` | 🟡 MEDIUM |

### Technical Debt

| ID | Location | Type | Description | Priority |
|----|----------|------|-------------|----------|
| TD-001 | Module | Missing Tests | Zero test files exist for this module | 🔴 HIGH |
| TD-002 | Module | No JSON Support | `JsonConfigurationManager` not implemented per spec | 🟡 MEDIUM |
| TD-003 | Module | No Validation | `SchemaValidator` not implemented per CL-L2-003 | 🔴 HIGH |
| TD-004 | Module | No Hot-Reload | `HotReloadManager`/`ConfigWatcher` not implemented per CL-L2-002 | 🟡 MEDIUM |
| TD-005 | Module | No Migration | `ConfigMigrator` not implemented | 🟡 MEDIUM |
| TD-006 | [YamlConfigurationManager.java:46](src/main/java/com/argonathsystems/framework/config/YamlConfigurationManager.java#L46) | Exception Handling | Uses `System.err.println` instead of SLF4J logging | 🟢 LOW |
| TD-007 | README.md | Template README | Contains placeholder text "Feature 1, Feature 2, Feature 3" | 🟢 LOW |
| TD-008 | CHANGELOG.md | Version Mismatch | Claims v1.0.0 released but IMPLEMENTATION_TRACKING shows v0.1.0 skeleton | 🟡 MEDIUM |

### TODO/FIXME/STUB Inventory

| Location | Type | Description | Action Required |
|----------|------|-------------|-----------------|
| (None found) | - | No TODO/FIXME/STUB markers in code | N/A |

---

## Requirements Traceability

### Specification Coverage

| Spec ID | Requirement | Status | Implementation Location | Notes |
|---------|-------------|--------|-------------------------|-------|
| CL-L1-001 | Configuration Management Library | 🚧 Partial | `ConfigurationManager`, `ConfigFactory` | Core interface defined, missing validation/hot-reload |
| CL-L2-001 | Hierarchical Configuration | ✅ Complete | `YamlConfigurationManager.getByPath()` | Dot-notation path traversal works |
| CL-L2-002 | Hot Reloading | ❌ Missing | N/A | No `ConfigWatcher` or `HotReloadManager` |
| CL-L2-003 | Validation Framework | ❌ Missing | N/A | No `SchemaValidator` |
| CL-L3-001 | File Format & Storage | ✅ Complete | `YamlConfigurationManager` | Path: `config/Argonath/{modId}/config.yml` |
| CL-L3-002 | API Design | 🚧 Partial | `ConfigurationManager` interface | Interface matches spec, needs `DataValue` migration |
| CL-L3-003 | Performance Requirements | ❓ Untested | N/A | No performance tests exist |

### Orphan Implementations (No Specification)

| Location | Description | Proposed Action |
|----------|-------------|-----------------|
| [ConfigLibPlugin.java](src/main/java/com/argonathsystems/framework/config/ConfigLibPlugin.java) | Empty plugin entry point with Hytale imports | REMOVE - Config lib should not be a plugin; use accessor pattern |

### Missing Implementations (Spec Not Implemented)

| Spec ID | Requirement | Gap Description | Priority |
|---------|-------------|-----------------|----------|
| CL-L2-002 | Hot Reloading | No file watcher, no change notification system | 🟡 MEDIUM |
| CL-L2-003 | Validation Framework | No JSON Schema validation, no type checking on load | 🔴 HIGH |
| CL-L3-003 | Performance Requirements | No benchmarks, no performance validation | 🟡 MEDIUM |
| LIB-001 | JSON/HOCON Support | Only YAML implemented, JSON/HOCON not available | 🟡 MEDIUM |
| LIB-001 | Environment Overrides | No dev/staging/production override system | 🟢 LOW |
| LIB-001 | Change Listeners | No callback registration for config changes | 🔴 HIGH |

---

## Accessor v2.0.0 Migration

### Required Changes

| Location | Current Type | Target Type | Migration Notes |
|----------|--------------|-------------|-----------------|
| [ConfigurationManager.java:26](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L26) | `Object` | `Optional<DataValue>` | Change return type of `get(String path)` |
| [ConfigurationManager.java:52](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L52) | `Object` | `DataValue` | Change parameter type of `set(String path, Object value)` |
| [ConfigurationManager.java:79](src/main/java/com/argonathsystems/framework/config/ConfigurationManager.java#L79) | `Map<String, Object>` | `Map<String, DataValue>` | Change return type of `getValues()` |
| [YamlConfigurationManager.java:21](src/main/java/com/argonathsystems/framework/config/YamlConfigurationManager.java#L21) | `Map<String, Object>` | `Map<String, DataValue>` | Internal storage map |
| All getByPath/setByPath methods | `Object` | `DataValue` | Internal helper methods |

### Breaking Change Impact

The migration to `DataValue` is a **MAJOR BREAKING CHANGE** that will cascade to all downstream modules:

**Directly Impacted Modules** (Wave 2.5+):
- `03-framework-storage` - Uses ConfigFactory for storage settings
- `04-framework-condition` - Uses config for condition definitions
- `04-framework-stats` - Uses config for stat definitions
- `04-framework-npc` - Uses config for NPC templates
- `04-framework-objective` - Uses config for objective definitions
- `05-framework-quest` - Uses config for quest definitions
- All `06-mod-*` modules

**Migration Strategy**:
1. Add `DataValue` dependency from accessor
2. Provide backward-compatible wrapper methods (deprecated)
3. Implement new type-safe API alongside existing
4. Publish migration guide for downstream modules
5. Remove deprecated methods in v2.0.0

### DataValue Integration Notes

The config module operates at a **different abstraction level** than `DataValue`:
- Config values come from YAML (SnakeYAML returns Java primitives)
- `DataValue` is designed for **runtime data exchange** between modules
- **Recommendation**: Keep `Object` internally for YAML parsing, convert to `DataValue` at API boundary

```java
// Proposed API addition
public Optional<DataValue> getAsDataValue(String path) {
    Object raw = get(path);
    return raw != null ? Optional.of(DataValue.of(raw)) : Optional.empty();
}
```

---

## HyUI Integration

**Not Applicable** - This module has no UI components.

---

## Hytale SDK Integration

### SDK Types Used

| Argonath Type | Hytale SDK Type | ECS Pattern | Notes |
|---------------|-----------------|-------------|-------|
| `ConfigLibPlugin` | `JavaPlugin`, `JavaPluginInit` | Plugin lifecycle | **VIOLATION** - Should be removed |

### ECS Alignment Requirements

The config module should have **ZERO Hytale SDK dependencies**. It is a platform-agnostic library.

**Required Changes**:
1. Remove `ConfigLibPlugin.java` entirely
2. Remove `HytaleServer-parent` dependency from `pom.xml`
3. Config loading should be triggered via accessor pattern from adapter layer

---

## Implementation Phases

### Phase 1: Critical Fixes [1 day]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P1-001 | Remove `ConfigLibPlugin.java` (Hytale leak) | `ConfigLibPlugin.java` | 0.25 days | None |
| P1-002 | Remove Hytale dependency from pom.xml | `pom.xml` | 0.25 days | P1-001 |
| P1-003 | Replace `System.err` with SLF4J logging | `YamlConfigurationManager.java` | 0.25 days | None |
| P1-004 | Update README with actual features | `README.md` | 0.25 days | None |

### Phase 2: API Stabilization [2 days]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P2-001 | Add `Optional<T>` return types to interface | `ConfigurationManager.java` | 0.5 days | Phase 1 |
| P2-002 | Implement `Optional`-based getters in YamlConfigurationManager | `YamlConfigurationManager.java` | 0.5 days | P2-001 |
| P2-003 | Add `DataValue` support at API boundary | `ConfigurationManager.java`, `YamlConfigurationManager.java` | 0.5 days | P2-002, Accessor v2.0.0 |
| P2-004 | Add change listener support | `ConfigurationManager.java`, `YamlConfigurationManager.java` | 0.5 days | P2-002 |

### Phase 3: Feature Completion [4 days]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P3-001 | Implement `JsonConfigurationManager` | `JsonConfigurationManager.java` | 1 day | Phase 2 |
| P3-002 | Implement `SchemaValidator` (JSON Schema) | `validation/SchemaValidator.java`, `validation/ValidationResult.java` | 1.5 days | Phase 2 |
| P3-003 | Implement `ConfigWatcher` (file watching) | `reload/ConfigWatcher.java` | 1 day | Phase 2 |
| P3-004 | Implement `HotReloadManager` | `reload/HotReloadManager.java` | 0.5 days | P3-003, P2-004 |

### Phase 4: Testing & Validation [2 days]

| Task ID | Description | Files | Effort | Dependencies |
|---------|-------------|-------|--------|--------------|
| P4-001 | Unit tests for ConfigurationManager interface | `src/test/java/.../ConfigurationManagerTest.java` | 0.5 days | Phase 2 |
| P4-002 | Unit tests for YamlConfigurationManager | `src/test/java/.../YamlConfigurationManagerTest.java` | 0.5 days | Phase 2 |
| P4-003 | Unit tests for validation | `src/test/java/.../validation/SchemaValidatorTest.java` | 0.5 days | P3-002 |
| P4-004 | Integration tests for hot-reload | `src/test/java/.../reload/HotReloadIntegrationTest.java` | 0.5 days | P3-004 |

---

## Estimated Timeline

| Phase | Duration | Start Condition |
|-------|----------|-----------------|
| Phase 1: Critical Fixes | 1 day | Immediate - **No blockers** |
| Phase 2: API Stabilization | 2 days | After Phase 1 |
| Phase 3: Feature Completion | 4 days | After Phase 2 |
| Phase 4: Testing & Validation | 2 days | After Phase 3 |
| **Total** | **9 days** | - |

**Minimum Viable**: Phases 1-2 (3 days) - Critical fixes + API stabilization

---

## Dependencies & Blockers

### Upstream Dependencies

| Module | Dependency Type | Status | Notes |
|--------|-----------------|--------|-------|
| `02-framework-accessor` | DataValue types | ✅ v2.0.0 Released | Need to add dependency for DataValue |
| `02-framework-core` | Core utilities | ✅ v2.1.0 Released | Already depends on this |

### Downstream Impact

**Modules that depend on 03-framework-config**:

| Module | Impact Level | Migration Required |
|--------|--------------|-------------------|
| `03-framework-storage` | 🔴 HIGH | Uses ConfigFactory extensively |
| `04-framework-condition` | 🟡 MEDIUM | Config-driven conditions |
| `04-framework-stats` | 🟡 MEDIUM | Stat definitions from config |
| `04-framework-npc` | 🟡 MEDIUM | NPC templates from config |
| `04-framework-objective` | 🟡 MEDIUM | Objective config |
| `04-framework-worldgen` | 🟡 MEDIUM | World generation config |
| `05-framework-quest` | 🔴 HIGH | Quest definitions from config |
| `05-framework-ui` | 🟡 MEDIUM | UI styling config |
| All `06-mod-*` | 🟡 MEDIUM | Per-mod configuration |

### External Blockers

- None identified

---

## Validation Criteria

### Build Validation
- [ ] `mvn clean compile` succeeds with zero errors
- [ ] `mvn test` passes all unit tests (currently none exist)
- [ ] No Hytale import leaks after Phase 1
- [ ] Checkstyle/PMD passes (if configured)

### Architecture Validation
- [ ] No `Object` types in public API (use generics + DataValue)
- [ ] No `return null;` without explicit Optional or exception
- [ ] ConfigLibPlugin.java removed
- [ ] Hytale dependency removed from pom.xml
- [ ] SLF4J used for all logging

### Specification Validation
- [ ] CL-L2-001: Hierarchical configuration works
- [ ] CL-L2-002: Hot-reload functional with change listeners
- [ ] CL-L2-003: Schema validation available
- [ ] CL-L3-002: API matches spec interface
- [ ] CL-L3-003: Performance < 0.1ms for lookups

### Test Coverage Targets
- [ ] Unit test coverage > 80%
- [ ] All public API methods tested
- [ ] Edge cases for path traversal tested
- [ ] Schema validation error cases tested

---

## Risk Assessment

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| DataValue migration breaks downstream | High | High | Provide deprecated compatibility layer |
| Hot-reload causes race conditions | Medium | Medium | Careful synchronization, copy-on-write semantics |
| JSON Schema library adds bloat | Low | Low | Use lightweight library (json-schema-validator) |
| File watcher platform differences | Medium | Low | Use Java NIO WatchService, test on all platforms |

---

## HytaleModder Handoff Prompt

```markdown
## Task: Implement 03-framework-config Refactoring

**Context**: The config framework needs critical fixes and API stabilization per the IMPLEMENTATION_PLAN.md.

**Phase 1 Tasks (Critical Fixes)**:

1. **Remove Hytale API Leak** (P1-001, P1-002):
   - Delete `src/main/java/com/argonathsystems/framework/config/ConfigLibPlugin.java`
   - Remove `<dependency>` for `HytaleServer-parent` from `pom.xml`
   - Config loading will be triggered via accessor pattern from adapter layer

2. **Fix Logging** (P1-003):
   - Replace all `System.err.println` calls with SLF4J `Logger.error()`
   - Add SLF4J Logger field to `YamlConfigurationManager`

3. **Update README** (P1-004):
   - Replace placeholder features with actual functionality description
   - Document the `config/Argonath/{modId}/config.yml` path convention

**Phase 2 Tasks (API Stabilization)**:

4. **Add Optional Return Types** (P2-001, P2-002):
   - Add `Optional<T> getOptional(String path, Class<T> type)` to interface
   - Implement in YamlConfigurationManager
   - Existing `get()` methods remain for backward compatibility (deprecated)

5. **Add DataValue Support** (P2-003):
   - Add accessor dependency to pom.xml
   - Add `Optional<DataValue> getAsDataValue(String path)` method
   - Convert raw objects to DataValue at API boundary

6. **Add Change Listeners** (P2-004):
   - Add `void addChangeListener(String pathPrefix, Consumer<ConfigChangeEvent> listener)`
   - Fire listeners on `set()` and `reload()`

**Reference Specifications**:
- HLR-ARCHITECTURE-002-lib-core-infrastructure.md (CL-L1-001 through CL-L3-003)
- SF-ARCHITECTURE-000-library-catalog.md (LIB-001)

**Build Validation**:
```bash
cd /mnt/d/Gaming/Argonath-Systems/03-framework-config
mvn clean compile
mvn test  # After tests are added
```

**Success Criteria**:
- No Hytale imports in module
- All public methods have Optional variants
- Change listener infrastructure in place
- All existing functionality preserved
```

---

## Appendix: File Inventory

| File | Lines | Status | Notes |
|------|-------|--------|-------|
| `ConfigurationManager.java` | 79 | 🚧 Needs DataValue migration | Core interface |
| `YamlConfigurationManager.java` | 200 | 🚧 Needs Optional + logging fixes | Main implementation |
| `ConfigFactory.java` | 40 | ✅ Functional | Factory pattern |
| `ConfigLibPlugin.java` | 10 | ❌ DELETE | Hytale leak |
| `package-info.java` | 17 | ✅ Good | Package documentation |

**Total Source Files**: 5  
**Test Files**: 0 (TD-001)  
**Estimated LOC**: ~250

---

*Document generated by HytaleArchitect agent on 2026-01-29*
