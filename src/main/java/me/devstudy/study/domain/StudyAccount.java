package me.devstudy.study.domain;

import jakarta.persistence.*;
import lombok.*;
import me.devstudy.account.domain.Account;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class StudyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    private boolean isManager;

    public static StudyAccount createStudyManager(Account account, Study study) {
        StudyAccount studyAccount = new StudyAccount();
        studyAccount.account = account;
        studyAccount.study = study;
        studyAccount.isManager = true;
        return studyAccount;
    }
}
