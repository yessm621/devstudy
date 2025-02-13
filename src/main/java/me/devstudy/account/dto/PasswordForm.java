package me.devstudy.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @NotBlank
    @Length(min = 8, max = 50)
    private String newPassword;

    @NotBlank
    @Length(min = 8, max = 50)
    private String newPasswordConfirm;
}
