package me.devstudy.zone;

import me.devstudy.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCityAndProvince(String cityName, String provinceName);
}
