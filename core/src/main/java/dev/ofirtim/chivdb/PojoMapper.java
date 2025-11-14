package dev.ofirtim.chivdb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class PojoMapper {

    private final TypeCoercion coercion;
    private final boolean caseInsensitiveFields;
    private final boolean ignoreUnknown;
    private final boolean failOnMissing;


    private PojoMapper(TypeCoercion coercion, boolean caseInsensitiveFields, boolean ignoreUnknown, boolean failOnMissing) {
        this.coercion = coercion;
        this.caseInsensitiveFields = caseInsensitiveFields;
        this.ignoreUnknown = ignoreUnknown;
        this.failOnMissing = failOnMissing;
    }


    public static Builder builder() {
        return new Builder();
    }

    private static boolean isScalar(Class<?> t) {
        return t.isPrimitive() || Number.class.isAssignableFrom(t) || t == String.class || t == UUID.class || t == Boolean.class || t == java.time.Instant.class || t == java.time.LocalDate.class || t == java.util.Date.class;
    }

    private static boolean hasSingleArgCtor(Class<?> t) {
        for (var c : t.getDeclaredConstructors()) if (c.getParameterCount() == 1) return true;
        return false;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toString(s.charAt(0)).toUpperCase() + s.substring(1);
    }

    public <T> T map(Map<String, Object> src, Class<T> type) {
        if (src == null) return null;
        if (src.size() == 1 && (isScalar(type) || hasSingleArgCtor(type))) {
            Object v = src.values().iterator().next();
            return constructSingleArg(type, v);
        }
        if (type.isRecord()) return constructRecord(src, type);
        return constructBean(src, type);
    }

    private <T> T constructRecord(Map<String, Object> src, Class<T> type) {
        try {
            RecordComponent[] comps = type.getRecordComponents();
            Object[] args = new Object[comps.length];
            for (int i = 0; i < comps.length; i++) {
                String name = comps[i].getName();
                String key = resolveKey(src, name);
                if (key == null) {
                    if (failOnMissing) throw new IllegalArgumentException("Missing field: " + name);
                    args[i] = null;
                    continue;
                }
                args[i] = coercion.coerce(src.get(key), comps[i].getType());
            }
            Class<?>[] pTypes = Arrays.stream(comps).map(RecordComponent::getType).toArray(Class[]::new);
            Constructor<T> ctor = type.getDeclaredConstructor(pTypes);
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map to record " + type.getName(), e);
        }
    }

    private <T> T constructBean(Map<String, Object> src, Class<T> type) {
        try {
            T obj = type.getDeclaredConstructor().newInstance();
            for (var e : src.entrySet()) {
                String field = e.getKey();
                String setter = "set" + capitalize(field);
                Method m = findMethod(type, setter);
                if (m == null) {
                    if (!ignoreUnknown) throw new IllegalArgumentException("Unknown property " + field);
                    continue;
                }
                Class<?> param = m.getParameterTypes()[0];
                Object coerced = coercion.coerce(e.getValue(), param);
                m.invoke(obj, coerced);
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map to bean " + type.getName(), e);
        }
    }

    private <T> T constructSingleArg(Class<T> type, Object v) {
        try {
            if (isScalar(type)) return type.cast(coercion.coerce(v, type));
            for (var c : type.getDeclaredConstructors()) {
                if (c.getParameterCount() == 1) {
                    c.setAccessible(true);
                    Class<?> p = c.getParameterTypes()[0];
                    return type.cast(c.newInstance(coercion.coerce(v, p)));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Single-arg construction failed for " + type.getName(), e);
        }
        throw new IllegalArgumentException("No single-arg constructor for " + type.getName());
    }

    private Method findMethod(Class<?> t, String name) {
        for (var m : t.getMethods()) if (m.getName().equals(name) && m.getParameterCount() == 1) return m;
        return null;
    }

    private String resolveKey(Map<String, Object> src, String wanted) {
        if (src.containsKey(wanted)) return wanted;
        for (var k : src.keySet()) if (k.equalsIgnoreCase(wanted)) return k;
        return null;
    }

    public static final class Builder {
        private TypeCoercion coercion = TypeCoercion.builder().build();
        private boolean caseInsensitiveFields = true;
        private boolean ignoreUnknown = true;
        private boolean failOnMissing = false;

        public Builder coercion(TypeCoercion c) {
            this.coercion = c;
            return this;
        }

        public Builder caseInsensitiveFields(boolean b) {
            this.caseInsensitiveFields = b;
            return this;
        }

        public Builder ignoreUnknown(boolean b) {
            this.ignoreUnknown = b;
            return this;
        }

        public Builder failOnMissing(boolean b) {
            this.failOnMissing = b;
            return this;
        }

        public PojoMapper build() {
            return new PojoMapper(coercion, caseInsensitiveFields, ignoreUnknown, failOnMissing);
        }
    }
}