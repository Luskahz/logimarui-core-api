package com.logimarui.operationalread.infra.jdbc;

import java.util.Set;

/** Generic, immutable SQL call shape supplied by a product-specific adapter. */
public record ProcedureSpec(String sqlName, Set<String> allowedModes, Set<String> requiredColumns) {
    public ProcedureSpec {
        if (sqlName == null || !sqlName.matches("[a-z][a-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid procedure identifier");
        }
        allowedModes = Set.copyOf(allowedModes);
        requiredColumns = Set.copyOf(requiredColumns);
        if (requiredColumns.isEmpty()) {
            throw new IllegalArgumentException("Result signature is required");
        }
    }

    public void validateMode(String mode) {
        if (allowedModes.isEmpty() && mode != null) {
            throw new IllegalArgumentException("Procedure does not accept mode");
        }
        if (!allowedModes.isEmpty() && (mode == null || !allowedModes.contains(mode))) {
            throw new IllegalArgumentException("Invalid or missing procedure mode");
        }
    }
}
