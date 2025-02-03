package me.devstudy.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
public class Notification {

    private boolean createdByEmail;
    private boolean createdByWeb;
    private boolean registrationResultByEmailByEmail;
    private boolean registrationResultByEmailByWeb;
    private boolean updatedByEmail;
    private boolean updatedByWeb;
}
