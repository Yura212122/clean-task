package academy.prog.julia.controllers;

import academy.prog.julia.json_responses.InviteCodeResponse;
import academy.prog.julia.json_responses.RegistrationResponse;
import academy.prog.julia.model.User;
import academy.prog.julia.services.RegistrationService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for handling user registration and invite code validation.
 * This controller provides endpoints for user registration and checking the validity of invite codes.
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    /**
     * Constructor to inject the required registration service.
     *
     * @param registrationService the service responsible for handling registration logic
     */
    public RegistrationController(
            RegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    /**
     * Endpoint for registering a new user.
     * The method takes in user details such as name, surname, phone, email, and passwords, and performs validation.
     * It also checks for CAPTCHA response and invite code validity before proceeding with registration.
     *
     * @param name the first name of the user
     * @param surname the last name of the user
     * @param phone the phone number of the user
     * @param email the email address of the user
     * @param password the user's chosen password
     * @param passwordConfirm confirmation of the password to ensure they match
     * @param user the user object, which is validated using @Valid
     * @param bindingResult the result of validation binding for the user object
     * @param inviteCode the invite code required for registration
     * @param captchaResponse optional CAPTCHA response for spam prevention
     * @return RegistrationResponse object containing the result of the registration process
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            @RequestParam("client_invite") String inviteCode,
            @RequestParam(value = "g-recaptcha-response", required = false) String captchaResponse
    ) {

        LOGGER.info("Registration process initiated.");

        return registrationService.register(
                name,
                surname,
                phone,
                email,
                password,
                passwordConfirm,
                bindingResult,
                inviteCode,
                captchaResponse
        );
    }

    /**
     * Endpoint to check the validity of an invite-code.
     * This method validates the invite code provided by the user to ensure that it is valid and usable for registration.
     *
     * @param inviteCode the invite code provided by the user
     * @return InviteCodeResponse containing the result of the invite code validation
     */
    @PostMapping("/invite")
    public InviteCodeResponse invite(
            @RequestParam("inviteCode") String inviteCode
    ) {
        return registrationService.checkInviteCode(inviteCode);
    }

}