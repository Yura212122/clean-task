package academy.prog.julia.integration.configurations;

import academy.prog.julia.configurations.MailSendConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class MailSendConfigurationTest {

    @Autowired
    private MailSendConfiguration mailSendConfiguration;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.host}")
    private String originalHost;

    @Value("${spring.mail.port}")
    private int originalPort;

    @Value("${spring.mail.username}")
    private String originalUsername;

    @Value("${spring.mail.password}")
    private String originalPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String originalAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String originalStarttls;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String originalProtocol;

    @Value("${spring.mail.properties.mail.debug}")
    private String originalDebug;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String originalSslTrustHost;


    @Test
    public void testJavaMailSender_shouldBeConfiguredCorrectly() {
        assertThat(javaMailSender).isInstanceOf(JavaMailSenderImpl.class);

        JavaMailSenderImpl sender = (JavaMailSenderImpl) javaMailSender;

        // Verify the host and port
        assertThat(sender.getHost()).isEqualTo(originalHost);
        assertThat(sender.getPort()).isEqualTo(originalPort);

        // Verify authentication credentials
        assertThat(sender.getUsername()).isEqualTo(originalUsername);
        assertThat(sender.getPassword()).isEqualTo(originalPassword);

        // Verify mail properties
        Properties properties = sender.getJavaMailProperties();
        assertThat(properties.getProperty("mail.transport.protocol")).isEqualTo(originalProtocol);
        assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo(originalAuth);
        assertThat(properties.getProperty("mail.smtp.starttls.enable")).isEqualTo(originalStarttls);
        assertThat(properties.getProperty("mail.debug")).isEqualTo(originalDebug);
        assertThat(properties.getProperty("mail.smtp.ssl.trust")).isEqualTo(originalSslTrustHost);
    }

    @Test
    public void testMissingHost_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setHost(null);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mail host must be provided")
        ;
    }

    @Test
    public void testInvalidPort_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setPort(-1);
        mailSendConfiguration.setHost(originalHost);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid port number: -1")
        ;
    }

    @Test
    public void testMissingUsername_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setUsername(null);
        mailSendConfiguration.setHost(originalHost);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mail username must be provided")
        ;
    }

    @Test
    public void testMissingPassword_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setPassword(null);
        mailSendConfiguration.setHost(originalHost);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mail password must be provided")
        ;
    }

    @Test
    public void testMissingAuth_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setAuth(null);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SMTP authentication must be enabled (true/false)")
        ;
    }

    @Test
    public void testMissingStarttls_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setStarttlsEnable(null);
        mailSendConfiguration.setHost(originalHost);
        mailSendConfiguration.setPassword(originalPassword);
        mailSendConfiguration.setAuth(originalAuth);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SMTP StartTLS must be enabled (true/false)")
        ;
    }

    @Test
    public void testMissingProtocol_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setProtocol(null);
        mailSendConfiguration.setHost(originalHost);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SMTP authentication must be enabled (true/false)")
        ;
    }

    @Test
    public void testMissingSslTrustHost_shouldThrowIllegalArgumentException() {
        mailSendConfiguration.setSslTrustHost(null);
        mailSendConfiguration.setHost(originalHost);
        mailSendConfiguration.setPassword(originalPassword);
        mailSendConfiguration.setProtocol(originalProtocol);
        mailSendConfiguration.setStarttlsEnable(originalStarttls);

        assertThatThrownBy(mailSendConfiguration::validateMailProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SSL trust host must be specified if using SSL")
        ;
    }

}
