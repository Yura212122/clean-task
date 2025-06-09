package academy.prog.julia.telegram.commands;

import java.util.List;

import org.springframework.stereotype.Component;

import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import academy.prog.julia.telegram.states.FindUserState;
import academy.prog.julia.telegram.states.SetEmailState;

/**
 * The `SetEmailCommand` class allows administrators and managers to
 * change the email addresses of users. It extends the `AdminOrManagerCommand`
 * class, allowing access to users with ADMIN or MANAGER roles.
 */
@Component
public class SetEmailCommand extends AdminOrManagerCommand{

    /**
     * Returns the name of the command used to set or update user email addresses.
     *
     * @return the command name as a string.
     */
    @Override
    public String getName() {
        return "/set_email";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Change email(s) of user"
     */
    @Override
    public String getDescription() {
        return "Change email(s) of user";
    }

    /**
     * Defines the sequence of states that the command will go through during its execution.
     * This command involves:
     * 1. Entering user phone or email to identify the user.
     * 2. Finding the user based on the provided data.
     * 3. Entering new email addresses, where the first email is used for login.
     * 4. Setting the new email addresses for the user.
     *
     * @return a list of `CommandState` objects representing the steps of the command.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter user phone or/and e-mail:\n" +
                        "(example -> 380111111111;aa@gmail.com)", "user_data"),
                new FindUserState("user_data"),
                new EnterTextState("Enter new email(s):\n" +
                        "(example -> aa@gmail.com,bb@gmail.com)\n" +
                        "First mail in the list is the mail for login!", "emailsToChange"),
                new SetEmailState("user_data", "emailsToChange")
        );
    }

}
