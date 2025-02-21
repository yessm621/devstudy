package me.devstudy.utils;

import me.devstudy.account.AccountRepository;
import me.devstudy.account.AccountService;
import me.devstudy.account.dto.SignupForm;
import me.devstudy.domain.Account;
import me.devstudy.domain.Notification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public WithAccountSecurityContextFactory(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        String nickname = annotation.value();

        SignupForm form = new SignupForm();
        form.setNickname(nickname);
        form.setEmail(nickname + "@gmail.com");
        form.setPassword("1234asdf");
        accountService.signup(form);

        Notification notification = new Notification();
        Account account = accountRepository.findByNickname("nickname");
        account.setNotification(notification);
        accountRepository.save(account);

        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
