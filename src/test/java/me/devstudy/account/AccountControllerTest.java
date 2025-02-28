package me.devstudy.account;

import me.devstudy.account.dto.SignupForm;
import me.devstudy.account.domain.Account;
import me.devstudy.account.domain.Notification;
import me.devstudy.mail.EmailMessage;
import me.devstudy.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @MockitoBean
    EmailService emailService;

    @Test
    @DisplayName("회원 가입 화면 진입 확인")
    void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("nickname"));

        assertTrue(accountRepository.existsByEmail("nohsm621@gmail.com"));

        Account account = accountRepository.findByEmail("nohsm621@gmail.com");
        assertNotEquals(account.getPassword(), "1234!@#$qwer");

        then(emailService)
                .should()
                .sendEmail(any(EmailMessage.class));
    }

    @Test
    @DisplayName("인증 메일 확인: 잘못된 링크")
    void verifyEmailWithWrongLink() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "token")
                        .param("email", "email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인: 유효한 링크")
    void verifyEmail() throws Exception {
        Account account = Account.builder()
                .email("nohsm621@gmail.com")
                .password("1234!@#$qwer")
                .nickname("nickname")
                .notification(Notification.builder()
                        .studyCreatedByWeb(true)
                        .studyUpdatedByWeb(true)
                        .studyRegistrationResultByWeb(true)
                        .build())
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailToken())
                        .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUsers", "nickname"))
                .andExpect(authenticated().withUsername("nickname"));
    }

    @Test
    @DisplayName("이메일로 로그인: 성공")
    void loginWithEmail() throws Exception {
        signup();
        mockMvc.perform(post("/login")
                        .param("username", "nohsm621@gmail.com")
                        .param("password", "1234!@#$qwer")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("nickname"));
    }

    @Test
    @DisplayName("닉네임으로 로그인: 성공")
    void loginWithNickname() throws Exception {
        signup();
        mockMvc.perform(post("/login")
                        .param("username", "nickname")
                        .param("password", "1234!@#$qwer")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("nickname"));
    }

    @Test
    @DisplayName("로그아웃: 성공")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    private void signup() {
        SignupForm signupForm = new SignupForm();
        signupForm.setEmail("nohsm621@gmail.com");
        signupForm.setNickname("nickname");
        signupForm.setPassword("1234!@#$qwer");
        accountService.signup(signupForm);
    }
}