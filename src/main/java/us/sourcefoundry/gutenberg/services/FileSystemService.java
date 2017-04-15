package us.sourcefoundry.gutenberg.services;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.MessageFormat;

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
     * Allows you to get a location from the file system by providing a path with message formatting.
     *
     * @param location The location of the file or directory to get.
     * @param args     Replacement Arguments.
     * @return A File
     */
    public File getByLocation(String location, Object... args) {
        return new File(
                MessageFormat.format(location, args)
        );
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
            File newFile = new File(location);
            newFile.setReadable(true,true);
            newFile.setWritable(true,true);
            newFile.setExecutable(true,true);
            FileWriter fileWriter = new FileWriter(newFile);
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
