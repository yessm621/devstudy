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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
}