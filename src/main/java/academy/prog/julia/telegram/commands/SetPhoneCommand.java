package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `SetPhoneCommand` class allows administrators and managers to
 * change the phone numbers of users. It extends the `AdminOrManagerCommand`
 * class, providing access to users with ADMIN or MANAGER roles.
 */
@Component
public class SetPhoneCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command used to set or update user phone numbers.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/set_phone";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Change phone(s) of user"
     */
    @Override
    public String getDescription() {
        return "Change phone(s) of user";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command involves:
     * 1. Entering the user ID to identify the user whose phone number(s) need to be changed.
     * 2. Entering new phone numbers to be set for the user.
     * 3. Applying the new phone numbers to the user.
     *
     * @return a list of `CommandState` objects representing the steps of the command.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
//                new EnterTextState("Enter userId:", "user_id"),
                new EnterTextState("Enter user phone or/and e-mail:\n" +
                        "(example -> 380111111111;aa@gmail.com)", "user_data"),
             //   new FindUserState("user_data"),

                new EnterTextState("Enter new phone(s):\n" +
                        "(example -> 380111111111,380222222222)", "user_phones"),
                new SetPhoneState("user_data", "user_phones")   //"user_id
        );
    }
}