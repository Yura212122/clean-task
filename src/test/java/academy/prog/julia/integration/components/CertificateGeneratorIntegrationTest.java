package academy.prog.julia.integration.components;

import academy.prog.julia.components.CertificateGenerator;
import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class CertificateGeneratorIntegrationTest {

    @MockBean
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private CertificateGenerator certificateGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("securePassword");
        testUser.setRegisterDate(LocalDateTime.now());
        testUser.setUniqueId("12345667788866");

        userRepository.save(testUser);
    }


    @Test
    @Transactional
    public void testGenerateCertificate_Success() {
        String courseName = "Course 101";
        byte[] mockPdfBytes = new byte[]{1, 2, 3};

        Certificate existingCertificate = new Certificate(mockPdfBytes);
        existingCertificate.setGroupName(courseName);
        existingCertificate.setUser(testUser);
        certificateRepository.save(existingCertificate);

        when(restTemplate
                .exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(mockPdfBytes))
        ;

        certificateGenerator.generateCertificate(testUser, courseName, testUser.getId());

        assertEquals(0, certificateRepository.count());
    }

    @Test
    @Transactional
    public void testGenerateCertificate_AlreadyExists() {
        String courseName = "Course 101";
        byte[] mockPdfBytes = new byte[]{1, 2, 3};

        Certificate existingCertificate = new Certificate(mockPdfBytes);
        existingCertificate.setGroupName(courseName);
        existingCertificate.setUser(testUser);

        when(certificateRepository.save(any(Certificate.class))).thenReturn(existingCertificate);
        when(certificateRepository.count()).thenReturn(1L);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(mockPdfBytes));

        certificateGenerator.generateCertificate(testUser, courseName, testUser.getId());

        assertEquals(1, certificateRepository.count(), "Certificate already exists");
    }

    @Test
    @Transactional
    public void testGenerateCertificate_NotExists() {
        String courseName = "Course 101";
        byte[] mockPdfBytes = new byte[]{1, 2, 3};

        Certificate existingCertificate = new Certificate(mockPdfBytes);
        existingCertificate.setGroupName(courseName);
        existingCertificate.setUser(testUser);
        certificateRepository.save(existingCertificate);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(mockPdfBytes));

        certificateGenerator.generateCertificate(testUser, courseName, testUser.getId());

        assertEquals(0, certificateRepository.count());
    }

    @Test
    @Transactional
    public void testGenerateCertificate_UserNotFound() {
        String courseName = "Course 101";

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(ResponseEntity.ok(new byte[]{1, 2, 3}));

        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        certificateGenerator.generateCertificate(nonExistentUser, courseName, 999L);

        assertEquals(0, certificateRepository.count());
    }

    @Test
    @Transactional
    public void testGenerateCertificate_ErrorResponse() {
        String courseName = "Course 101";

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(byte[].class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

        certificateGenerator.generateCertificate(testUser, courseName, testUser.getId());

        assertEquals(0, certificateRepository.count());
    }

}
