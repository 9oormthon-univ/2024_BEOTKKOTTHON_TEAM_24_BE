package goorm.reinput.user.controller;

import goorm.reinput.user.domain.dto.UserSignupDto;
import goorm.reinput.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(final @Valid @RequestBody UserSignupDto userSignupDto) {
        log.info("[UserController] signUp userEmail : {}", userSignupDto.getUserEmail());

        userService.signUp(userSignupDto);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

}
