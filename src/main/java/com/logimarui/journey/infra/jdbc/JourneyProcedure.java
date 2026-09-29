package com.logimarui.journey.infra.jdbc;

import com.logimarui.operationalread.infra.jdbc.ProcedureSpec;

import java.util.Set;

/** The four V2 procedure calls approved for Journey reads. */
enum JourneyProcedure {
    TML("sp_tml_v2", true, "tml_segundos"),
    TR("sp_tr_v2", false, "tr_segundos"),
    TI("sp_ti_v2", true, "ti_segundos"),
    JL("sp_jl_v2", true, "jl_segundos");

    private final ProcedureSpec spec;

    JourneyProcedure(String sqlName, boolean usesMode, String indicatorColumn) {
        this.spec = new ProcedureSpec(sqlName, usesMode ? Set.of("ponto", "mpd") : Set.of(),
                Set.of("data", "mapa", "codigo_colaborador", indicatorColumn));
    }

    ProcedureSpec spec() { return spec; }
}
