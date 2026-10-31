package com.logimarui.receivables.core;

import com.logimarui.receivables.core.model.Receivables.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class ReceivablesRiskTest {
    private final LocalDate today = LocalDate.of(2026, 10, 31);
    private final Policy policy = new Policy(7, 90, 5, 2);
    private Payment payment(long title, String amount, int delay) {
        return new Payment(1, 10, title, "0", today.minusDays(40), today.minusDays(40 - delay),
                new BigDecimal(amount), BigDecimal.ZERO, new BigDecimal(amount), 0);
    }
    private OpenTitle open(long title, String amount, int delay) {
        return new OpenTitle(new Key(1, 10, title, "0"), "Cliente", today.minusDays(100), null,
                today.minusDays(delay), new BigDecimal(amount), new BigDecimal(amount), null, null, null, null, null);
    }
    private Risk assess(List<Payment> payments, List<OpenTitle> open) {
        return ReceivablesRisk.assess(payments, open, "AVAILABLE", false, today, policy);
    }
    private List<Payment> punctual() {
        var result = new ArrayList<Payment>();
        for (int i = 1; i <= 5; i++) result.add(payment(i, "100", 0));
        return result;
    }

    @Test void punctualHistoryIsLevelOneAndLargeExposureAloneDoesNotCreateRisk() {
        var risk = assess(punctual(), List.of(open(99, "10000", -5)));
        assertThat(risk.level()).isEqualTo(1);
        assertThat(risk.score()).isZero();
        assertThat(risk.onTimeMedian()).isEqualByComparingTo("100");
        assertThat(risk.exposureToOnTimeMedian()).isEqualByComparingTo("100");
    }

    @Test void onePartialTitleCountsOnceAndAmountsAreWeightedByLatePortion() {
        var payments = punctual();
        payments.add(payment(90, "50", 0));
        payments.add(payment(90, "20", 10));
        payments.add(payment(90, "10", 20));
        var risk = assess(payments, List.of(open(90, "20", 30)));
        assertThat(risk.observedTitles()).isEqualTo(6);
        assertThat(risk.lateTitles()).isEqualTo(1);
        assertThat(risk.lateTitlePercent()).isEqualByComparingTo("16.67");
        assertThat(risk.lateAmountPercent()).isEqualByComparingTo("8.33");
        assertThat(risk.weightedDelayDays()).isEqualByComparingTo("20");
        assertThat(risk.level()).isEqualTo(4);
        assertThat(risk.reasons()).anyMatch(r -> r.contains("Nível mínimo 4"));
    }

    @Test void higherAmountsAddPointsOnlyWithComparableSamplesAndHigherLateRate() {
        var payments = punctual();
        for (int i = 10; i <= 12; i++) payments.add(payment(i, "500", 20));
        var risk = assess(payments, List.of(open(99, "600", -2)));
        assertThat(risk.highAmountTitles()).isEqualTo(3);
        assertThat(risk.highAmountLatePercent()).isEqualByComparingTo("100");
        assertThat(risk.otherAmountLatePercent()).isEqualByComparingTo("0");
        assertThat(risk.components()).anySatisfy(c -> {
            assertThat(c.code()).isEqualTo("AMOUNT_SENSITIVITY");
            assertThat(c.points()).isEqualTo(10);
        });
        assertThat(risk.level()).isGreaterThanOrEqualTo(3);
        payments.remove(payments.size() - 1);
        assertThat(assess(payments, List.of()).components().stream()
                .filter(c -> c.code().equals("AMOUNT_SENSITIVITY")).findFirst().orElseThrow().points()).isZero();
    }

    @Test void unknownHistoryNeverBecomesLowRiskEvenWithManyOpenTitles() {
        var open = new ArrayList<OpenTitle>();
        for (int i = 0; i < 10; i++) open.add(open(i + 100, "200", 100));
        for (String status : List.of("UNAVAILABLE", "EMPTY_PERIOD")) {
            var risk = ReceivablesRisk.assess(List.of(), open, status, false, today, policy);
            assertThat(risk.level()).isNull();
            assertThat(risk.score()).isNull();
        }
        assertThat(assess(List.of(payment(1, "10", 0)), open).status()).isEqualTo("INSUFFICIENT");
        assertThat(ReceivablesRisk.assess(List.of(), open, "AVAILABLE", true, today, policy).status()).isEqualTo("UNIT_UNMAPPED");
    }

    @Test void excludesPriorYearFutureAndInvalidEventsButRetainsOldOutstandingDelinquencyFloor() {
        var payments = punctual();
        payments.add(new Payment(1, 10, 70, "0", LocalDate.of(2025, 12, 1), today.minusDays(1),
                BigDecimal.TEN, null, null, 0));
        payments.add(new Payment(1, 10, 71, "0", today.minusDays(1), today.plusDays(1), BigDecimal.TEN, null, null, 0));
        payments.add(new Payment(1, 10, 72, "0", null, today.minusDays(1), BigDecimal.TEN, null, null, 0));
        var risk = assess(payments, List.of(open(80, "500", 400)));
        assertThat(risk.observedTitles()).isEqualTo(5);
        assertThat(risk.lateTitles()).isZero();
        assertThat(risk.level()).isEqualTo(5);
        assertThat(risk.maximumOpenDelayDays()).isEqualTo(400);
        assertThat(risk.openOverdueAmount()).isEqualByComparingTo("500");
    }

    @Test void modelUsesOnlyMaturedTitlesAndSupportsEarlyPaymentOfCurrentYearDueDate() {
        var payments = punctual();
        payments.add(new Payment(1, 10, 70, "0", LocalDate.of(2026, 1, 1), LocalDate.of(2025, 12, 30),
                new BigDecimal("100"), null, null, 0));
        payments.add(new Payment(1, 10, 71, "0", today.plusDays(1), today, BigDecimal.TEN, null, null, 0));
        assertThat(assess(payments, List.of(open(99, "100", 0))).observedTitles()).isEqualTo(6);
    }
}
