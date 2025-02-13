package me.devstudy.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.devstudy.domain.Account;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileDto {

    @Length(max = 35)
    private String bio;
    @Length(max = 50)
    private String url;
    @Length(max = 50)
    private String job;
    @Length(max = 50)
    private String location;
    private String image;

    public static ProfileDto from(Account account) {
        return new ProfileDto(account);
    }

    protected ProfileDto(Account account) {
        this.bio = account.getProfile().getBio();
        this.url = account.getProfile().getUrl();
        this.job = account.getProfile().getJob();
        this.location = account.getProfile().getLocation();
        this.image = account.getProfile().getImage();
    }
}
