package me.devstudy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_zone_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public static AccountZone createAccountZone(Account account, Zone zone) {
        AccountZone accountZone = new AccountZone();
        accountZone.account = account;
        accountZone.zone = zone;
        return accountZone;
    }
}
