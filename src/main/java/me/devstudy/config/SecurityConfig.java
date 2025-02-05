package me.devstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/sign-up", "/login",
                                "/check-email-token", "/email-login", "/check-email-login", "/login-link").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/*").permitAll()
                        .requestMatchers("/node_modules/**", "/images/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(auth -> auth
                .loginPage("/login").permitAll());

        http.logout(auth -> auth
                .logoutUrl("/logout")
                .logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
