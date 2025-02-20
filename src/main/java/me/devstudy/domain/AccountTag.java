package me.devstudy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"account", "tag"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
