package academy.prog.julia.services;

import academy.prog.julia.dto.ReCaptchaResponseDto;
import academy.prog.julia.helpers.ErrorDescription;
import academy.prog.julia.helpers.PhoneNumberValidator;
import academy.prog.julia.json_responses.InviteCodeResponse;
import academy.prog.julia.json_responses.RegistrationResponse;
import academy.prog.julia.model.Invite;
import academy.prog.julia.repos.InviteRepository;
import academy.prog.julia.telegram.BotCredentials;
import academy.prog.julia.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for users registration, including registrations to current application and
 * checking invite code for registration.
 */
@Service
public class RegistrationService {

    @Value("${recaptcha.url}")
    private String recaptchaUrlProp;

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private static final Logger LOGGER = LogManager.getLogger(RegistrationService.class);

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final BotCredentials botCredentials;
    private final InviteRepository inviteRepository;
    private final Environment environment;

    /**
     * Constructor for RegistrationService.
     *
     * @param userService the UserService instance for user-related operations
     * @param botCredentials the BotCredentials instance for bot-related information
     * @param restTemplate the RestTemplate instance for making HTTP requests
     * @param inviteRepository the InviteRepository instance for invite-related operations
     * @param environment the Environment instance for accessing environment properties
     */
    public RegistrationService(
            UserService userService,
            BotCredentials botCredentials,
            RestTemplate restTemplate,
            InviteRepository inviteRepository,
            Environment environment
    ) {
        this.userService = userService;
        this.botCredentials = botCredentials;
        this.restTemplate = restTemplate;
        this.inviteRepository = inviteRepository;
        this.environment = environment;
    }

    /**
     * Handles user registration, including validation and CAPTCHA verification.
     *
     * @param name the user's first name
     * @param surname the user's last name
     * @param phone the user's phone number
     * @param email the user's email address
     * @param password the user's password
     * @param passwordConfirm the confirmation of the user's password
     * @param bindingResult the result of validation binding
     * @param inviteCode the invite code used for registration
     * @param captchaResponse the CAPTCHA response token
     * @return a RegistrationResponse containing the result of the registration process
     */
    @Transactional
    public RegistrationResponse register(
            String name,
            String surname,
            String phone,
            String email,
            String password,
            String passwordConfirm,
            BindingResult bindingResult,
            String inviteCode,
            String captchaResponse
    ) {
        // Check internet availability
        if (userService.isUserInternetAvailable()) {
            String urlReCaptcha = String.format(
                    recaptchaUrlProp.concat("?secret=%s&response=%s"),
                    recaptchaSecret,
                    captchaResponse
            );

            boolean isPhoneExists = userService.isPhoneNumberExists(PhoneNumberValidator.validateAndNormalize(phone));
            boolean isEmailExists = userService.isEmailExists(email);

            Optional<Invite> isInviteCodeValid = inviteRepository.findByCode(inviteCode);

            List<ErrorDescription> errorList = new ArrayList<>();

            if (PhoneNumberValidator.validateAndNormalize(phone) == null) {
                errorList.add(new ErrorDescription("phone", "Invalid phone number format!"));
                logPhoneNumberError("Invalid phone number format!");

                return new RegistrationResponse("failed", errorList, "");
            }

            if (isInviteCodeValid.isEmpty()) {
                errorList.add(new ErrorDescription(
                        "inviteCode",
                        "Your invite-code is invalid or completely used up!"
                ));
                LOGGER.info("inviteCodeError: {}", "Your invite-code is invalid or completely used up!");
                return new RegistrationResponse("failed", errorList, "");
            }

            if (isEmailExists) {
                LOGGER.info("emailError: {}", "Email address already exists!");
                errorList.add(new ErrorDescription("email", "Email address already exists!"));
            }

            if (isPhoneExists) {
                logPhoneNumberError("Phone number already exists!");
                errorList.add(new ErrorDescription("phone", "Phone number already exists!"));
            }

            if (!errorList.isEmpty()) {
                LOGGER.info("RegistrationError: {}", "Registration failed. " + errorList);
                return new RegistrationResponse("failed", errorList, "");
            }

            // Verify CAPTCHA response
            ReCaptchaResponseDto responseReCaptcha =
                    restTemplate.postForObject(urlReCaptcha, Collections.emptyList(), ReCaptchaResponseDto.class);
            assert responseReCaptcha != null;
            if (responseReCaptcha.isSuccess()) {
                LOGGER.info("captchaError: {}", "Fill captcha");
            }

            if (bindingResult.hasErrors()) {
                List<ErrorDescription> errorListValid = new ArrayList<>();
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    errorListValid.add(new ErrorDescription(fieldError.getField(), fieldError.getDefaultMessage()));
                }

                return new RegistrationResponse("failed", errorListValid, "");
            }

            // Validate password confirmation
            RegistrationResponse response;
            if (!password.equals(passwordConfirm)) {
                List<ErrorDescription> errorList2 = new ArrayList<>();
                errorList2.add(new ErrorDescription("passwordConfirm", "Password are different!"));
                response = new RegistrationResponse("failed", errorList2, "");
            } else {
                var uniqueId = userService.registerUser(name, surname, phone, email, password, inviteCode);

                var telegramUrl = String.format(Utils.TELEGRAM_URL, botCredentials.getBotName(), uniqueId);

                if (StringUtils.isEmpty(name)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription("name", "Name cannot be empty!"));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else if (StringUtils.isEmpty(surname)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription("surname", "Surname cannot be empty!"));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else if (StringUtils.isEmpty(phone)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription("phone", "Phone number cannot be empty!"));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else if (StringUtils.isEmpty(email)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription("email", "Email address cannot be empty!"));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else if (StringUtils.isEmpty(password)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription("password", "Password cannot be empty!"));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else if (StringUtils.isEmpty(passwordConfirm)) {
                    List<ErrorDescription> errorList2 = new ArrayList<>();
                    errorList2.add(new ErrorDescription(
                            "passwordConfirm",
                            "Password confirmation cannot be empty!"
                    ));
                    response = new RegistrationResponse("failed", errorList2, "");
                } else {
                    response = new RegistrationResponse("success", new ArrayList<>(), telegramUrl);
                }
            }

            return response;

        } else {
            List<ErrorDescription> errorList = new ArrayList<>();
            errorList.add(new ErrorDescription("registration", "No internet connection."));
            return new RegistrationResponse("failed", errorList, "");
        }
    }

    /**
     * Checks the validity of an invite code, including whether it has expired or been used up.
     *
     * @param inviteCode the invite code to be checked
     * @return an InviteCodeResponse indicating the status of the invite code
     */
    @Transactional
    public InviteCodeResponse checkInviteCode(String inviteCode) {
        LOGGER.info("inviteCode: {}", inviteCode);

        Optional<Invite> optionalInvite = inviteRepository.findByCode(inviteCode);

        if (optionalInvite.isEmpty()) {
            return new InviteCodeResponse("invalid code");
        }

        Invite inviteForCheckCount = optionalInvite.get();
        LOGGER.info("inviteForCheckCount: {}", inviteForCheckCount);

        // Check for expiration
        boolean isInviteCodeExpired = false;
        if (inviteForCheckCount.getExpirationDate() != null) {
            Instant expirationInstant = inviteForCheckCount
                    .getExpirationDate().atZone(ZoneId.systemDefault()).toInstant();
            Instant currentInstant = Instant.now();
            isInviteCodeExpired = expirationInstant.isBefore(currentInstant);
        }


        // Invite code status
        String inviteCodeStatus;
        if (isInviteCodeExpired) {
            inviteCodeStatus = "expired";
            LOGGER.info("Invite code validation is expired.");
        } else if (inviteForCheckCount.getUsageCount() <= 0) {
            inviteCodeStatus = "used";
            LOGGER.info("Invite code validation is used.");
        } else {
            inviteCodeStatus = "success";
            LOGGER.info("Invite code validation is successful.");
        }

        return new InviteCodeResponse(inviteCodeStatus);
    }

    /**
     * Logs a phone number error.
     *
     * @param message the error message
     */
    private void logPhoneNumberError(String message) {
        LOGGER.info("phoneNumberError: {}", message);
    }

}