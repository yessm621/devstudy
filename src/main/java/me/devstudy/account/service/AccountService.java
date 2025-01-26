package me.devstudy.account.service;

import lombok.RequiredArgsConstructor;
import me.devstudy.account.controller.SignupForm;
import me.devstudy.account.domain.entity.Account;
import me.devstudy.account.domain.entity.Notification;
import me.devstudy.account.repository.AccountRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void signup(SignupForm signupForm) {
        Account newAccount = saveNewAccount(signupForm);
        newAccount.generateToken();
        sendVerificationEmail(newAccount);
    }

    private Account saveNewAccount(SignupForm signupForm) {
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
        return newAccount;
    }

    private void sendVerificationEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("DevStudy 회원가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s",
                newAccount.getEmailToken(), newAccount.getEmail()));
        javaMailSender.send(mailMessage);
    }
}
