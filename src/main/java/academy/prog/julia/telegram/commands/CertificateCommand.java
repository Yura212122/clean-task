package academy.prog.julia.telegram.commands;

import academy.prog.julia.telegram.states.CertificateState;
import academy.prog.julia.telegram.states.CommandState;
import academy.prog.julia.telegram.states.EnterTextState;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The `CertificateCommand` class handles the generation and sending of certificates to students.
 * This command can be executed by users with admin or manager roles. The user is prompted to provide
 * student IDs, group names, or other identifiers for certificate generation.
 */
@Component
public class CertificateCommand extends AdminOrManagerCommand {

    /**
     * Returns the name of the command, used to trigger the certificate generation process.
     *
     * @return the command name, "/certificate".
     */
    @Override
    public String getName() {
        return "/certificate";
    }

    /**
     * Provides a short description of what the command does.
     * This description will be displayed in the bot's help message.
     *
     * @return a string describing the command functionality, "Command to generate and send certificates to students"
     */
    @Override
    public String getDescription() {
        return "Command to generate and send certificates to students";
    }

    /**
     * Defines the sequence of states for this command. The user is prompted to enter student IDs, group names,
     * or other user information required to generate certificates. The data entered is then processed by the
     * `CertificateState` to handle the certificate generation.
     *
     * @return a list of `CommandState` objects representing the steps required for certificate generation.
     */
    @Override
    public List<CommandState> getStates() {
        return List.of(
                new EnterTextState("Enter student ID (IDs of students) and/or group of" +
                        " student(s) to generate certificate(s): \n" +
                        "E.g.\n" +
                        "user_id:1\n" +
                        "user_id:1,2\n" +
                        "group:Test\n" +
                        "group:Test,Test2\n" +
                        "user_email:test@email.test\n" +
                        "user_email:test@email.test,anothertest@email.test\n" +
                        "user_phone:+380111234567\n" +
                        "user_phone:+380111234567,+380111234568\n" +
                        "And grouping with separator \";\"\n" +
                        "user_id:1;group:Test\n" +
                        "group:Test;user_email:test@email.test",
                        "user_data"
                ),

                new CertificateState("user_data")

        );
    }

}
