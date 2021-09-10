package com.isoterik.cash4life;

import com.badlogic.gdx.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class User {
    private String username;
    private String email;
    private String password;

    private float accountBalance;
    private String lastPlayedDate;

    private final float TIME_BETWEEN_PLAYS = 6F;

    // Change this in production (balance)
    public User() {
        this("", "", "", 10000f, null);
    }

    public User(String username, String email, String password, float accountBalance, String lastPlayedDate) {
        this.username = username;
        this.email = username;
        this.password = password;

        this.accountBalance = accountBalance;
        this.lastPlayedDate = lastPlayedDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAccountBalance(float accountBalance) {
        this.accountBalance = accountBalance;
    }

    public float getAccountBalance() {
        return accountBalance;
    }

    public void setLastPlayedDate(String date) {
        lastPlayedDate = date;
    }

    public String getLastPlayedDate() {
        return lastPlayedDate;
    }

    public String getAccountBalanceAsString() {
        StringBuilder balanceAsString = new StringBuilder(String.valueOf((int) accountBalance));

        int n = balanceAsString.length();
        if (n >= 4 && n <= 6) balanceAsString.insert(n - 3, ',');

        return "N" + balanceAsString;
    }

    public void deposit(float amount) {
        accountBalance += amount;
    }

    public void withdraw(float amount) {
        if (amount > accountBalance)
            throw new IllegalArgumentException("Withdraw amount must be less than or equal to account balance");

        accountBalance -= amount;
    }

    public Date getDateFromString(String string) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        return formatter.parse(lastPlayedDate);
    }

    public boolean isPlayable() throws ParseException {
        if (lastPlayedDate == null) return true;

        Date previousDate = getDateFromString(lastPlayedDate);
        Date currentDate = new Date();

        long timeDifference = currentDate.getTime() - previousDate.getTime();

        return TimeUnit.MILLISECONDS.toHours(timeDifference) >= TIME_BETWEEN_PLAYS;
    }

    public String getWaitTimeToPlay() throws ParseException {
        long timeDifference = new Date().getTime() - getDateFromString(lastPlayedDate).getTime();
        long difference = (long) ((TIME_BETWEEN_PLAYS * 3600000) - timeDifference);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        long hours = TimeUnit.MILLISECONDS.toHours(difference);

        String result = "";
        if (seconds > 0) result = (seconds - (minutes * 60)) + " seconds ";
        if (minutes > 0) result = (minutes - (hours * 60)) + " minutes " + result;
        if (hours > 0) result = hours + " hours " + result;

        return result;
    }
}
