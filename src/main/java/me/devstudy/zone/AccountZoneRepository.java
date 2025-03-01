package me.devstudy.zone;

import me.devstudy.account.domain.Account;
import me.devstudy.zone.domain.AccountZone;
import me.devstudy.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AccountZoneRepository extends JpaRepository<AccountZone, Long> {

    Set<AccountZone> findByAccount(Account account);

    Optional<AccountZone> findByAccountAndZone(Account account, Zone zone);
}
