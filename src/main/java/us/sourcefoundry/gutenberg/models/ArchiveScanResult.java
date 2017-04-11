package us.sourcefoundry.gutenberg.models;

public class ArchiveScanResult {

    private String archivePath;
    private FormeContext context;

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public FormeContext getContext() {
        return context;
    }

    public void setContext(FormeContext context) {
        this.context = context;
    }
}
