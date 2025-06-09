package academy.prog.julia.telegram.states;

import academy.prog.julia.model.User;
import academy.prog.julia.telegram.executor.StateExecutionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * State for setting or updating the phone numbers of a user.
 * This class extends CommandState.
 */
public class SetPhoneState extends CommandState {

    private static final Logger LOGGER = LogManager.getLogger(SetPhoneState.class);

    private static final String SEPARATOR = ",";

    private static final Set<String> COUNTRY_CODES = new HashSet<>(Set.of(
            "213", "376", "244", "1264", "1268", "54", "374", "297", "61", "43", "994", "1242", "973", "880",
            "1246", "375", "32", "501", "229", "1441", "975", "591", "387", "267", "55", "673", "359", "226",
            "257", "855", "237", "1", "238", "1345", "236", "56", "86", "57", "269", "242", "682", "506", "385",
            "53", "90392", "357", "42", "45", "253", "1809", "593", "20", "503", "240", "291", "372", "251",
            "500", "298", "679", "358", "33", "594", "689", "241", "220", "7880", "49", "233", "350", "30", "299",
            "1473", "590", "671", "502", "224", "245", "592", "509", "504", "852", "36", "354", "91", "62", "98",
            "964", "353", "972", "39", "1876", "81", "962", "77", "254", "686", "850", "82", "965", "996", "856",
            "371", "961", "266", "231", "218", "417", "370", "352", "853", "389", "261", "265", "60", "960",
            "223", "356", "692", "596", "222", "52", "691", "373", "377", "976", "1664", "212", "258", "95",
            "264", "674", "977", "31", "687", "64", "505", "227", "234", "683", "672", "670", "47", "968", "680",
            "507", "675", "595", "51", "63", "48", "351", "1787", "974", "262", "40", "250", "378", "239", "966",
            "221", "381", "248", "232", "65", "421", "386", "677", "252", "27", "34", "94", "290", "1869", "1758",
            "249", "597", "268", "46", "41", "963", "886", "66", "228", "676", "1868", "216", "90", "993", "1649",
            "688", "256", "44", "380", "971", "598", "678", "379", "58", "84", "681", "969", "967", "260", "263"
    ));

    private final String userIdAttribute;
    private final String userPhonesAttribute;

    /**
     * Constructs a SetPhoneState with specified attributes for user ID and list of phone numbers.
     *
     * @param userIdAttribute the attribute name containing the user ID
     * @param userPhonesAttribute the attribute name containing the list of phone numbers
     */
    public SetPhoneState(
            String userIdAttribute,
            String userPhonesAttribute
    ) {
        super(false);
        this.userIdAttribute = userIdAttribute;
        this.userPhonesAttribute = userPhonesAttribute;
    }

    /**
     * Handles the entry into this state by updating the user's phone numbers.
     *
     * @param context the execution context containing state and service information
     */
    @Override
    public void enter(StateExecutionContext context) {
        var userId = context.getAttributeAsString(userIdAttribute);
        var userPhones = context.getAttributeAsString(userPhonesAttribute);

        if (userId == null || !isLong(userId)) {
            context.sendMessage("Wrong user id");
            return;
        }

        if (userPhones == null) {
            context.sendMessage("Wrong user phones");
            return;
        }

        var userService = context.getUserService();
        Optional<User> optionalUser = userService.findById(Long.parseLong(userId));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            var parts = userPhones.split(SEPARATOR);
            var newParts = normalizePhoneNumber(parts);

            for (String part : newParts) {
                if (!isValidCountryCode(part)) {
                    context.sendMessage("Invalid country code in the phone number: " + part + ".");
                    return;
                }

                if (part.length() < 9 || part.trim().length() > 15) {
                    context.sendMessage(
                            "Invalid quantity of digits in the phone number: " + part +
                            ". The length of the number should be from 9 to 15 digits."
                    );
                    return;
                }

                // Check if the phone number is already in use by another user
                List<User> existingUsers = userService.findByPhone(part);
                for (User existingUser : existingUsers) {
                    if (!existingUser.getId().equals(user.getId())) {
                        context.sendMessage("Phone number " + part + " is already in use by another user.");
                        return;
                    }
                }
            }

            LOGGER.info("parts length: {}", newParts.length);
            try {
                user.setPhone(newParts[0].trim());
                user.getPhones().clear();

                for (String phone : newParts) {
                    user.getPhones().add("+" + phone.trim());
                }

                userService.saveUser(user);
                context.sendMessage("User phone(s) successfully changed");

            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid phone number format: {}", e.getMessage());
                context.sendMessage(
                        "Invalid phone number format: " + newParts[0].trim() + ". Please use a valid format."
                );
            }
        } else {
            context.sendMessage("user id not exists");
        }
    }

    /**
     * Checks if a string can be parsed as a long integer.
     *
     * @param str the string to check
     * @return true if the string can be parsed as a long, false otherwise
     */
    private static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates if the phone number starts with a known country code.
     *
     * @param phoneNumber the phone number to check
     * @return true if the phone number starts with a valid country code, false otherwise
     */
    public boolean isValidCountryCode(String phoneNumber) {
        for (String code : COUNTRY_CODES) {
            if (phoneNumber.startsWith(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Normalizes phone numbers by trimming whitespace and removing the leading "+".
     *
     * @param phoneNumbers an array of phone numbers
     * @return a new array with normalized phone numbers
     */
    private String[] normalizePhoneNumber(String[] phoneNumbers) {
        var newPhones = new String[phoneNumbers.length];
        for (int i = 0; i < phoneNumbers.length; i++) {
            String phoneNumber = phoneNumbers[i].trim();
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.substring(1);
            }
            newPhones[i] = phoneNumber;
        }
        return newPhones;
    }

}
