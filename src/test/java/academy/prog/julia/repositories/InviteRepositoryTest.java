package academy.prog.julia.repositories;


import academy.prog.julia.model.Invite;
import academy.prog.julia.model.UserRole;
import academy.prog.julia.repos.InviteRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Rollback
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InviteRepositoryTest {

    @Autowired
    private InviteRepository inviteRepository;
    private Invite invite;

    @BeforeEach
    void setUp(){
        invite = new Invite();
        invite.setRole(UserRole.STUDENT);
        invite.setCode("code1");
        invite.setUsageCount(1);
        invite.setExpirationDate(LocalDateTime.now());
        inviteRepository.save(invite);


    }

    @Test
    void findByCode(){
        Optional<Invite> testFound = inviteRepository.findByCode(invite.getCode());
        assertTrue(testFound.isPresent());
        assertEquals(invite, testFound.get());

        Optional<Invite> testNotFound = inviteRepository.findByCode("NotExisitingCode");
        assertTrue(testNotFound.isEmpty());

        Optional<Invite> testNull = inviteRepository.findByCode(null);
        assertTrue(testNull.isEmpty());


    }

}
