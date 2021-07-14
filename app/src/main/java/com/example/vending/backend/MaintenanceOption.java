package com.example.vending.backend;

public enum MaintenanceOption {
    RESET_PRODUCTS("reset products"),
    RESET_COINS("reset coins");

    String option;

    MaintenanceOption(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return option;
    }
}
