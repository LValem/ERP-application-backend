package ee.taltech.iti03022024project.security;

import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;


@Configuration
@EnableScheduling
public class ApplicationConfiguration {
    @Bean
    public SecretKey jwtkey() {
        return Jwts.SIG.HS256.key().build();
    }
}
