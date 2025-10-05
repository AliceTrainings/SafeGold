package com.goldloan.demo.entity;

public enum Karat {
    GOLD_18("18K"), GOLD_22("22K"), GOLD_24("24K");

    private final String display;

    Karat(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
