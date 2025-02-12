package me.devstudy.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.dto.ProfileDto;
import me.devstudy.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final AccountService accountService;

    @GetMapping("/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("profileDto", ProfileDto.from(account));
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute ProfileDto profileDto, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }
        accountService.updateProfile(account, profileDto);
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:/settings/profile";
    }
}
