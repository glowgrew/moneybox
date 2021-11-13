package ru.glowgrew.moneybox.api;

public final class PlayerAccount {

    private final String username;
    private final long amount;

    public PlayerAccount(String username, long amount) {
        this.username = username;
        this.amount = amount;
    }

    public String username() {
        return username;
    }

    public long amount() {
        return amount;
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerAccount)) return false;

        PlayerAccount that = (PlayerAccount) o;

        if (amount != that.amount) return false;
        return username.equals(that.username);
    }

    @Override
    public String toString() {
        return "PlayerAccount[" + "username=" + username + ", " + "amount=" + amount + ']';
    }
}
