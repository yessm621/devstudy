package me.devstudy.study;

import me.devstudy.study.domain.StudyZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyZoneRepository extends JpaRepository<StudyZone, Long> {
}
