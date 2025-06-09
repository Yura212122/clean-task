package academy.prog.julia.exceptions;

/**
 * Custom exception class to represent a scenario where test questions are not found.
 *
 * This exception is thrown when the system cannot find the test questions associated with
 * a particular test. It extends {@link Exception}, indicating that this is a checked exception
 * that must be declared in the method signature or handled within a try-catch block.
 */
public class TestQuestionsNotFound extends Exception{

    /**
     * Constructs a new TestQuestionsNotFound exception with the specified detail message.
     *
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message the detail message to be saved with the exception
     */
    public TestQuestionsNotFound(String message) {
        super(message);
    }
}
