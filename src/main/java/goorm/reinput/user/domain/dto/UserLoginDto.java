package goorm.reinput.user.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginDto {
    @Email
    private String userEmail;
    private String userPassword;

    @Builder
    public UserLoginDto(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
}
