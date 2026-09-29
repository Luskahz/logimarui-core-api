package com.logimarui.journey.core;

import com.logimarui.journey.core.model.JlItem;
import com.logimarui.journey.core.model.JourneyPeriod;
import com.logimarui.journey.core.model.TiItem;
import com.logimarui.journey.core.model.TmlItem;
import com.logimarui.journey.core.model.TrItem;
import com.logimarui.journey.core.port.JourneyReadRepository;
import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JourneyService {
    private final JourneyReadRepository reader;

    public JourneyService(JourneyReadRepository reader) {
        this.reader = reader;
    }

    public JourneyPeriod<TmlItem> tml(JourneyQuery query) {
        query.requireMode();
        return period(query, reader.tml(query.from(), query.to(), query.mode()),
                TmlItem::context, TmlItem::snapshotAt, TmlItem::expurge);
    }

    public JourneyPeriod<TrItem> tr(JourneyQuery query) {
        return period(query, reader.tr(query.from(), query.to()),
                TrItem::context, TrItem::snapshotAt, TrItem::expurge);
    }

    public JourneyPeriod<TiItem> ti(JourneyQuery query) {
        query.requireMode();
        return period(query, reader.ti(query.from(), query.to(), query.mode()),
                TiItem::context, TiItem::snapshotAt, TiItem::expurge);
    }

    public JourneyPeriod<JlItem> jl(JourneyQuery query) {
        query.requireMode();
        return period(query, reader.jl(query.from(), query.to(), query.mode()),
                JlItem::context, JlItem::snapshotAt, JlItem::expurge);
    }

    private <T> JourneyPeriod<T> period(
            JourneyQuery query,
            List<T> source,
            Function<T, OperationalContext> context,
            Function<T, LocalDateTime> snapshot,
            Function<T, Expurge> expurge
    ) {
        List<T> items = source.stream().filter(item -> {
            OperationalContext row = context.apply(item);
            if (query.map() != null && !Objects.equals(query.map(), row.map())) return false;
            if (query.employeeCode() != null && !Objects.equals(query.employeeCode(), row.employeeCode())) return false;
            if (query.role() != null && !query.role().equalsIgnoreCase(row.role())) return false;
            Boolean marked = expurge.apply(item).present();
            return switch (query.expurge()) {
                case "expurged" -> Boolean.TRUE.equals(marked);
                case "not_expurged" -> Boolean.FALSE.equals(marked);
                default -> true;
            };
        }).toList();
        LocalDateTime snapshotAt = items.stream().map(snapshot).filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);
        return new JourneyPeriod<>(query.from(), query.to(), snapshotAt, items);
    }
}
