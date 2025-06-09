package academy.prog.julia.components;

import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.repos.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Component for generating certificates for users.
 * This class communicates with an external microservice to generate PDF certificates
 * and stores them in the database.
 */
@Component
public class CertificateGenerator {

    private static final Logger LOGGER = LogManager.getLogger(CertificateGenerator.class);

    private final RestTemplate restTemplate;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    /**
     * The URL of the microservice that generates the certificates.
     * This value is injected from the application.properties file.
     */
    @Value("${certificate.generator.url}")
    private String microserviceUrl;

    /**
     * Constructor for CertificateGenerator.
     *
     * @param restTemplate           RestTemplate instance for making HTTP requests.
     * @param certificateRepository  Repository for accessing certificates in the database.
     * @param userRepository         Repository for accessing users in the database.
     */
    public CertificateGenerator(
            RestTemplate restTemplate,
            CertificateRepository certificateRepository,
            UserRepository userRepository
    ) {
        this.restTemplate = restTemplate;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generates a certificate for the specified user and course, if it does not already exist.
     *
     * @param user       The user for whom the certificate is to be generated.
     * @param courseName The name of the course for which the certificate is being generated.
     * @param userId     The ID of the user.
     */
    @Transactional
    public void generateCertificate(User user, String courseName, Long userId) {

        if (user == null || courseName == null || userId == null) {
            LOGGER.error("Invalid input: user, courseName, and userId must not be null.");
            return;
        }

        Certificate existingCertificate = certificateRepository.findByGroupNameAndUserId(courseName, userId);
        if (existingCertificate != null) {
            LOGGER.info("Certificate already exists for courseName: {} and userId: {}", courseName, userId);
            return;
        }

        Map<String, String> requestData = new HashMap<>();
        String studentName = user.getName() + " " + user.getSurname();
        requestData.put("name", studentName);
        requestData.put("course", courseName);
        requestData.put("user_id", String.valueOf(userId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                microserviceUrl,
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            byte[] pdfBytes = responseEntity.getBody();

            String certificateId = generateCertificateId();

            Certificate certificate = new Certificate(pdfBytes);
            certificate.setUniqueId(certificateId);
            certificate.setGroupName(courseName);
            certificate.setFile(pdfBytes);

            User userWithId = userRepository.findById(userId).orElse(null);

            if (userWithId != null) {
                certificate.setUser(userWithId);
                certificateRepository.save(certificate);
            } else {
                LOGGER.info("User with id {} not found!", userId);
            }
        } else {
            LOGGER.error("Error getting certificate: {}", responseEntity.getStatusCode());
        }
    }

    /**
     * Generates a random unique identifier for a certificate.
     *
     * @return A unique identifier string for the certificate.
     */
    private String generateCertificateId() {
        String randomId = UUID.randomUUID().toString().replaceAll("-", "");
        if (randomId.length() > 32) {
            randomId = randomId.substring(0, 32);
        }

        return randomId;
    }
}