package goorm.reinput.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // TODO: Springboot 버전 업데이트에 기존에 사용했던 시큐리티 설정이 대거 변경됨. 추후 업데이트 필요
        return http
                .csrf().disable()
                .formLogin().disable()
                .build();
    }
}
