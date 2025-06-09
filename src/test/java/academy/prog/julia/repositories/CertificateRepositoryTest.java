package academy.prog.julia.repositories;

import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.CertificateTask;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.repos.CertificateTaskRepository;
import academy.prog.julia.repos.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CertificateRepositoryTest {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private Certificate certificate1;
    private Certificate certificate2;

    @BeforeEach
    void setUp() {
        // Create and save a user for use in tests
        user = new User();
        user.setName("Name");
        user.setSurname("Surname");
        user.setEmail("prosto@example.com");
        user.setPhone("1234567890");
        user.setActive(true);
        user.setPassword("Password");
        user.setRegisterDate(LocalDateTime.now());
        user.setUniqueId("uniqueId");
        userRepository.save(user);

        // Create and save two certificates associated with the user
        certificate1 = new Certificate();
        certificate1.setUniqueId("uniqueId1");
        certificate1.setUser(user);
        certificate1.setFile("Certificate 1".getBytes());
        certificate1.setGroupName("group1");
        certificateRepository.save(certificate1);

        certificate2 = new Certificate();
        certificate2.setUniqueId("uniqueId2");
        certificate2.setUser(user);
        certificate2.setGroupName("group2");
        certificate2.setFile("Certificate 2".getBytes());
        certificateRepository.save(certificate2);
    }

    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        certificateRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUniqueId() {
        // Test finding a certificate by its unique ID
        Certificate test1 = certificateRepository.findByUniqueId(certificate1.getUniqueId());
        assertEquals(certificate1, test1);

        // Test for a non-existing unique ID, expecting null
        Certificate test2 = certificateRepository.findByUniqueId("NonExistingUniqueId");
        assertNull(test2);
    }

    @Test
    void findByUser() {
        // Test finding certificates associated with a specific user
        List<Certificate> test1 = certificateRepository.findByUser(user);
        assertAll("Testing list",
                () -> assertThat(test1).hasSize(2),
                () -> assertThat(test1).contains(certificate1, certificate2));

        // Test finding certificates with a null user, expecting all certificates to have a null user
        List<Certificate> test2 = certificateRepository.findByUser(null);
        assertTrue(test2.isEmpty() || test2.stream().allMatch(c -> c.getUser() == null),
                "Expected only certificates with null user when searching by null user");
    }

    @Test
    void findByGroupNameAndUserId() {
        // Test finding a certificate by group name and user ID
        Certificate test1 = certificateRepository
                .findByGroupNameAndUserId(certificate1.getGroupName(), user.getId());
        assertEquals(certificate1, test1);

        // Test for a non-existing group name with user ID, expecting null
        Certificate test2 = certificateRepository
                .findByGroupNameAndUserId("NonExistingGroup", user.getId());
        assertNull(test2);
    }

    @Test
    void findByUserId() {
        // Test finding certificates by user ID
        List<Certificate> test1 = certificateRepository.findByUserId(user.getId());
        assertAll("Testing list",
                () -> assertThat(test1).hasSize(2),
                () -> assertThat(test1).contains(certificate1, certificate2));

        // Test with a non-existing user ID, expecting an empty list
        List<Certificate> test2 = certificateRepository.findByUserId(-1L);
        assertTrue(test2.isEmpty());
    }
}
