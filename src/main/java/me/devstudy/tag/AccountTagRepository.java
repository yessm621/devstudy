package me.devstudy.tag;

import me.devstudy.account.domain.Account;
import me.devstudy.tag.domain.AccountTag;
import me.devstudy.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTagRepository extends JpaRepository<AccountTag, Long> {

    AccountTag findByAccountAndTag(Account account, Tag tag);

    List<AccountTag> findByAccount(Account account);
}
