package me.devstudy.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.dto.ProfileDto;
import me.devstudy.account.dto.SignupForm;
import me.devstudy.domain.Account;
import me.devstudy.domain.Notification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Account signup(SignupForm signupForm) {
        Account newAccount = saveNewAccount(signupForm);
        newAccount.generateToken();
        sendVerificationEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(SignupForm signupForm) {
        Account account = Account.builder()
                .email(signupForm.getEmail())
                .password(bCryptPasswordEncoder.encode(signupForm.getPassword()))
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

    public void sendVerificationEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("DevStudy 회원가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s",
                newAccount.getEmailToken(), newAccount.getEmail()));
        javaMailSender.send(mailMessage);
    }

    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public void login(Account account, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), account.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

        SecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.saveContext(SecurityContextHolder.getContext(), request, response);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = Optional.ofNullable(accountRepository.findByEmail(username))
                .orElse(accountRepository.findByNickname(username));
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    @Transactional
    public void emailVerified(Account account) {
        account.verified();
    }

    @Transactional
    public void updateProfile(Account account, ProfileDto profileDto) {
        account.updateProfile(profileDto);
        accountRepository.save(account);
    }

    @Transactional
    public void updatePassword(Account account, String newPassword) {
        account.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(account);
    }
}
