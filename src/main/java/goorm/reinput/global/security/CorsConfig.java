package goorm.reinput.global.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // TODO: cors 설정. dev, prod 주소 추가 예정
//    @Value("${cors.origin.development}")
//    private String developmentOrigin;
//
//    @Value("${cors.origin.production}")
//    private String productionOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE")
                .allowCredentials(true)
                .allowedOrigins("http://localhost:3000","https://2024-beotkkotthon-team-24-fe.vercel.app", "https://reinput.info", "https://www.reinput.info");
    }
}