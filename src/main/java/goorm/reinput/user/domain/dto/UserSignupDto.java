package goorm.reinput.user.domain.dto;

import goorm.reinput.user.domain.Job;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignupDto {
    private String userEmail;
    private String userPassword;
    private String userName;
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
