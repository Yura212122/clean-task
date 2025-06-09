package academy.prog.julia.helpers;

/**
 * Utility class for validating and normalizing phone numbers.
 *
 * This class provides a method to validate a phone number against a predefined pattern and normalize
 * it into an international format.
 *
 */
public class PhoneNumberValidator {

    /**
     * Regular expression pattern for validating phone numbers. The pattern supports:
     *
     *     International format starting with "00": Matches phone numbers that start with "00"
     *         followed by 7 to 13 digits. Example: "0012345678901". This format is used for international
     *         dialing.
     *     Ukrainian mobile number starting with "0": Matches phone numbers that start with "0"
     *         followed by 9 digits. Example: "0123456789". This format is specific to Ukrainian mobile
     *         numbers without a country code.
     *     International numbers starting with "+" (excluding "+0"): Matches phone numbers that start
     *         with "+" followed by 9 to 15 digits, but excludes numbers starting with "+0". Example:
     *         "+1234567890". This format is used for international dialing with a country code.
     *     Local numbers without "+" or "0": Matches phone numbers that consist of 9 to 15 digits
     *         without any prefix. Example: "123456789". This format is used for local dialing where the
     *         country code is not included.
     *
     */
    private static final String PHONE_NUMBER_PATTERN = "^(00\\d{7,13}|0\\d{9}|(?!\\+0)\\+\\d{9,15}|\\d{9,15})$";

    /**
     * Validates and normalizes the provided phone number.
     *
     * The method first checks if the phone number matches the pattern. If it does, it normalizes the number
     * into an international format. It handles various cases including:
     *
     *     International format starting with "00"
     *     Ukrainian mobile number starting with "0"
     *     International numbers starting with "+" (excluding "+0")
     *     Local numbers without "+" or starting with "0"
     *
     * If the phone number does not match the pattern, it returns null.
     *
     * @param phoneNumber the phone number to validate and normalize
     * @return the normalized phone number in international format, or null if invalid
     */
    public static String validateAndNormalize(String phoneNumber) {
        if (phoneNumber.matches(PHONE_NUMBER_PATTERN)) {
            if (phoneNumber.startsWith("00")) {
                return "+" + phoneNumber.substring(2);
            } else if (phoneNumber.startsWith("0")) {
                return "+38" + phoneNumber;
            } else if (phoneNumber.matches("^\\+\\d+") && !phoneNumber.startsWith("+0")) {
                return phoneNumber;
            } else if (!phoneNumber.startsWith("+") && !phoneNumber.startsWith("0")) {
                return "+" + phoneNumber;
            }
        }

        return null;
    }

}
