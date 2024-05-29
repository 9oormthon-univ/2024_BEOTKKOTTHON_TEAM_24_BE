package goorm.reinput.user.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.user.domain.dto.*;
import goorm.reinput.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }

    @Operation(summary = "회원가입", description = "유저가 회원가입할 때 사용하는 API")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(final @Valid @RequestBody UserSignupDto userSignupDto) {
        log.info("[UserController] signUp userEmail : {}", userSignupDto.getUserEmail());

        userService.signUp(userSignupDto);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @Operation(summary = "로그인", description = "유저가 로그인할 때 사용하는 API")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(final @Valid @RequestBody UserLoginDto userLoginDto) {
        log.info("[UserController] login userEmail : {}", userLoginDto.getUserEmail());
        return ResponseEntity.ok().body(userService.login(userLoginDto));
    }

    @Operation(summary = "토큰 재발급", description = "유저가 토큰을 재발급할 때 사용하는 API")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(final @RequestBody TokenRequest refreshToken) {
        return ResponseEntity.ok().body(userService.reissueAccessToken(refreshToken.getRefreshToken()));
    }

    @Operation(summary = "회원탈퇴", description = "유저가 회원탈퇴할 때 사용하는 API")
    @DeleteMapping("deactivate")
    public ResponseEntity<String> deactivateUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.deactivate(principalDetails.getUserId());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "유저가 로그아웃할 때 사용하는 API 현재는 아무런 작업을 하지 않음, 프론트에서 토큰 삭제 처리")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Operation(summary = "유저 정보 가져오기", description = "유저 정보(이름, 직업, 이메일)가 필요할 때 호출하는 API")
    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> getUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok().body(userService.getUserInfo(principalDetails.getUserId()));
    }
}
