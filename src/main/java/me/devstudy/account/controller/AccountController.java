package me.devstudy.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.controller.validator.SignupFormValidator;
import me.devstudy.account.domain.entity.Account;
import me.devstudy.account.repository.AccountRepository;
import me.devstudy.account.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupFormValidator signupFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

    @GetMapping("/check-email-token")
    public String verifyEmail(@RequestParam("token") String token,
                              @RequestParam("email") String email, Model model) {
        Account account = accountService.findAccountByEmail(email);
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return "account/email-verification";
        }
        if (!token.equals(account.getEmailToken())) {
            model.addAttribute("error", "wrong.token");
            return "account/email-verification";
        }
        account.verified();
        model.addAttribute("numberOfUsers", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return "account/email-verification";
    }
}
