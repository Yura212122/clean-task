package academy.prog.julia.services;

import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sending emails, including sending certificates as attachments.
 */
@Service
public class MailSenderService {

    private static final Logger LOGGER = LogManager.getLogger(MailSenderService.class);

    private final JavaMailSender javaMailSender;
    private final CertificateRepository certificateRepository;

    @Value("${spring.mail.username}")
    private String emailFrom;

    /**
     * Constructor for MailSenderService.
     *
     * @param javaMailSender the JavaMailSender instance for sending emails
     * @param certificateRepository the repository for accessing Certificate entities
     */
    public MailSenderService(
            JavaMailSender javaMailSender,
            CertificateRepository certificateRepository
    ) {
        this.javaMailSender = javaMailSender;
        this.certificateRepository = certificateRepository;
    }

    /**
     * Sends an email with a certificate attached.
     *
     * @param subject the subject of the email
     * @param messageBody the body of the email
     * @param certificateId the ID of the certificate to be attached
     * @param user the user who will receive the email
     */
    @Transactional
    public void send(
            String subject,
            String messageBody,
            Long certificateId,
            User user
    ) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new JuliaRuntimeException("Certificate not found with ID: " + certificateId));

        if (!certificate.getUser().equals(user)) {
            LOGGER.error("Certificate does not belong to user with ID: {}", user.getId());
            throw new JuliaRuntimeException("Certificate does not belong to user with ID: " + user.getId());
        }

        // Create a new MimeMessage for sending the email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(emailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(messageBody, true);

            // Add the certificate as an attachment
            ByteArrayDataSource dataSource =
                    new ByteArrayDataSource(certificate.getFile(), "application/octet-stream");

            helper.addAttachment(certificate.getUniqueId(), dataSource);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email. Error: {}", e.getMessage());
            throw new JuliaRuntimeException("Failed to send email");
        }

        javaMailSender.send(mimeMessage);
    }

    /**
     * Sends a simple email message.
     *
     * @param emailTo the recipient's email address
     * @param subject the subject of the email
     * @param message the body of the email
     */
    public void sendFromProgAcademy(
            String emailTo,
            String subject,
            String message
    ) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        // Set the email properties
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    /**
     * Sends an email with a certificate attached as a PDF file.
     *
     * @param emailTo the recipient's email address
     * @param subject the subject of the email
     * @param message the body of the email
     * @param certificateFile the byte array of the certificate file to be attached
     */
    public void sendCertificateEmail(
            String emailTo,
            String subject,
            String message,
            byte[] certificateFile
    ) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // Set the email properties
            helper.setFrom(emailFrom);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(message, true);

            ByteArrayResource resource = new ByteArrayResource(certificateFile);
            helper.addAttachment("certificate.pdf", resource);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email in method sendCertificateEmail! Error: {}", e.getMessage());
            throw new JuliaRuntimeException("Failed to send email in method sendCertificateEmail!");
        }
    }



}
