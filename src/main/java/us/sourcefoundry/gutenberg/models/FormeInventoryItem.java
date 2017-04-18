package us.sourcefoundry.gutenberg.models;

/**
 * An entry from the inventory file.
 */
public class FormeInventoryItem {

    //The github user name from which the forme was downloaded.
    private String username;
    //The github repository from which the forme was downloaded.
    private String repository;
    //The repository reference.
    private String reference;
    //The name of the forme.
    private String name;
    //The location it was installed in the installation directory.
    private String installPath;

    /**
     * Gets the github username.
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the github username.
     *
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the github repository.
     *
     * @return String
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Sets the github repository.
     *
     * @param repository String
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * Gets the repository reference.
     *
     * @return String
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the repository reference.
     *
     * @param reference String
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the forme name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the forme name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the install path.
     *
     * @return String
     */
    public String getInstallPath() {
        return installPath;
    }

    /**
     * Sets the install path.
     *
     * @param installPath String
     */
    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }
}
