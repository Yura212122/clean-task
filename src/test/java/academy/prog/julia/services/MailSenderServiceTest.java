package academy.prog.julia.services;

import academy.prog.julia.exceptions.JuliaRuntimeException;
import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Optional;

import static academy.prog.julia.services.TaskServiceTest.getTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {
    @InjectMocks
    private MailSenderService mailSenderService;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() throws Exception {
        Field emailFromField = MailSenderService.class.getDeclaredField("emailFrom");
        emailFromField.setAccessible(true);
        emailFromField.set(mailSenderService, "progacademytest@gmail.com");
    }

    @Test
    @Transactional
    void testSend_whenCertificateNotFound() {
        Long certificateId = 1L;

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        JuliaRuntimeException actual = assertThrows(JuliaRuntimeException.class, () -> mailSenderService.send("", "", certificateId, null));
        assertEquals("Certificate not found with ID: " + certificateId, actual.getMessage());
        verify(certificateRepository, times(1)).findById(certificateId);
    }

    @Test
    @Transactional
    void testSend_whenIncorrectUser() {
        Long certificateId = 1L;
        User user = getTestUser(1L);
        User actualUser = getTestUser(2L);
        Certificate certificate = new Certificate();
        certificate.setUser(actualUser);
        certificate.setId(certificateId);
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        JuliaRuntimeException actual = assertThrows(JuliaRuntimeException.class, () -> mailSenderService.send("", "", certificateId, user));
        assertEquals("Certificate does not belong to user with ID: " + user.getId(), actual.getMessage());
        verify(certificateRepository, times(1)).findById(certificateId);
    }

    @Test
    @Transactional
    void testSend_whenSuccess() {
        Long certificateId = 1L;
        User user = getTestUser(1L);
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setId(certificateId);
        certificate.setFile(new byte[]{});
        certificate.setUniqueId("unique" + certificateId);
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        mailSenderService.send("Test Subject", "Test body", certificateId, user);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @Transactional
    void testSendFromProgAcademy_whenSuccess() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        mailSenderService.sendFromProgAcademy("user@mail.com", "Test Subject", "Test body");
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @Transactional
    void testSendCertificateEmail_whenSuccess() {
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        mailSenderService.sendCertificateEmail("user@mail.ua", "Test Subject", "Test Message", new byte[]{});

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

}
