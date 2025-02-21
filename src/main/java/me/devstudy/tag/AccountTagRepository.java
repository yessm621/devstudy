package me.devstudy.tag;

import me.devstudy.domain.Account;
import me.devstudy.domain.AccountTag;
import me.devstudy.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {

    AccountTag findByAccountAndTag(Account account, Tag tag);

    List<AccountTag> findByAccount(Account account);
}
