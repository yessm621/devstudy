package me.devstudy.account.endpoint.controller;

import lombok.Data;

@Data
public class SignupForm {

    private String nickname;
    private String email;
    private String password;
}
