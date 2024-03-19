package goorm.reinput.global.auth;

import goorm.reinput.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        //UserDetails 구체화, override CustomImpl 로 나누고 UserId를 Interface 로 빼는 것이 좋을 수 있
        User userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("해당 사용자 : {}를 찾을 수 없습니다. : ", userId);
                    throw new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId);
                });
        if (userEntity != null) {
            log.info("[PrincipalDetailsService] userId : {}", userEntity.getUserId());
            return new PrincipalDetails(userEntity);
        }

        return null;
    }
}
