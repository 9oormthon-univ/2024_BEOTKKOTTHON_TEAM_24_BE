package goorm.reinput.user.domain.dto;

import goorm.reinput.user.domain.Job;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignupDto {
    @NotNull
    @Email
    private String userEmail;
    @NotNull
    private String userPassword;
    @NotNull
    private String userName;
    @NotNull
    private Job job;

    List<String> topicList;

    @Builder
    public UserSignupDto(String userEmail, String password, String userName, Job job, List<String> topicList) {
        this.userEmail = userEmail;
        this.userPassword = password;
        this.userName = userName;
        this.job = job;
        this.topicList = topicList;
    }
}
