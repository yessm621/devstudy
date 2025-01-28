package me.devstudy.account.repository;

import me.devstudy.account.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);
}
