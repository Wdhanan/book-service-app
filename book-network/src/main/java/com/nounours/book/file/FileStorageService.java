package com.nounours.book.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j // to log errors
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;
    // the return is the file path of the cover picture
    public String saveFile(@NonNull MultipartFile sourceFile,

                           @NonNull Integer userId) {
        final String fileUploadSubPath ="users" + File.separator  +userId; // subpath is specific to each user

        return uploadFile(sourceFile, fileUploadSubPath);

    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {
        Path finalUploadPath = Paths.get(fileUploadPath, fileUploadSubPath);
        File targetFolder = finalUploadPath.toFile(); // Create folder using finalUploadPath
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + finalUploadPath);
                return null;
            }
        }

        // Extract the file extension (jpeg, jpg, png..)
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // Set up the final path and file name
        String targetFilePath = finalUploadPath.resolve(System.currentTimeMillis() + "." + fileExtension).toString();

        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to " + targetFilePath);
            return targetFilePath;

        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName ==null || fileName.isEmpty()){
            return "";

        }
        //file.jpg get the index of the dot(.)
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        return  fileName.substring(lastDotIndex + 1).toLowerCase(); // return the string beginning after the index of the  dot (.)

    }
}
