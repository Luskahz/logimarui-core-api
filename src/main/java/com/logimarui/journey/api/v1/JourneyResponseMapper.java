package com.logimarui.journey.api.v1;

import com.logimarui.journey.api.v1.dto.IndicatorResult;
import com.logimarui.journey.api.v1.dto.Expurge;
import com.logimarui.journey.api.v1.dto.JlItem;
import com.logimarui.journey.api.v1.dto.JourneyPeriodResponse;
import com.logimarui.journey.api.v1.dto.OperationalContext;
import com.logimarui.journey.api.v1.dto.TiItem;
import com.logimarui.journey.api.v1.dto.TmlItem;
import com.logimarui.journey.api.v1.dto.TrItem;
import com.logimarui.journey.core.model.JourneyPeriod;

import java.util.function.Function;

/** Translates the read model into the stable v1 HTTP representation. */
final class JourneyResponseMapper {
    private JourneyResponseMapper() {}

    private static <S, T> JourneyPeriodResponse<T> period(JourneyPeriod<S> source, Function<S, T> mapper) {
        return new JourneyPeriodResponse<>(source.from(), source.to(), source.snapshotAt(),
                source.items().stream().map(mapper).toList());
    }

    static JourneyPeriodResponse<TmlItem> tml(JourneyPeriod<com.logimarui.journey.core.model.TmlItem> source) {
        return period(source, row -> new TmlItem(context(row.context()), row.mode(), row.pointOrigin(),
                row.entryAt(), row.morningAt(), row.mapDepartureAt(), row.startedAt(), row.endedAt(),
                row.snapshotAt(), metric(row.tml()), checklist(row.loadChecklist()),
                checklist(row.maintenanceChecklist()), row.totalChecklistSeconds(), expurge(row.expurge())));
    }

    static JourneyPeriodResponse<TrItem> tr(JourneyPeriod<com.logimarui.journey.core.model.TrItem> source) {
        return period(source, row -> new TrItem(context(row.context()), row.mapDepartureAt(), row.mapReturnAt(),
                row.snapshotAt(), row.plannedSeconds(), metric(row.tr()), row.dispersion(), expurge(row.expurge())));
    }

    static JourneyPeriodResponse<TiItem> ti(JourneyPeriod<com.logimarui.journey.core.model.TiItem> source) {
        return period(source, row -> new TiItem(context(row.context()), row.mode(), row.mapDepartureAt(),
                row.vehicleEntryAt(), row.physicalCloseAt(), row.financialCloseAt(), row.pointOrigin(),
                row.pointExitAt(), row.endedAt(), row.snapshotAt(), row.operationalStage(),
                metric(row.physicalClose()), metric(row.financialClose()), metric(row.ti()),
                row.sourcePhysicalSeconds(), row.sourceFinancialSeconds(), row.sourceInternalSeconds(),
                expurge(row.expurge())));
    }

    static JourneyPeriodResponse<JlItem> jl(JourneyPeriod<com.logimarui.journey.core.model.JlItem> source) {
        return period(source, row -> new JlItem(context(row.context()), row.mode(), row.pointJourneyDate(),
                row.pointOrigin(), row.morningAt(), row.pointEntryAt(), row.mapDepartureAt(),
                row.mapReturnAt(), row.physicalCloseAt(), row.financialCloseAt(), row.pointExitAt(),
                row.startedAt(), row.endedAt(), row.snapshotAt(), row.operationalStage(),
                metric(row.tml()), metric(row.tr()), metric(row.physicalClose()),
                metric(row.financialClose()), metric(row.ti()), metric(row.jl()),
                row.componentSumSeconds(), row.divergenceSeconds(), expurge(row.expurge())));
    }

    private static IndicatorResult metric(com.logimarui.journey.core.model.IndicatorResult source) {
        return source == null ? null : new IndicatorResult(source.secondsDifference(), source.seconds(),
                source.targetSeconds(), source.lifecycleStatus(), source.targetStatus(), source.achieved(),
                source.temporalOrderAnomaly());
    }

    private static OperationalContext context(com.logimarui.operationalread.core.model.OperationalContext source) {
        return source == null ? null : new OperationalContext(source.date(), source.map(),
                source.mapOrigin(), source.registration(), source.employeeCode(), source.role(),
                source.employeeName(), source.vehicle(), source.plate(), source.fleet(),
                source.mapDriverCode(), source.routeSupervisorCode(), source.routeSupervisorName());
    }

    private static Expurge expurge(com.logimarui.operationalread.core.model.Expurge source) {
        if (source == null) return null;
        var flags = source.flags();
        return new Expurge(source.present(), source.count(), source.ids(), source.types(),
                source.reasons(), source.observations(), flags == null ? null : new Expurge.Flags(
                flags.stoppedMap(), flags.considerRv(), flags.recharge(), flags.historical(),
                flags.general()));
    }

    private static TmlItem.Checklist checklist(com.logimarui.journey.core.model.TmlItem.Checklist source) {
        return source == null ? null : new TmlItem.Checklist(source.count(), source.totalSeconds(),
                source.effectiveSeconds(), source.effectiveStartedAt(), source.effectiveEndedAt(),
                source.adherent());
    }
}
