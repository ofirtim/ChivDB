package dev.ofirtim.chivdb;

import java.util.Properties;

public final class DataBaseConfiguration {
    public final EngineType type;
    public final String uri;
    public final String database;
    public final String username;
    public final String password;
    public final Properties extras;

    public final TypeCoercion coercion;
    public final PojoMapper objectifier;

    private DataBaseConfiguration(Builder b) {
        this.type = b.type;
        this.uri = b.uri;
        this.database = b.database;
        this.username = b.username;
        this.password = b.password;
        this.extras = b.extras;
        this.coercion = (b.coercion != null) ? b.coercion : TypeCoercion.builder().build();
        this.objectifier = (b.objectifier != null) ? b.objectifier : PojoMapper.builder().coercion(this.coercion).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private EngineType type;
        private String uri, database, username, password;
        private final Properties extras = new Properties();
        private TypeCoercion coercion;
        private PojoMapper objectifier;

        public Builder type(EngineType k) {
            this.type = k;
            return this;
        }

        public Builder uri(String s) {
            this.uri = s;
            return this;
        }

        public Builder database(String s) {
            this.database = s;
            return this;
        }

        public Builder username(String s) {
            this.username = s;
            return this;
        }

        public Builder password(String s) {
            this.password = s;
            return this;
        }

        public Builder extra(String k, String v) {
            this.extras.setProperty(k, v);
            return this;
        }

        public Builder objectifier(PojoMapper p) {
            this.objectifier = p;
            return this;
        }

        public DataBaseConfiguration build() {
            return new DataBaseConfiguration(this);
        }
    }
}