package com.isoterik.cash4life.cashpuzzles;

import java.util.HashMap;

public final class Constants {
    public static final float GUI_WIDTH  = 480f;
    public static final float GUI_HEIGHT = 780f;

    private final HashMap<Float, Integer> timeAndPrice;

    public Constants() {
        timeAndPrice = new HashMap<>();
        loadTimeAndPrices();
    }

    private void loadTimeAndPrices() {
        timeAndPrice.put(0.5f, 100);
        timeAndPrice.put(30f, 300);
        timeAndPrice.put(60f, 500);
        timeAndPrice.put(120f, 1000);
        timeAndPrice.put(180f, 2000);
        timeAndPrice.put(300f, 3500);
    }

    public HashMap<Float, Integer> getTimeAndPrice() {
        return timeAndPrice;
    }
}
