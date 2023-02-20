package com.shopme.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

@Slf4j
public class FileUploadUtil {



    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        log.debug("uploadPath: {}", uploadPath.toAbsolutePath());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("saved file:{} to: {}" , fileName, filePath.toAbsolutePath());

        } catch (IOException ex) {
            throw new IOException("Could not save file:" + fileName, ex);
        }
    }

    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);
        try {
            Files.list(dirPath).forEach(file -> {
                        if (!Files.isDirectory(file)) {
                            try {
                                Files.delete(file);
                            } catch (IOException ex) {
                                log.error("Could not delete fileЖ {}", file);
                            }
                        }
                    }
            );
        } catch (IOException ex) {
            log.error("Could not list directory: {}", dirPath);
        }
    }

    public static void removeDir(String dir) {

        log.info("FileUploadUtil | removeDir is started");

        log.info("FileUploadUtil | removeDir | dir : " + dir);

        cleanDir(dir);

        log.info("FileUploadUtil | cleanDir(dir) is over");

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            log.error("Could not remove directory: " + dir);
        }

    }
}
