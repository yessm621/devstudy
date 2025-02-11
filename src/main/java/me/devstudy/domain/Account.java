package me.devstudy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
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
}
