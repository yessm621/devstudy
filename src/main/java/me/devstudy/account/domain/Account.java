package me.devstudy.account.domain;

import jakarta.persistence.*;
import lombok.*;
import me.devstudy.account.dto.NotificationForm;
import me.devstudy.account.dto.ProfileDto;
import me.devstudy.tag.domain.AccountTag;
import me.devstudy.zone.domain.AccountZone;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
@ToString(exclude = "accountTags")
public class Account extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;
    private boolean isValid;
    private String emailToken;
    private LocalDateTime joinedAt;
    private LocalDateTime emailTokenGeneratedAt;

    @Embedded
    private Profile profile;

    @Embedded
    private Notification notification;

    @OneToMany(mappedBy = "account")
    private List<AccountTag> accountTags = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private Set<AccountZone> accountZones = new HashSet<>();

    public static Account with(String email, String nickname, String password) {
        Account account = new Account();
        account.email = email;
        account.nickname = nickname;
        account.password = password;
        return account;
    }

    public void generateToken() {
        this.emailToken = UUID.randomUUID().toString();
        this.emailTokenGeneratedAt = LocalDateTime.now();
    }

    public void verified() {
        this.isValid = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean enableToSendEmail() {
        return this.emailTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @PostLoad
    private void init() {
        if (profile == null) {
            profile = new Profile();
        }

        if (notification == null) {
            notification = new Notification();
        }
    }

    public void updateProfile(ProfileDto profileDto) {
        if (this.profile == null) {
            this.profile = new Profile();
        }
        this.profile.setBio(profileDto.getBio());
        this.profile.setUrl(profileDto.getUrl());
        this.profile.setJob(profileDto.getJob());
        this.profile.setLocation(profileDto.getLocation());
        this.profile.setImage(profileDto.getImage());
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNotification(NotificationForm notificationForm) {
        this.notification.setStudyCreatedByEmail(notificationForm.isStudyCreatedByEmail());
        this.notification.setStudyCreatedByWeb(notificationForm.isStudyCreatedByWeb());
        this.notification.setStudyUpdatedByEmail(notificationForm.isStudyUpdatedByEmail());
        this.notification.setStudyUpdatedByWeb(notificationForm.isStudyUpdatedByWeb());
        this.notification.setStudyRegistrationResultByEmail(notificationForm.isStudyRegistrationResultByEmail());
        this.notification.setStudyRegistrationResultByWeb(notificationForm.isStudyRegistrationResultByWeb());
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isValid(String token) {
        return this.emailToken.equals(token);
    }
}
