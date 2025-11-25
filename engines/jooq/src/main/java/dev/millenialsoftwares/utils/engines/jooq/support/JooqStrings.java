package dev.millenialsoftwares.utils.engines.jooq.support;

import dev.ofirtim.chivdb.policy.LikeEscaper;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public final class JooqStrings {

    private final LikeEscaper esc;
    private final SQLDialect dialect;

    public JooqStrings(LikeEscaper esc, SQLDialect dialect) {
        this.esc = esc;
        this.dialect = dialect;
    }

    public Condition like(Field<String> f, String s) {
        return f.like(DSL.val("%" + esc.escape(s) + "%")).escape(esc.escapeChar());
    }

    public Condition startsWith(Field<String> f, String s) {
        return f.like(DSL.val(esc.escape(s) + "%")).escape(esc.escapeChar());
    }

    public Condition endsWith(Field<String> f, String s) {
        return f.like(DSL.val("%" + esc.escape(s))).escape(esc.escapeChar());
    }

    public Condition contains(Field<String> f, String s) {
        return f.like(DSL.val("%" + esc.escape(s) + "%")).escape(esc.escapeChar());
    }

    public Condition regex(Field<String> f, String re) {
        return switch (dialect.family()) {
            case POSTGRES -> DSL.condition("{0} ~ {1}", f, DSL.val(re));
            case MYSQL -> DSL.condition("{0} REGEXP {1}", f, DSL.val(re));
            default -> throw new UnsupportedOperationException("Regex unsupported: " + dialect);
        };
    }
}