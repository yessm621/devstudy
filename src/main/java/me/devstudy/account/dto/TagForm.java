package me.devstudy.account.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TagForm {

    @Pattern(regexp = "^.{0,20}$")
    private String tagTitle;
}
