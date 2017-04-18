package us.sourcefoundry.gutenberg.models.forme;

/**
 * This call represents the expected file permissions for a copy or template.
 */
public class Permissions {

    //Set the file to allow for reads.
    private boolean read = true;
    //Set the file to allow for writes.
    private boolean write = true;
    //Set the file to allow for execution.
    private boolean execute = false;

    /**
     * Can the file be read.
     *
     * @return boolean
     */
    public boolean canRead() {
        return read;
    }

    /**
     * Set if the file can be read.
     *
     * @param read boolean
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Can the file be written .
     *
     * @return boolean
     */
    public boolean canWrite() {
        return write;
    }

    /**
     * Set if the file can be written.
     *
     * @param write boolean
     */
    public void setWrite(boolean write) {
        this.write = write;
    }

    /**
     * Can the file be executed.
     *
     * @return boolean
     */
    public boolean canExecute() {
        return execute;
    }

    /**
     * Set if the file can be executed.
     *
     * @param execute boolean
     */
    public void setExecute(boolean execute) {
        this.execute = execute;
    }
}
