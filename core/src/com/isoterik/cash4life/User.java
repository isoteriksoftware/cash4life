package com.isoterik.cash4life;

public class User {
    private String username;
    private String email;
    private String password;

    private float accountBalance;

    // Change this in production (balance)
    public User() {
        this("", "", "", 9500f);
    }

    public User(String username, String email, String password, float accountBalance) {
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

    public String getAccountBalanceAsString() {
        StringBuilder balanceAsString = new StringBuilder(String.valueOf((int) accountBalance));

        int n = balanceAsString.length();
        if (n == 4) balanceAsString.insert(1, ',');
        else if (n == 5) balanceAsString.insert(2, ',');
        else if (n == 6) balanceAsString.insert(3, 'c');

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
}
