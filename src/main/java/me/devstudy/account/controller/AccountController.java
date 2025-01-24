package me.devstudy.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.controller.validator.SignupFormValidator;
import me.devstudy.account.domain.entity.Account;
import me.devstudy.account.domain.entity.Notification;
import me.devstudy.account.repository.AccountRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupFormValidator signupFormValidator;
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;

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

        signupFormValidator.validate(signupForm, errors);
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = Account.builder()
                .email(signupForm.getEmail())
                .password(signupForm.getPassword())
                .nickname(signupForm.getNickname())
                .notification(Notification.builder()
                        .createdByWeb(true)
                        .createdByEmail(true)
                        .registrationResultByEmailByWeb(true)
                        .build())
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateToken();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("DevStudy 회원가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s",
                newAccount.getEmailToken(), newAccount.getEmail()));
        javaMailSender.send(mailMessage);

        return "redirect:/";
    }
}
