package me.devstudy.tag;

import lombok.RequiredArgsConstructor;
import me.devstudy.account.AccountRepository;
import me.devstudy.domain.Account;
import me.devstudy.domain.AccountTag;
import me.devstudy.domain.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final AccountTagRepository accountTagRepository;

    public List<String> getTags(Account account) {
        return accountTagRepository.findByAccount(account)
                .stream()
                .map(AccountTag::getTag)
                .map(Tag::getTitle)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTag(String email, String title) {
        Tag tag = tagRepository.findByTitle(title)
                .orElseGet(() -> tagRepository.save(Tag.builder()
                        .title(title)
                        .build()));

        Account account = accountRepository.findByEmail(email);
        AccountTag accountTag = AccountTag.createAccountTag(account, tag);
        if (!account.getAccountTags().contains(accountTag)) {
            accountTagRepository.save(accountTag);
        }
    }

    @Transactional
    public void removeTag(Account account, String title) {
        Tag tag = tagRepository.findByTitle(title)
                .orElseThrow(IllegalArgumentException::new);
        AccountTag accountTag = accountTagRepository.findByAccountAndTag(account, tag);
        accountTagRepository.delete(accountTag);
    }
}
