package me.devstudy.account;

import me.devstudy.domain.Account;
import me.devstudy.utils.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 수정: 입력값 정상")
    @WithAccount("nickname")
    void updateProfile() throws Exception {
        String bio = "한줄 소개";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByNickname("nickname");
        assertEquals(bio, account.getProfile().getBio());
    }

    @Test
    @DisplayName("프로필 수정: 입력값 에러")
    @WithAccount("nickname")
    void updateProfileWithError() throws Exception {
        String bio = "35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러. 35자 넘으면 에러.";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profileDto"));
        Account account = accountRepository.findByNickname("nickname");
        assertNull(account.getProfile().getBio());
    }

    @Test
    @DisplayName("프로필 수정 폼")
    @WithAccount("nickname")
    void updateProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profileDto"));
    }

    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount("nickname")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 정상")
    @WithAccount("nickname")
    void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "12341234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        accountRepository.flush();

        Account account = accountRepository.findByNickname("nickname");

        assertTrue(bCryptPasswordEncoder.matches("12341234", account.getPassword()));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(불일치)")
    @WithAccount("nickname")
    void updatePasswordWithNotMatchedError() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "12341234")
                        .param("newPasswordConfirm", "12341234!!")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(길이)")
    @WithAccount("nickname")
    void updatePasswordWithLengthError() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword", "1234")
                        .param("newPasswordConfirm", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}