package us.sourcefoundry.gutenberg.models.forme;

/**
 * This represents a user prompt for information.
 */
public class VarPrompt {

    //The message to show the user when requesting input.
    private String message;
    //The name of the variable in which the user response will be stored.
    private String name;
    //The default value of the variable if the user does not supply an response and instead hits enter/return.
    private String defaultValue = "";

    /**
     * Gets the message.
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     * @param message String
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets Name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets Name
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Default Value.
     * @return String
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     * @param defaultValue String
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
