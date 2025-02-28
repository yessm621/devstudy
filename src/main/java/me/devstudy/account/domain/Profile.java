package me.devstudy.account.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
@ToString
public class Profile {

    private String bio;
    private String url;
    private String job;
    private String location;
    private String company;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;
}
