package me.devstudy.account.endpoint.controller;

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

        then(javaMailSender)
                .should()
                .send(any(SimpleMailMessage.class));
    }
}