package me.devstudy.study;

import me.devstudy.study.domain.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long> {
}
