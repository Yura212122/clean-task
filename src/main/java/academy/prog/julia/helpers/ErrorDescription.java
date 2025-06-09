package academy.prog.julia.helpers;

/**
 * Represents a description of an error associated with a specific property.
 *
 * This class encapsulates information about an error related to a particular property,
 * including the property name and the error message.
 *
 */
public class ErrorDescription {

    private String property;
    private String error;

    /**
     * Constructs a new ErrorDescription with the specified property and error message.
     *
     * @param property the name of the property associated with the error
     * @param error the error message
     */
    public ErrorDescription(
            String property,
            String error
    ) {
        this.property = property;
        this.error = error;
    }

    /**
     * Returns the property name associated with the error.
     *
     * @return the property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the property name associated with the error.
     *
     * @param property the property name to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error message.
     *
     * @param error the error message to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Returns a string representation of the ErrorDescription in JSON format.
     *
     * @return a JSON representation of the ErrorDescription
     */
    @Override
    public String toString() {
        return "{\"property\":\"" + property + "\", \"error\":\"" + error + "\"}";
    }

}
