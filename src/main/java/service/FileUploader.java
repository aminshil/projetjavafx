package service;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUploader {
    private final String targetDirectory;

    public FileUploader(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String upload(File file) throws IOException {
        String originalFilename = file.getName();
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        String fileExtension = getFileExtension(originalFilename);
        String fileName = safeFilename + "-" + UUID.randomUUID().toString() + "." + fileExtension;

        File targetFile = new File(targetDirectory, fileName);
        file.renameTo(targetFile);

        return fileName;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }
}
