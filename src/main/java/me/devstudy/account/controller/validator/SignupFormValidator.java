package me.devstudy.account.controller.validator;

import lombok.RequiredArgsConstructor;
import me.devstudy.account.controller.SignupForm;
import me.devstudy.account.repository.AccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm) target;
        if (accountRepository.existsByEmail(signupForm.getEmail())) {
            errors.rejectValue("email", "invalid mail",
                    new Object[]{signupForm.getEmail()}, "이미 사용 중인 이메일 입니다.");
        }

        if (accountRepository.existsByNickname(signupForm.getNickname())) {
            errors.rejectValue("nickname", " invalid nickname",
                    new Object[]{signupForm.getNickname()}, "이미 사용 중인 닉네임 입니다.");
        }
    }
}
