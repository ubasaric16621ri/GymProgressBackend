package com.example.gymprogress.models;

public class JwtRequest {

    private String username;
    private String password;

    // Prazan konstruktor (potreban za deserializaciju JSON-a)
    public JwtRequest() {
    }

    // Parametarski konstruktor
    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getteri i setteri
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
