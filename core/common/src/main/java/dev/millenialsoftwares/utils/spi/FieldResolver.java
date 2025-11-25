package dev.millenialsoftwares.utils.spi;

import dev.millenialsoftwares.utils.structs.ChivField;

public interface FieldResolver<F> {

    F resolve(ChivField<?> F);

}
