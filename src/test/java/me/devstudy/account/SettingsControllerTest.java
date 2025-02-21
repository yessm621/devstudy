package me.devstudy.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.devstudy.account.dto.TagForm;
import me.devstudy.domain.Account;
import me.devstudy.tag.TagRepository;
import me.devstudy.tag.TagService;
import me.devstudy.utils.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

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
        assertNull(account.getProfile());
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

    @Test
    @DisplayName("알림 설정 수정 폼")
    @WithAccount("nickname")
    void updateNotificationForm() throws Exception {
        mockMvc.perform(get("/settings/notification"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/notification"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notificationForm"));
    }

    @Test
    @DisplayName("알림 설정 수정: 입력값 정상")
    @WithAccount("nickname")
    void updateNotification() throws Exception {
        mockMvc.perform(post("/settings/notification")
                        .param("studyCreatedByEmail", "true")
                        .param("studyCreatedByWeb", "true")
                        .param("studyRegistrationResultByEmail", "true")
                        .param("studyRegistrationResultByWeb", "true")
                        .param("studyUpdatedByEmail", "true")
                        .param("studyUpdatedByWeb", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notification"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("nickname");
        assertTrue(account.getNotification().isStudyCreatedByEmail());
        assertTrue(account.getNotification().isStudyCreatedByWeb());
        assertTrue(account.getNotification().isStudyRegistrationResultByEmail());
        assertTrue(account.getNotification().isStudyRegistrationResultByWeb());
        assertTrue(account.getNotification().isStudyUpdatedByEmail());
        assertTrue(account.getNotification().isStudyUpdatedByWeb());
    }

    @Test
    @DisplayName("닉네임 수정 폼")
    @WithAccount("nickname")
    void updateNicknameForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @Test
    @DisplayName("닉네임 수정: 입력값 정상")
    @WithAccount("nickname")
    void updateNickname() throws Exception {
        String newNickname = "nickname2";
        mockMvc.perform(post("/settings/account")
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname(newNickname);
        assertEquals(newNickname, account.getNickname());
    }

    @Test
    @DisplayName("닉네임 수정: 에러(길이)")
    @WithAccount("nickname")
    void updateNicknameWithShortNickname() throws Exception {
        String newNickname = "n";
        mockMvc.perform(post("/settings/account")
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @Test
    @DisplayName("닉네임 수정: 에러(중복)")
    @WithAccount("nickname")
    void updateNicknameWithDuplicatedNickname() throws Exception {
        String newNickname = "nickname";
        mockMvc.perform(post("/settings/account")
                        .param("nickname", newNickname)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @DisplayName("태그 수정 폼")
    @WithAccount("nickname")
    void getTags() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @Test
    @DisplayName("태그 추가")
    @WithAccount("nickname")
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("tagTitle");
        mockMvc.perform(post("/settings/tags/add")
                        .param("tagTitle", tagForm.getTagTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("태그 삭제")
    @WithAccount("nickname")
    void removeTag() throws Exception {
        String tagTitle = "tagTitle";
        Account account = accountRepository.findByNickname("nickname");
        tagService.addTag(account.getEmail(), tagTitle);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("tagTitle");

        mockMvc.perform(post("/settings/tags/remove")
                        .param("tagTitle", tagTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}