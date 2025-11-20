package dev.ofirtim.chivdb.spi;

import dev.ofirtim.chivdb.structs.ChivField;

public interface FieldResolver<F> {

    F resolve(ChivField<?> F);

}
