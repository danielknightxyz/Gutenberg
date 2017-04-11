package us.sourcefoundry.gutenberg.services;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * This will allow you to interact with the file system.
 */
public class FileSystemService {

    /**
     * Allows you to get a location from the file system by providing a path.
     *
     * @param location The location of the file or directory to get.
     * @return A File.
     */
    public File getByLocation(String location) {
        return new File(location);
    }

    /**
     * Will open a file for streaming.
     *
     * @param file The file to streamFile.
     * @return InputStream
     */
    public InputStream streamFile(File file) throws FileNotFoundException {
        if (file.isDirectory())
            return null;

        return new FileInputStream(file);
    }

    /**
     * Creates a directory.
     *
     * @param location The location of the directory to create.
     * @return Boolean
     */
    public boolean createDirectory(String location) {
        return (new File(location)).mkdirs();
    }

    /**
     * Creates a file with the provided contents.
     *
     * @param location The path tot save the file.
     * @param contents The contents to save in the file.
     */
    public void createFile(String location, String contents) {
        try {
            File file = new File("test1.txt");
            FileWriter fileWriter = new FileWriter(new File(location));
            fileWriter.write(contents);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a directory from one location to  another.
     *
     * @param sourcePath      The location of the directory to copy.
     * @param destinationPath The location to copy the directory.
     */
    public void copyDirectory(String sourcePath, String destinationPath) throws IOException {
        FileUtils.copyDirectory(
                this.getByLocation(sourcePath), this.getByLocation(destinationPath)
        );
    }

    /**
     * Copies a file from one location to  another.
     *
     * @param sourcePath      The location of the file to copy.
     * @param destinationPath The location to copy the directory.
     */
    public void copyFile(String sourcePath, String destinationPath) throws IOException {
        FileUtils.copyFile(
                this.getByLocation(sourcePath), this.getByLocation(destinationPath)
        );
    }
}
