package me.devstudy.account.endpoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute(new SignupForm());
        return "account/sign-up";
    }
}
