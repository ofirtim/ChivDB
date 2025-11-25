package dev.millenialsoftwares.utils.policy;

public interface LikeEscaper {

    static LikeEscaper sqlStandard() {

        return new LikeEscaper() {

            @Override
            public String escape(String s) {
                return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            }

            @Override
            public char escapeChar() {
                return '\\';
            }
        };
    }

    String escape(String s);

    char escapeChar();
}
