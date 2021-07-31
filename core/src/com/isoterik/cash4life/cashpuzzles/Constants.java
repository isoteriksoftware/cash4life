package com.isoterik.cash4life.cashpuzzles;

import java.util.HashMap;

public final class Constants {
    public static final float GUI_WIDTH  = 480f;
    public static final float GUI_HEIGHT = 780f;

    private final HashMap<Float, Integer> timeAndPrice;

    public static boolean RELOAD_TIME = false;
    public static float RELOAD_TIME_PRICE = 0f;

    public Constants() {
        timeAndPrice = new HashMap<>();
        loadTimeAndPrices();
    }

    private void loadTimeAndPrices() {
        timeAndPrice.put(30f, 500);
        timeAndPrice.put(60f, 1000);
        timeAndPrice.put(120f, 2000);
        timeAndPrice.put(180f, 3000);
    }

    public HashMap<Float, Integer> getTimeAndPrice() {
        return timeAndPrice;
    }
}
