package me.devstudy.study;

import me.devstudy.study.domain.StudyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyAccountRepository extends JpaRepository<StudyAccount, Long> {
}
