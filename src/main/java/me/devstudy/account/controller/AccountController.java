package me.devstudy.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.controller.validator.SignupFormValidator;
import me.devstudy.account.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupFormValidator signupFormValidator;
    private final AccountService accountService;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute(new SignupForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signupSubmit(@Valid @ModelAttribute SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        accountService.signup(signupForm);

        return "redirect:/";
    }
}
