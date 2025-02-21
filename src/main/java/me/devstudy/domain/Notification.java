package me.devstudy.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
@ToString
public class Notification {

    private boolean studyCreatedByEmail = false;
    private boolean studyCreatedByWeb = true;
    private boolean studyRegistrationResultByEmail = false;
    private boolean studyRegistrationResultByWeb = true;
    private boolean studyUpdatedByEmail = false;
    private boolean studyUpdatedByWeb = true;
}
