package com.logimarui.receivables.core;

import java.time.LocalDate;
import java.util.Set;

public record ReceivablesQuery(String search, Integer unit, Integer seller, Integer supervisor,
        LocalDate dueFrom, LocalDate dueTo, LocalDate issuedFrom, LocalDate issuedTo,
        String status, String priority, String sort, int page, int size,
        LocalDate historyFrom, LocalDate historyTo, Integer riskLevel) {
    public ReceivablesQuery(String search, Integer unit, Integer seller, Integer supervisor,
            LocalDate dueFrom, LocalDate dueTo, LocalDate issuedFrom, LocalDate issuedTo,
            String status, String priority, String sort, int page, int size, LocalDate historyFrom, LocalDate historyTo) {
        this(search, unit, seller, supervisor, dueFrom, dueTo, issuedFrom, issuedTo,
                status, priority, sort, page, size, historyFrom, historyTo, null);
    }
    public ReceivablesQuery {
        search = search == null ? "" : search.trim();
        status = status == null || status.isBlank() ? "ALL" : status;
        priority = priority == null || priority.isBlank() ? "ALL" : priority;
        sort = sort == null || sort.isBlank() ? "PRIORITY" : sort;
        if (search.length() > 100 || page < 0 || page > 100000 || size < 1 || size > 100)
            throw new IllegalArgumentException("Pesquisa ou paginação inválida.");
        if (!Set.of("ALL", "OVERDUE", "DUE_TODAY", "UPCOMING", "UNKNOWN_DUE").contains(status)
                || !Set.of("ALL", "OVERDUE", "PREVENTIVE", "DUE_SOON", "MONITOR", "DATA_REVIEW").contains(priority)
                || !Set.of("PRIORITY", "RISK_DESC", "DUE_ASC", "AMOUNT_DESC", "CUSTOMER_ASC").contains(sort)
                || riskLevel != null && (riskLevel < 1 || riskLevel > 5))
            throw new IllegalArgumentException("Filtro ou ordenação inválida.");
        if ((dueFrom != null && dueTo != null && dueFrom.isAfter(dueTo))
                || (issuedFrom != null && issuedTo != null && issuedFrom.isAfter(issuedTo))
                || historyFrom == null || historyTo == null || historyFrom.isAfter(historyTo)
                || historyFrom.plusYears(5).isBefore(historyTo))
            throw new IllegalArgumentException("Período inválido (histórico máximo de 5 anos).");
        if ((unit != null && unit < 0) || (seller != null && seller < 0) || (supervisor != null && supervisor < 0))
            throw new IllegalArgumentException("Código inválido.");
    }
}

