package dev.millenialsoftwares.utils.engines.jooq.support;

import dev.ofirtim.chivdb.TypeCoercion;
import dev.ofirtim.chivdb.spi.FieldResolver;
import dev.ofirtim.chivdb.spi.ValueCoercer;
import dev.ofirtim.chivdb.structs.ChivField;
import org.jooq.Field;
import org.jooq.impl.DSL;

public final class JooqTypes implements FieldResolver<Field<?>>, ValueCoercer {

    private final TypeCoercion coercion;

    public JooqTypes(TypeCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public Field<?> resolve(ChivField<?> f) {
        return DSL.field(DSL.name(f.name()), f.type());
    }

    @Override
    public Object coerce(Object value, Class<?> targetType) {
        return coercion.coerce(value, targetType);
    }
}