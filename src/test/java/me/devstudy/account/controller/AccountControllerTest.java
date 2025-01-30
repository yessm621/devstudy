package me.devstudy.account.controller;

import me.devstudy.account.domain.entity.Account;
import me.devstudy.account.domain.entity.Notification;
import me.devstudy.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @DisplayName("회원 가입 화면 진입 확인")
    void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원가입 처리: 입력값 오류")
    void signupFormWithError() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "nickname")
                        .param("email", "email@gmail")
                        .param("password", "1234!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원가입 처리: 입력값 정상")
    void signupFormSubmit() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "nickname")
                        .param("email", "nohsm621@gmail.com")
                        .param("password", "1234!@#$qwer")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("nohsm621@gmail.com"));

        Account account = accountRepository.findByEmail("nohsm621@gmail.com");
        assertNotEquals(account.getPassword(), "1234!@#$qwer");

        then(javaMailSender)
                .should()
                .send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증 메일 확인: 잘못된 링크")
    void verifyEmailWithWrongLink() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "token")
                        .param("email", "email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("인증 메일 확인: 유효한 링크")
    void verifyEmail() throws Exception {
        Account account = Account.builder()
                .email("nohsm621@gmail.com")
                .password("1234!@#$qwer")
                .nickname("nickname")
                .notification(Notification.builder()
                        .createdByWeb(true)
                        .updatedByWeb(true)
                        .registrationResultByEmailByWeb(true)
                        .build())
                .build();

        Account newAccount = accountRepository.save(account);
        System.out.println("newAccount = " + newAccount);
        newAccount.generateToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUsers", "nickname"));
    }
}