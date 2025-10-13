package com.luxestay.hotel.dto;

public class LoginResponse {
    private String token;
    private String role;
    private int id;
    private String name;
    private String loginType;

    public LoginResponse() {}

    public LoginResponse(String token, String role, int id, String name, String loginType) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.name = name;
        this.loginType = loginType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}