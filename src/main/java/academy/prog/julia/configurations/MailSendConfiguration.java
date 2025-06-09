package academy.prog.julia.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * Configuration class for setting up the email sending functionality using JavaMailSender.
 * The configuration values are fetched from the application's properties file.
 */
@Configuration
public class MailSendConfiguration {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.mail.debug}")
    private String debug;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String sslTrustHost;

    /**
     * Bean for configuring and returning an instance of JavaMailSender.
     * This sender will be used for sending emails from the application.
     *
     * @return JavaMailSender instance with configured properties
     * @throws IllegalArgumentException if required properties are missing or invalid
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        validateMailProperties();

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        // Configure mail properties
        Properties javaMailProperties = javaMailSender.getJavaMailProperties();
        javaMailProperties.setProperty("mail.transport.protocol", protocol);
        javaMailProperties.setProperty("mail.smtp.auth", auth);
        javaMailProperties.setProperty("mail.smtp.starttls.enable", starttlsEnable);
        javaMailProperties.setProperty("mail.debug", debug);
        javaMailProperties.setProperty("mail.smtp.ssl.trust", sslTrustHost);

        return javaMailSender;
    }

    /**
     * Validate the mail properties to ensure that required values are provided
     * and are in a valid format.
     */
    public void validateMailProperties() {
        if (Objects.isNull(host) || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Mail host must be provided");
        }

        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port number: " + port);
        }

        if (Objects.isNull(username) || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Mail username must be provided");
        }

        if (Objects.isNull(password) || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mail password must be provided");
        }

        if (Objects.isNull(auth) || auth.trim().isEmpty()) {
            throw new IllegalArgumentException("SMTP authentication must be enabled (true/false)");
        }

        if (Objects.isNull(starttlsEnable) || starttlsEnable.trim().isEmpty()) {
            throw new IllegalArgumentException("SMTP StartTLS must be enabled (true/false)");
        }

        if (Objects.isNull(protocol) || protocol.trim().isEmpty()) {
            throw new IllegalArgumentException("Mail transport protocol must be specified");
        }

        if (Objects.isNull(sslTrustHost) || sslTrustHost.trim().isEmpty()) {
            throw new IllegalArgumentException("SSL trust host must be specified if using SSL");
        }
    }

    /**
     * Setters for test cases.
     */
    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setStarttlsEnable(String starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSslTrustHost(String sslTrustHost) {
        this.sslTrustHost = sslTrustHost;
    }

}
