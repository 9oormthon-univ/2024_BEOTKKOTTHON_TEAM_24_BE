package goorm.reinput.user.service;

import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.user.domain.User;
import goorm.reinput.folder.domain.Folder;
import goorm.reinput.user.domain.dto.UserSignupDto;
import goorm.reinput.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public void signUp(UserSignupDto userSignUpDto) {
        log.info("[UserService] signUp userEmail : {}", userSignUpDto.getUserEmail());
        User user = User.builder()
                .userEmail(userSignUpDto.getUserEmail())
                .userPassword(userSignUpDto.getUserPassword())
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
}
