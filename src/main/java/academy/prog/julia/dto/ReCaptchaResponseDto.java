package academy.prog.julia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
 * DTO for handling the response from the reCAPTCHA verification API.
 *
 * This class is used to map the JSON response from the reCAPTCHA service to a Java object.
 * The response JSON typically includes fields such as:
 * - "success": a boolean indicating whether the reCAPTCHA verification was successful.
 * - "challenge_ts": the timestamp when the reCAPTCHA challenge was loaded (ISO format yyyy-MM-dd'T'HH:mm:ssZZ).
 * - "hostname": the hostname of the site where the reCAPTCHA was solved.
 * - "error-codes": an optional list of error codes if the verification failed (in our case, it will remove
 * the "-" character as Java does not support it.
 *
 * In this implementation, only the "success" and "error-codes" fields are retained.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) Ignore any unknown properties in the JSON response that are not
 * mapped to fields in this DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReCaptchaResponseDto {

    private boolean success;

    @JsonAlias("error-codes")
    private Set<String> errorCodes;

    /**
     * Gets the success status of the reCAPTCHA verification.
     *
     * @return true if the verification was successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the reCAPTCHA verification.
     *
     * @param success true if the verification was successful, false otherwise.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the set of error codes returned by the reCAPTCHA API if verification failed.
     *
     * @return a set of error codes, or null if there are no error codes.
     */
    public Set<String> getErrorCodes() {
        return errorCodes;
    }

    /**
     * Sets the set of error codes returned by the reCAPTCHA API.
     *
     * @param errorCodes a set of error codes, or null if there are no error codes.
     */
    public void setErrorCodes(Set<String> errorCodes) {
        this.errorCodes = errorCodes;
    }

}
