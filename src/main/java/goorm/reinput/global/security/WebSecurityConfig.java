package goorm.reinput.global.security;

import goorm.reinput.global.security.jwt.JWTTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final JWTTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    public WebSecurityConfig(JWTTokenAuthenticationFilter jwtTokenAuthenticationFilter) {
        this.jwtTokenAuthenticationFilter = jwtTokenAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
        * csrf, cors, sessionManagement, authorizeHttpRequests, addFilterBefore, headers 설정
        csrf : csrf 설정을 비활성화
        cors : cors 설정을 기본값으로 설정
        sessionManagement : 세션 생성 정책을 STATELESS로 설정
        authorizeHttpRequests : 요청에 대한 권한 설정
        addFilterBefore : JWTTokenAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        headers : frameOptions 설정을 비활성화
         */
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests->
                        requests
                                .requestMatchers("/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs", "/api-docs/**", "/v3/api-docs/**", "/user/login", "/user/signup", "/user/healthcheck", "/insight/share/**", "/actuator", "/actuator/prometheus").permitAll()
                                // 로그인 및 회원가입 혹은 공개 API에 대한 접근 허용
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headerConfig ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                );
        return http.build();
    }
}
