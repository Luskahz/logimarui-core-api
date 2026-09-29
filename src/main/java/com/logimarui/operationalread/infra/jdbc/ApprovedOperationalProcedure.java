package com.logimarui.operationalread.infra.jdbc;

import java.util.Set;

/** Fixed SQL identifiers and result signatures accepted by the read executor. */
public enum ApprovedOperationalProcedure {
    TML("sp_tml_v2", true, "tml_segundos"),
    TR("sp_tr_v2", false, "tr_segundos"),
    TI("sp_ti_v2", true, "ti_segundos"),
    JL("sp_jl_v2", true, "jl_segundos");

    private final String sqlName;
    private final boolean requiresMode;
    private final Set<String> requiredColumns;

    ApprovedOperationalProcedure(String sqlName, boolean requiresMode, String indicatorColumn) {
        this.sqlName = sqlName;
        this.requiresMode = requiresMode;
        this.requiredColumns = Set.of("data", "mapa", "codigo_colaborador", indicatorColumn);
    }

    public String sqlName() { return sqlName; }
    public Set<String> requiredColumns() { return requiredColumns; }

    public void validateMode(String mode) {
        if (requiresMode && !("ponto".equals(mode) || "mpd".equals(mode))) {
            throw new IllegalArgumentException("Procedure requires ponto or mpd mode");
        }
        if (!requiresMode && mode != null) {
            throw new IllegalArgumentException("Procedure does not accept mode");
        }
    }
}
