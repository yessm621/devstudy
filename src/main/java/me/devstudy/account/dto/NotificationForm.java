package me.devstudy.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.devstudy.domain.Account;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationForm {

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyRegistrationResultByEmail;
    private boolean studyRegistrationResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    protected NotificationForm(Account account) {
        this.studyCreatedByEmail = account.getNotification().isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.getNotification().isStudyCreatedByWeb();
        this.studyRegistrationResultByEmail = account.getNotification().isStudyRegistrationResultByEmail();
        this.studyRegistrationResultByWeb = account.getNotification().isStudyRegistrationResultByWeb();
        this.studyUpdatedByEmail = account.getNotification().isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = account.getNotification().isStudyUpdatedByWeb();
    }

    public static NotificationForm from(Account account) {
        return new NotificationForm(account);
    }
}
