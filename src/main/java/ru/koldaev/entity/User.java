package ru.koldaev.entity;

public class User {
    private String login;
    private String password;
    private String name;
    private String family;

    public User() {
    }

    public User
            (
                    String login,
                    String password,
                    String name,
                    String family
            ) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.family = family;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
