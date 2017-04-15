package us.sourcefoundry.gutenberg.models.forme;

public class Permissions {

    private boolean read = true;
    private boolean write = true;
    private boolean execute = false;

    public boolean canRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean canWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean canExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }
}
