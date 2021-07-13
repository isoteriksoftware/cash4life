package com.isoterik.cash4life;

// This class is useless. Used for some testing

public class DemoUser {
    private String username;
    private String email;
    private String password;

    private float accountBalance;

    public DemoUser() {
        this("", "", "", 0f);
    }

    public DemoUser(String username, String email, String password, float accountBalance) {
        this.username = username;
        this.email = username;
        this.password = password;

        this.accountBalance = accountBalance;
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

    public String getAccountBalance(int i) {
        return "N" + accountBalance;
    }

    public void deposit(float amount) {
        accountBalance += amount;
    }

    public void withdraw(float amount) {
        if (amount > accountBalance)
            throw new IllegalArgumentException("Withdraw amount must be less than or equal to account balance");

        accountBalance -= amount;
    }
}
