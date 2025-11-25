package dev.millenialsoftwares.utils.helpers;

import dev.millenialsoftwares.utils.operators.ComparisonOp;
import dev.millenialsoftwares.utils.structs.ChivAtom;
import dev.millenialsoftwares.utils.structs.ChivCondition;
import dev.millenialsoftwares.utils.structs.ChivJunction;
import dev.millenialsoftwares.utils.structs.ChivNot;

import java.util.Optional;

public final class Normalizer {
    private Normalizer() {}


    public static ChivCondition toNNF(ChivCondition c) {
        if (c instanceof ChivAtom<?> || c == null) return c;
        if (c instanceof ChivJunction j) {
            var mapped = j.parts().stream().map(Normalizer::toNNF).toList();
            return new ChivJunction(j.and(), mapped);
        }
        if (c instanceof ChivNot n) return negateNNF(n.inner());
        throw new IllegalArgumentException("Unknown condition: " + c);
    }


    private static ChivCondition negateNNF(ChivCondition c) {
        if (c instanceof ChivAtom<?> a) return complement(a).orElseGet(() -> new ChivNot(a));
        if (c instanceof ChivJunction j) {
            var neg = j.parts().stream().map(Normalizer::negateNNF).toList();
            return new ChivJunction(!j.and(), neg);
        }
        if (c instanceof ChivNot n) return toNNF(n.inner());
        throw new IllegalArgumentException("Unknown condition: " + c);
    }

    private static Optional<ChivCondition> complement(ChivAtom<?> a) {
        var f = a.chivField(); var v = a.value(); var op = a.op();
        if (op instanceof ComparisonOp cmp) {
            return switch (cmp) {
                case GREATER_THAN -> Optional.of(new ChivAtom<>(f, ComparisonOp.LESS_THAN_OR_EQUAL, v));
                case GREATER_THAN_OR_EQUAL -> Optional.of(new ChivAtom<>(f, ComparisonOp.LESS_THAN, v));
                case LESS_THAN -> Optional.of(new ChivAtom<>(f, ComparisonOp.GREATER_THAN_OR_EQUAL, v));
                case LESS_THAN_OR_EQUAL -> Optional.of(new ChivAtom<>(f, ComparisonOp.GREATER_THAN, v));
                default -> Optional.empty();
            };
        }
        return Optional.empty();
    }
}