package me.devstudy.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.devstudy.account.dto.*;
import me.devstudy.account.validator.NicknameFormValidator;
import me.devstudy.account.validator.PasswordFormValidator;
import me.devstudy.account.domain.Account;
import me.devstudy.tag.TagService;
import me.devstudy.zone.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final AccountService accountService;
    private final TagService tagService;
    private final ZoneService zoneService;
    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping("/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("profileDto", ProfileDto.from(account));
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute ProfileDto profileDto,
                                Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }
        accountService.updateProfile(account, profileDto);
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/password")
    public String updatePassword(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm,
                                 Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/password";
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/settings/password";
    }

    @GetMapping("/notification")
    public String notificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("notificationForm", NotificationForm.from(account));
        return "settings/notification";
    }

    @PostMapping("/notification")
    public String updateNotification(@CurrentUser Account account, @Valid NotificationForm notificationForm,
                                     Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/notification";
        }
        accountService.updateNotification(account, notificationForm);
        attributes.addFlashAttribute("message", "알림 설정을 수정하였습니다.");
        return "redirect:/settings/notification";
    }

    @GetMapping("/account")
    public String nicknameForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute("nicknameForm", new NicknameForm(account.getNickname()));
        return "settings/account";
    }

    @PostMapping("/account")
    public String updateNickname(@CurrentUser Account account, @Valid NicknameForm nicknameForm,
                                 Errors errors, Model model, RedirectAttributes attributes,
                                 HttpServletRequest request, HttpServletResponse response) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/account";
        }
        accountService.updateNickname(account, nicknameForm.getNickname(), request, response);
        attributes.addFlashAttribute("message", "닉네임을 수정하였습니다.");
        return "redirect:/settings/account";
    }

    @GetMapping("/tags")
    public String getTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
        List<String> tags = tagService.getTags(account);
        model.addAttribute("tags", tags);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(tagService.whitelist()));
        model.addAttribute(account);
        return "settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseStatus(HttpStatus.OK)
    public void addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        tagService.addTag(account.getEmail(), title);
    }

    @PostMapping("/tags/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        tagService.removeTag(account, title);
    }

    @GetMapping("/zones")
    public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        List<String> zones = zoneService.getZones(account);
        List<String> allZones = zoneService.getAllZones();
        model.addAttribute("zones", zones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseStatus(HttpStatus.OK)
    public void addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        zoneService.addZone(account, zoneForm);
    }

    @PostMapping("/zones/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        zoneService.removeZone(account, zoneForm);
    }
}
