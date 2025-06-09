package academy.prog.julia.services;

import academy.prog.julia.json_responses.CertificateResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import academy.prog.julia.components.CertificateGenerator;
import academy.prog.julia.model.Certificate;
import academy.prog.julia.model.CertificateTask;
import academy.prog.julia.model.Group;
import academy.prog.julia.model.User;
import academy.prog.julia.repos.CertificateRepository;
import academy.prog.julia.repos.CertificateTaskRepository;
import academy.prog.julia.repos.GroupRepository;
import academy.prog.julia.repos.UserRepository;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import academy.prog.julia.exceptions.JuliaRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Service for managing the creation, generation, and sending of certificates for users.
 * Handles tasks related to certificate generation and sending emails with attached certificates.
 */
@Service
@EnableScheduling
public class CertificateService {

    private static final Logger LOGGER = LogManager.getLogger(CertificateService.class);

    private final CertificateTaskRepository certificateTaskRepository;
    private final CertificateGenerator certificateGenerator;
    private final UserRepository userRepository;
    private final MailSenderService mailSenderService;
    private final CertificateRepository certificateRepository;
    private final GroupRepository groupRepository;

    /**
     * Constructor for CertificateService.
     *
     * @param certificateTaskRepository repository for handling certificate tasks
     * @param certificateGenerator      service for generating certificate files
     * @param userRepository            repository for managing users
     * @param mailSenderService         service for sending emails
     * @param certificateRepository     repository for managing certificates
     * @param groupRepository           repository for managing user groups
     */
    public CertificateService(
            CertificateTaskRepository certificateTaskRepository,
            CertificateGenerator certificateGenerator,
            UserRepository userRepository,
            MailSenderService mailSenderService,
            CertificateRepository certificateRepository,
            GroupRepository groupRepository
    ) {
        this.certificateTaskRepository = certificateTaskRepository;
        this.certificateGenerator = certificateGenerator;
        this.userRepository = userRepository;
        this.mailSenderService = mailSenderService;
        this.certificateRepository = certificateRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Processes a map of users and creates tasks for generating certificates for them.
     *
     * @param userMap a map where the key is the group name and the value is a list of users
     */
    @Transactional
    public void processUsers(Map<String, List<User>> userMap) {
        for (Map.Entry<String, List<User>> entry : userMap.entrySet()) {
            String groupName = entry.getKey();
            List<User> userList = entry.getValue();

            for (User user : userList) {
                CertificateTask certificateTask = new CertificateTask();
                certificateTask.setUserId(user.getId());
                certificateTask.setGroupName(groupName);
                certificateTask.setGenerated(false);
                certificateTask.setSend(false);
                certificateTask.setSendError("empty");

                certificateTaskRepository.save(certificateTask);
            }
        }
    }

    /**
     * Retrieves a map of users filtered by different criteria such as user IDs, groups, phone numbers, and emails.
     *
     * @param context    the state execution context
     * @param userIds    list of user IDs to filter by
     * @param groups     list of group names to filter by
     * @param userPhones list of user phone numbers to filter by
     * @param userEmails list of user email addresses to filter by
     * @return a map where the key is the group name and the value is a list of users matching the criteria
     */
    @Transactional(readOnly = true)
    public Map<String, List<User>> getUserListByCriteria(
            StateExecutionContext context,
            List<String> userIds,
            List<String> groups,
            List<String> userPhones,
            List<String> userEmails
    ) {
        Map<String, List<User>> userMap = new HashMap<>();

        // Filtering by user IDs
        if (!userIds.isEmpty()) {
            for (String userId : userIds) {
                try {
                    Long userIdLong = Long.valueOf(userId);
                    User userOptional = userRepository.findById(userIdLong).orElse(null);
                    if (userOptional != null) {
                        List<Group> groupList = groupRepository.findAllByUser(userOptional);
                        for (Group group : groupList) {
                            userMap.computeIfAbsent(group.getName(), k -> new ArrayList<>()).add(userOptional);
                        }
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid userId format: {}", userId);
                }
            }
        }

        // Filtering by groups
        if (!groups.isEmpty()) {
            for (String groupName : groups) {
                List<User> users = userRepository.findAllUsersByGroups(groupName);
                for (User user : users) {
                    userMap.computeIfAbsent(groupName, k -> new ArrayList<>()).add(user);
                }
            }
        }

        // Filtering by phone numbers
        if (!userPhones.isEmpty()) {
            for (String phone : userPhones) {
                List<User> users = userRepository.findByPhoneLike(phone);

                for (User user : users) {
                    List<Group> groupList = groupRepository.findAllByUser(user);
                    for (Group group : groupList) {
                        userMap.computeIfAbsent(group.getName(), k -> new ArrayList<>()).add(user);
                    }
                }

            }
        }

        // Filtering by email addresses
        if (!userEmails.isEmpty()) {
            for (String email : userEmails) {
                List<User> users = userRepository.findByEmailLike(email);

                for (User user : users) {
                    List<Group> groupList = groupRepository.findAllByUser(user);
                    for (Group group : groupList) {
                        userMap.computeIfAbsent(group.getName(), k -> new ArrayList<>()).add(user);
                    }
                }
            }
        }

        return userMap;
    }

    /**
     * Scheduled task that generates certificates for users every minute.
     *
     * isolation = Isolation.SERIALIZABLE this is the highest isolation level in database transaction
     * management. It ensures that transactions are executed in such a way that they appear to be
     * completely isolated from one another
     */
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void scheduledCertificateGeneratorForUsers() {

        List<CertificateTask> tasksToGenerate = certificateTaskRepository.findCertificateTasksToGenerate();

        for (CertificateTask task : tasksToGenerate) {
            long userId = task.getUserId();
            String groupName = task.getGroupName();

            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                try {
                    certificateGenerator.generateCertificate(user, groupName, userId);
                    task.setGenerated(true);
                } catch (ResourceAccessException resourceAccessException) {
                    LOGGER.info("Service for certificate generation NOT FOUND");
                    task.setSendError("Service for certificate generation NOT FOUND");
                } catch (Exception exception) {
                    LOGGER.info("Service for certificate generation ERROR");
                    task.setSendError("Service for certificate generation ERROR");
                }
            }
        }
    }

    /**
     * Scheduled task that sends certificates to users' emails every minute.
     */
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void sendCertificateOnEmails() {
        List<CertificateTask> tasksToSend = certificateTaskRepository.findTasksToBeSent();

        for (CertificateTask task : tasksToSend) {
            if (!task.isSend()) {
                try {
                    Long userId = task.getUserId();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new JuliaRuntimeException("User not found with ID: " + userId));
                    String emailTo = user.getEmail();
                    String subject = "Certificate";
                    String message = "Dear " + user.getName() + ",\n\nPlease find attached your certificate.";

                    List<Certificate> certificateList = certificateRepository.findByUser(user);

                    if (!certificateList.isEmpty()) {
                        for (Certificate certificate : certificateList) {
                            if (Objects.equals(certificate.getGroupName(), task.getGroupName()) &&
                                    Objects.equals(certificate.getUser().getId(), task.getUserId())
                            ) {
                                mailSenderService
                                        .sendCertificateEmail(emailTo, subject, message, certificate.getFile());

                                task.setSend(true);
                            }
                        }

                    } else {
                        throw new JuliaRuntimeException("Certificate not found for User with ID: " + userId);
                    }
                } catch (Exception e) {
                    task.setSendError(e.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves the certificate file by its unique ID.
     *
     * @param certificateId the unique ID of the certificate
     * @return the byte array representing the certificate file as a IMAGE_PNG
     */
    @Transactional(readOnly = true)
    public byte[] getCertificateById(String certificateId) {
        Certificate certificate = certificateRepository.findByUniqueId(certificateId);

        if (certificate != null) {
            byte[] pdfData = certificate.getFile();
            try {
                return convertPdfToImage(pdfData);
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert PDF to image", e);
            }
        } else {
            throw new NoSuchElementException("Certificate not found!");
        }
    }


    @Transactional(readOnly = true)
    public List<CertificateResponse> getAllCertificatesByUserId(String userId) {
        List<Certificate> certificates = certificateRepository.findByUserId(Long.valueOf(userId));
        List<CertificateResponse> certificateResponse = new ArrayList<>();

        for (Certificate certificate : certificates) {
            CertificateResponse dto = new CertificateResponse(
                    certificate.getId(),
                    certificate.getUniqueId(),
                    certificate.getGroupName()
            );
            certificateResponse.add(dto);
        }
        return certificateResponse;
    }


    /**
     * Converts a PDF file into a PNG image.
     *
     * @param pdfData a byte array representing the PDF file
     * @return a byte array representing the image in PNG format
     * @throws IOException if there are issues handling the PDF or image conversion
     */
    public byte[] convertPdfToImage(byte[] pdfData) throws IOException {
        // Load the PDF document from the byte array
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfData))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

            // Convert the BufferedImage to a PNG byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            return baos.toByteArray();
        }
    }

}
