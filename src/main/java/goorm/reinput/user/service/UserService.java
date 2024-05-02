package goorm.reinput.user.service;

import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.global.security.jwt.TokenProvider;
import goorm.reinput.user.domain.User;
import goorm.reinput.folder.domain.Folder;
import goorm.reinput.user.domain.dto.TokenResponseDto;
import goorm.reinput.user.domain.dto.UserLoginDto;
import goorm.reinput.user.domain.dto.UserSignupDto;
import goorm.reinput.user.exception.CustomUserException;
import goorm.reinput.user.exception.UserErrorCode;
import goorm.reinput.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public void signUp(UserSignupDto userSignUpDto) {
        log.info("[UserService] signUp userEmail : {}", userSignUpDto.getUserEmail());
        //user 저장
        User user = User.builder()
                .userEmail(userSignUpDto.getUserEmail())
                .userPassword(bCryptPasswordEncoder.encode(userSignUpDto.getUserPassword()))
                .userName(userSignUpDto.getUserName())
                .job(userSignUpDto.getJob())
                .build();
        userRepository.save(user);

        //topic list에서 folder 생성
        userSignUpDto.getTopicList().forEach(topic -> {
            Folder folder = Folder.builder()
                    .folderName(topic)
                    .user(user)
                    .folderColor(FolderColor.BLUE)
                    .build();
            folderRepository.save(folder);
        });
    }

    public TokenResponseDto login(UserLoginDto userLoginDto){
        log.info("[UserService] login userEmail : {}", userLoginDto.getUserEmail());
        //todo : login useremail custom exception
        User user = userRepository.findByUserEmail(userLoginDto.getUserEmail())
                .orElseThrow(() -> new CustomUserException(UserErrorCode.USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(userLoginDto.getUserPassword(), user.getUserPassword())) {
            log.warn("[UserService] login password not match userEmail : {}", userLoginDto.getUserEmail());
            throw new CustomUserException(UserErrorCode.USER_PASSWORD_NOT_MATCH);
        }

        return TokenResponseDto.builder()
                .accessToken(tokenProvider.generateAccessToken(user.getUserId()))
                .refreshToken(tokenProvider.generateRefreshToken(user.getUserId()))
                .build();
    }

    public TokenResponseDto reissueAccessToken(String refreshToken) {
        log.info("[UserService] reissueAccessToken refreshToken");
        String userId = tokenProvider.getUserIdByToken(refreshToken);
        return TokenResponseDto.builder()
                .accessToken(tokenProvider.generateAccessToken(Long.parseLong(userId)))
                .refreshToken(refreshToken)
                .build();
    }

    public void deactivate(Long userId) {
        log.info("[UserService] delete userId : {}", userId);
        userRepository.deactivateUserByUserId(userId);
    }

}
