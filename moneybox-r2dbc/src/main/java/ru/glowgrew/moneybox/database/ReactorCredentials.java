package ru.glowgrew.moneybox.database;

import java.util.Objects;

public final class ReactorCredentials {

    private final String host;
    private final int port;
    private final String username, password, database;

    private ReactorCredentials(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String database() {
        return database;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password, database);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReactorCredentials)) return false;

        ReactorCredentials that = (ReactorCredentials) o;

        if (port != that.port) return false;
        if (!host.equals(that.host)) return false;
        if (!username.equals(that.username)) return false;
        if (!password.equals(that.password)) return false;
        return database.equals(that.database);
    }

    @Override
    public String toString() {
        return "ReactorCredentials{host='" +
               host +
               '\'' +
               ", port=" +
               port +
               ", username='" +
               username +
               '\'' +
               ", password='" +
               password +
               '\'' +
               ", database='" +
               database +
               '\'' +
               '}';
    }

    public static class Builder {

        private String host = "127.0.0.1";
        private int port = -1;
        private String username = "root";
        private String password = "root";
        private String database = "database";

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public ReactorCredentials build() {
            if (port == -1) {
                throw new IllegalStateException("port is not specified");
            }
            return new ReactorCredentials(host, port, username, password, database);
        }
    }
}
