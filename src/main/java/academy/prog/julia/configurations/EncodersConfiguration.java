package academy.prog.julia.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for providing a custom {@link PasswordEncoder} bean.
 *
 * This class is specifically created to ensure consistent password encoding behavior across different environments,
 * particularly in production where the default {@link PasswordEncoder} may occasionally fail to work as expected.
 *
 * The configured encoder uses the BCrypt algorithm with a strength of 8, which provides a good balance between security
 * and performance.
 */
@Configuration
public class EncodersConfiguration {

    /**
     * Bean definition for the {@link PasswordEncoder} that uses BCrypt with a strength of 8.
     *
     * @return a {@link BCryptPasswordEncoder} instance with the specified strength.
     */
    @Bean
    public PasswordEncoder userPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

}
