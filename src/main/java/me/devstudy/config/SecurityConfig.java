package me.devstudy.config;

import lombok.RequiredArgsConstructor;
import me.devstudy.account.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/sign-up", "/login", "/check-email-token", "/email-login",
                                "/check-email-login", "/login-link", "/login-by-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/*").permitAll()
                        .requestMatchers("/node_modules/**", "/images/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(auth -> auth
                .loginPage("/login").permitAll());

        http.logout(auth -> auth
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));

        http.rememberMe(auth -> auth
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository()));

        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
