package com.logimarui.receivables.core;

import com.logimarui.receivables.core.model.Receivables.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Versioned behavioural scorecard, not a calibrated probability of default. */
final class ReceivablesRisk {
    static final String MODEL = com.logimarui.receivables.core.model.Receivables.RISK_MODEL;
    private ReceivablesRisk() {}

    private static final class Observation {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal delayedAmount = BigDecimal.ZERO;
        BigDecimal weightedDays = BigDecimal.ZERO;
        int delay;
        LocalDate due;
        boolean open;
        void add(BigDecimal value, int days) {
            amount = amount.add(value);
            if (days > 0) delayedAmount = delayedAmount.add(value);
            weightedDays = weightedDays.add(value.multiply(BigDecimal.valueOf(days)));
            delay = Math.max(delay, days);
        }
    }

    static Risk assess(List<Payment> payments, List<OpenTitle> portfolio, String sourceStatus,
                       boolean otherUnit, LocalDate today, Policy policy) {
        LocalDate from = today.withDayOfYear(1);
        Map<String, Observation> observations = new HashMap<>();
        Set<String> paidTitles = new HashSet<>();
        for (Payment p : payments) {
            if (p.dueOn() == null || p.paidOn() == null || p.amount() == null || p.amount().signum() <= 0
                    || p.dueOn().isBefore(from) || p.dueOn().isAfter(today) || p.paidOn().isAfter(today)) continue;
            paidTitles.add(identity(p.title(), p.installment()));
            Observation o = observations.computeIfAbsent(identity(p.title(), p.installment()), k -> new Observation());
            o.due = o.due == null || p.dueOn().isBefore(o.due) ? p.dueOn() : o.due;
            o.add(p.amount(), days(p.dueOn(), p.paidOn()));
        }
        // Preserve monetary portions, but count an installment once even when it is partially paid and open.
        BigDecimal openOverdue = BigDecimal.ZERO;
        BigDecimal exposure = BigDecimal.ZERO;
        int maxOpenDelay = 0;
        for (OpenTitle t : portfolio) {
            exposure = exposure.add(t.pendingAmount());
            if (t.dueOn() == null || !t.dueOn().isBefore(today)) continue;
            int delay = days(t.dueOn(), today);
            openOverdue = openOverdue.add(t.pendingAmount());
            maxOpenDelay = Math.max(maxOpenDelay, delay);
            if (t.dueOn().isBefore(from)) continue;
            Observation o = observations.computeIfAbsent(identity(t.key().title(), t.key().installment()), k -> new Observation());
            o.open = true;
            o.due = o.due == null || t.dueOn().isBefore(o.due) ? t.dueOn() : o.due;
            o.add(t.pendingAmount(), delay);
        }
        List<Observation> rows = List.copyOf(observations.values());
        List<Observation> late = rows.stream().filter(o -> o.delay > 0).toList();
        List<Observation> recent = rows.stream().filter(o -> !o.due.isBefore(today.minusDays(policy.recentDays()))).toList();
        int recentLate = (int) recent.stream().filter(o -> o.delay > 0).count();
        // Exclude still-open titles from the baseline of successful observed payments.
        List<BigDecimal> onTime = rows.stream().filter(o -> o.delay == 0 && !o.open).map(o -> o.amount).sorted().toList();
        BigDecimal median = median(onTime);
        List<Observation> high = median == null ? List.of() : rows.stream()
                .filter(o -> o.amount.compareTo(median.multiply(new BigDecimal("1.5"))) > 0).toList();
        List<Observation> other = median == null ? List.of() : rows.stream()
                .filter(o -> o.amount.compareTo(median.multiply(new BigDecimal("1.5"))) <= 0).toList();
        BigDecimal amount = rows.stream().map(o -> o.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal delayed = rows.stream().map(o -> o.delayedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal dayAmount = rows.stream().map(o -> o.weightedDays).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal latePct = percent(late.size(), rows.size());
        BigDecimal amountPct = ratio(delayed, amount, 100);
        BigDecimal weightedDays = ratio(dayAmount, delayed, 1);
        BigDecimal highPct = percent(high.stream().filter(o -> o.delay > 0).count(), high.size());
        BigDecimal otherPct = percent(other.stream().filter(o -> o.delay > 0).count(), other.size());
        BigDecimal exposureRatio = median == null ? null : ratio(exposure, median, 1);
        String status = "UNAVAILABLE".equals(sourceStatus) ? "UNAVAILABLE"
                : otherUnit && paidTitles.isEmpty() ? "UNIT_UNMAPPED"
                : paidTitles.isEmpty() ? "NO_HISTORY"
                : paidTitles.size() < Math.max(3, policy.minimumHistoryTitles()) ? "INSUFFICIENT" : "AVAILABLE";
        var reasons = new ArrayList<String>();
        reasons.add(late.size() + " de " + rows.size() + " títulos observados com vencimento em " + today.getYear()
                + " atrasaram (pagos ou ainda abertos; cada parcela conta uma vez).");
        if (openOverdue.signum() > 0) reasons.add("Saldo vencido atual: R$ " + openOverdue.toPlainString()
                + "; maior atraso de " + maxOpenDelay + " dia(s).");
        if (!"AVAILABLE".equals(status)) {
            reasons.add("Histórico anual " + status + "; nível de risco não estimado.");
            return new Risk(status, MODEL, today.getYear(), from, today, null, null, rows.size(), late.size(),
                    latePct, amountPct, weightedDays, recentLate, openOverdue, maxOpenDelay,
                    median, high.size(), highPct, other.size(), otherPct, exposureRatio, List.of(), List.copyOf(reasons));
        }
        var components = new ArrayList<RiskComponent>();
        components.add(component("FREQUENCY", value(latePct) / 100, 30));
        components.add(component("SEVERITY", value(weightedDays) / 30, 20));
        components.add(component("RECENCY", recent.isEmpty() ? 0 : (double) recentLate / recent.size(), 15));
        components.add(component("LATE_AMOUNT", value(amountPct) / 100, 15));
        boolean comparableAmounts = onTime.size() >= 3 && high.size() >= 3 && other.size() >= 3;
        components.add(component("AMOUNT_SENSITIVITY", comparableAmounts
                ? (value(highPct) - value(otherPct)) / 100 : 0, 10));
        components.add(component("EXPOSURE", onTime.size() >= 3 && !late.isEmpty()
                ? (value(exposureRatio) - 1) / 2 : 0, 10));
        int score = components.stream().mapToInt(RiskComponent::points).sum();
        int level = score < 20 ? 1 : score < 40 ? 2 : score < 60 ? 3 : score < 80 ? 4 : 5;
        // Current delinquency overrides an otherwise diluted historical average.
        int floor = maxOpenDelay >= 90 ? 5 : maxOpenDelay >= 30 ? 4 : maxOpenDelay >= 8 ? 3 : 1;
        if (floor > level) {
            level = floor;
            reasons.add("Nível mínimo " + floor + " aplicado pelo atraso atual de " + maxOpenDelay + " dia(s).");
        }
        reasons.add("Índice comportamental " + score + "/100; nível " + level + "/5. Não representa probabilidade de inadimplência.");
        if (comparableAmounts && highPct.compareTo(otherPct) > 0)
            reasons.add("Títulos acima de 1,5× a mediana paga em dia atrasaram " + highPct + "%; demais: " + otherPct + "%.");
        else reasons.add("Efeito de valores maiores sem evidência suficiente de aumento de atraso.");
        return new Risk(status, MODEL, today.getYear(), from, today, level, score, rows.size(), late.size(),
                latePct, amountPct, weightedDays, recentLate, openOverdue, maxOpenDelay, median,
                high.size(), highPct, other.size(), otherPct, exposureRatio, List.copyOf(components), List.copyOf(reasons));
    }

    private static String identity(long title, String installment) { return title + ":" + installment; }
    private static int days(LocalDate from, LocalDate to) { return (int) Math.max(0, ChronoUnit.DAYS.between(from, to)); }
    private static double value(BigDecimal v) { return v == null ? 0 : v.doubleValue(); }
    private static RiskComponent component(String code, double fraction, int maximum) {
        return new RiskComponent(code, (int) Math.round(Math.max(0, Math.min(1, fraction)) * maximum), maximum);
    }
    private static BigDecimal percent(long numerator, long denominator) {
        return ratio(BigDecimal.valueOf(numerator), BigDecimal.valueOf(denominator), 100);
    }
    private static BigDecimal ratio(BigDecimal numerator, BigDecimal denominator, int multiplier) {
        return denominator.signum() == 0 ? null : numerator.multiply(BigDecimal.valueOf(multiplier))
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }
    private static BigDecimal median(List<BigDecimal> values) {
        if (values.isEmpty()) return null;
        int middle = values.size() / 2;
        return values.size() % 2 == 1 ? values.get(middle)
                : values.get(middle - 1).add(values.get(middle)).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }
}
