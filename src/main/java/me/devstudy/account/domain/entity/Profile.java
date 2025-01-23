package me.devstudy.account.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.devstudy.account.domain.support.ListStringConverter;

import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
public class Profile {

    private String bio;

    @Convert(converter = ListStringConverter.class)
    private List<String> urls;
    private String location;
    private String company;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;
}
