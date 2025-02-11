package me.devstudy.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.devstudy.domain.Account;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileDto {

    private String bio;
    private String url;
    private String job;
    private String location;

    public static ProfileDto from(Account account) {
        return new ProfileDto(account);
    }

    protected ProfileDto(Account account) {
        this.bio = account.getProfile().getBio();
        this.url = account.getProfile().getUrl();
        this.job = account.getProfile().getJob();
        this.location = account.getProfile().getLocation();
    }
}
