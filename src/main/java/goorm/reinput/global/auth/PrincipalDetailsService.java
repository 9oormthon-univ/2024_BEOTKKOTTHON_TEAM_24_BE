package goorm.reinput.global.auth;

import goorm.reinput.user.domain.User;
import goorm.reinput.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrincipalDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     UserDetailsService 구현체
     user가 존재 할경우 PrincipalDetails로 반환
     */
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        //todo: customException 처리
        User userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다. : " + userId));

        // 로그 메시지는 예외 처리 이전에 기록될 수 없으므로, 여기서는 예외 발생 후에는 도달할 수 없습니다.
        log.info("[PrincipalDetailsService] userId : {}", userEntity.getUserId());
        return new PrincipalDetails(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username을 userId로 간주하고 처리하는 로직 구현
        // 실제 사용 시 적절한 타입 변환과 예외 처리 필요
        Long userId = Long.parseLong(username);
        User userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        log.info("[PrincipalDetailsService] userId : {}", userEntity.getUserId());
        return new PrincipalDetails(userEntity);
    }
}
