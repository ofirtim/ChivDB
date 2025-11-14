package dev.ofirtim.chivdb.spi;

import dev.ofirtim.chivdb.structureless.ChivField;

public interface FieldResolver<F> {

    F resolve(ChivField<?> F);

}
