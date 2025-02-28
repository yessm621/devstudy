package me.devstudy.tag.domain;

import jakarta.persistence.*;
import lombok.*;
import me.devstudy.account.domain.Account;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"account", "tag"})
@Builder
@Getter
public class AccountTag {

    @Id
    @GeneratedValue
    @Column(name = "account_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static AccountTag createAccountTag(Account account, Tag tag) {
        AccountTag accountTag = new AccountTag();
        accountTag.account = account;
        accountTag.tag = tag;
        return accountTag;
    }
}
