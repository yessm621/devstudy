package me.devstudy.account.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.devstudy.account.domain.Account;
import me.devstudy.account.domain.Profile;
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
        Profile profile = account.getProfile();
        this.bio = profile != null ? profile.getBio() : null;
        this.url = profile != null ? profile.getUrl() : null;
        this.job = profile != null ? profile.getJob() : null;
        this.location = profile != null ? profile.getLocation() : null;
        this.image = profile != null ? profile.getImage() : null;
    }
}
