package com.logimarui.journey.infra.jdbc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JourneyProcedureTest {
    @Test
    void journeyOwnsExactlyTheFourApprovedV2CallsAndTheirSignatures() {
        assertThat(Arrays.stream(JourneyProcedure.values()).map(value -> value.spec().sqlName()))
                .containsExactlyInAnyOrder("sp_tml_v2", "sp_tr_v2", "sp_ti_v2", "sp_jl_v2");
        assertThat(JourneyProcedure.TR.spec().requiredColumns())
                .isEqualTo(Set.of("data", "mapa", "codigo_colaborador", "tr_segundos"));
        assertThat(JourneyProcedure.TML.spec().requiredColumns()).contains("tml_segundos");
        assertThat(JourneyProcedure.TI.spec().requiredColumns()).contains("ti_segundos");
        assertThat(JourneyProcedure.JL.spec().requiredColumns()).contains("jl_segundos");
    }

    @Test
    void trRejectsModeAndOtherIndicatorsRequireOneOfTwoModes() {
        JourneyProcedure.TR.spec().validateMode(null);
        assertThatThrownBy(() -> JourneyProcedure.TR.spec().validateMode("ponto"))
                .isInstanceOf(IllegalArgumentException.class);
        for (JourneyProcedure procedure : Set.of(JourneyProcedure.TML, JourneyProcedure.TI, JourneyProcedure.JL)) {
            procedure.spec().validateMode("ponto");
            procedure.spec().validateMode("mpd");
            assertThatThrownBy(() -> procedure.spec().validateMode(null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> procedure.spec().validateMode("other"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void operationalReadSourceDoesNotDependOnJourneyOrNameItsIndicators() throws IOException {
        Path root = Path.of("src/main/java/com/logimarui/operationalread");
        try (var files = Files.walk(root)) {
            String source = files.filter(path -> path.toString().endsWith(".java"))
                    .map(path -> {
                        try { return Files.readString(path); }
                        catch (IOException exception) { throw new IllegalStateException(exception); }
                    }).collect(Collectors.joining("\n"));
            assertThat(source).doesNotContain("com.logimarui.journey", "sp_tml_v2", "sp_tr_v2",
                    "sp_ti_v2", "sp_jl_v2", "enum TML", "enum TR", "enum TI", "enum JL");
        }
    }
}
