package me.devstudy.study;

import lombok.RequiredArgsConstructor;
import me.devstudy.domain.Account;
import me.devstudy.study.domain.Study;
import me.devstudy.study.domain.StudyAccount;
import me.devstudy.study.dto.StudyForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyAccountRepository studyAccountRepository;

    @Transactional
    public Study createNewStudy(StudyForm studyForm, Account account) {
        Study study = Study.createStudy(studyForm);
        StudyAccount manager = StudyAccount.createStudyManager(account, study);
        studyRepository.save(study);
        studyAccountRepository.save(manager);
        return study;
    }
}
