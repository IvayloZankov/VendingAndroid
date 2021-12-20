package com.example.vending.maintenance;

import androidx.annotation.NonNull;

public enum MaintenanceOption {
    RESET_PRODUCTS("reset products"),
    RESET_COINS("reset coins");

    private final String option;

    MaintenanceOption(String option) {
        this.option = option;
    }

    @NonNull
    @Override
    public String toString() {
        return option;
    }
}
