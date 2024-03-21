package goorm.reinput.user.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.user.domain.dto.TokenRequest;
import goorm.reinput.user.domain.dto.TokenResponseDto;
import goorm.reinput.user.domain.dto.UserLoginDto;
import goorm.reinput.user.domain.dto.UserSignupDto;
import goorm.reinput.user.service.UserService;
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
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(final @Valid @RequestBody UserSignupDto userSignupDto) {
        log.info("[UserController] signUp userEmail : {}", userSignupDto.getUserEmail());

        userService.signUp(userSignupDto);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(final @Valid @RequestBody UserLoginDto userLoginDto){
        log.info("[UserController] login userEmail : {}", userLoginDto.getUserEmail());
        return ResponseEntity.ok().body(userService.login(userLoginDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(final @RequestBody TokenRequest refreshToken) {
        return ResponseEntity.ok().body(userService.reissueAccessToken(refreshToken.getRefreshToken()));
    }

    @DeleteMapping("deactivate")
    public ResponseEntity<String> deactivateUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.deactivate(principalDetails.getUserId());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
