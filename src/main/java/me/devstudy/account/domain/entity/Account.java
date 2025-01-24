package me.devstudy.account.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;
    private boolean isValid;
    private String emailToken;

    @Embedded
    private Profile profile;

    @Embedded
    private Notification notification;

    public void generateToken() {
        this.emailToken = UUID.randomUUID().toString();
    }
}
