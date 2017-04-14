package us.sourcefoundry.gutenberg.models.commands.add;

import us.sourcefoundry.gutenberg.models.forme.Forme;

/**
 * This represents the a located forme file in a tar archive.  This used when adding forme(s) from an arvhive downloaded
 * from the Github archive.
 */
public class ArchiveScanResult {

    //The path in the archive the forme file was located.
    private String archivePath;
    //The forme from the archive.  This is the actual forme which was located.
    private Forme forme;

    /**
     * Get the archive path.
     *
     * @return String
     */
    public String getArchivePath() {
        return archivePath;
    }

    /**
     * Set the archive path.
     *
     * @param archivePath String
     */
    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    /**
     * Get the forme.
     *
     * @return Forme
     */
    public Forme getForme() {
        return forme;
    }

    /**
     * Set the forme.
     *
     * @param forme Forme
     */
    public void setForme(Forme forme) {
        this.forme = forme;
    }
}
