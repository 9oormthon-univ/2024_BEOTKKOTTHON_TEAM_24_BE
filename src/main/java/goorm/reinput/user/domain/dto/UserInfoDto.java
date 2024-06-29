package goorm.reinput.user.domain.dto;

import goorm.reinput.user.domain.Job;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private String userName;
    @Email
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private Job job;
}
