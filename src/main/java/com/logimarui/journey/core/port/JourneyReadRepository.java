package com.logimarui.journey.core.port;

import com.logimarui.journey.core.model.JlItem;
import com.logimarui.journey.core.model.TiItem;
import com.logimarui.journey.core.model.TmlItem;
import com.logimarui.journey.core.model.TrItem;

import java.time.LocalDate;
import java.util.List;

/** Read-only boundary for the approved Journey V2 projections. */
public interface JourneyReadRepository {
    List<TmlItem> tml(LocalDate from, LocalDate to, String mode);
    List<TrItem> tr(LocalDate from, LocalDate to);
    List<TiItem> ti(LocalDate from, LocalDate to, String mode);
    List<JlItem> jl(LocalDate from, LocalDate to, String mode);
}
