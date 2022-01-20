package com.isoterik.cash4life.basketball;

import java.util.ArrayList;

public class Constants {
    public static final float GUI_WIDTH  = 480f;
    public static final float GUI_HEIGHT = 780f;

    public static Data SELECTED_DATA;

    private final ArrayList<Data> data;

    public Constants() {
        data = new ArrayList<>();
        loadTimeAndPrices();
    }

    private void loadTimeAndPrices() {
        //data.add(new Data(1, 1, 1, 3, 2)); // Test data
        data.add(new Data(20, 5000, 1000, 105, 100));
        //data.add(new Data(10, 2500, 500, 50, 40));
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public static class Data {
        private final int time;
        private final int price;
        private final int reward;
        private final int totalBalls;
        private final int ballsToWin;

        public Data(int time, int price, int reward, int totalBalls, int ballsToWin) {
            this.time = time;
            this.price = price;
            this.reward = reward;
            this.totalBalls = totalBalls;
            this.ballsToWin = ballsToWin;
        }

        public int getTime() {
            return time;
        }

        public int getPrice() {
            return price;
        }

        public int getReward() {
            return reward;
        }

        public int getTotalBalls() {
            return totalBalls;
        }

        public int getBallsToWin() {
            return ballsToWin;
        }
    }
}
