package com.shopme.admin;

import com.shopme.admin.user.common.entity.Product;
import com.shopme.admin.user.common.entity.ProductImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
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

    public static  void setMainImageName(MultipartFile mainImageMultipart, Product product) {

        log.info("ProductController | setMainImageName is started");
        log.info("ProductController | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            log.info("ProductController | setMainImageName | fileName : " + fileName);
            product.setMainImage(fileName);
        }

        log.info("ProductController | setMainImageName is completed");
    }

    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                             Product product) {
        log.info("ProductController | setExistingExtraImageNames is started");
        if(imageIDs != null) {
            log.info("ProductController | deleteExtraImagesWeredRemovedOnForm | imageIDs  : " + imageIDs.toString());
        }
        if(imageNames != null) {
            log.info("ProductController | deleteExtraImagesWeredRemovedOnForm | imageNames  : " + imageNames.toString());
        }

        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    public static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        log.info("ProductController | setNewExtraImageNames is started");
        log.info("ProductController | setNewExtraImageNames | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {

            for (MultipartFile multipartFile : extraImageMultiparts) {
                log.info("ProductController | setNewExtraImageNames | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());

                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    log.info("ProductController | setNewExtraImageNames | fileName : " + fileName);

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }

        log.info("ProductController | setExtraImageNames is completed");
    }

    public static void setProductDetails(String[] detailIDs, String[] detailNames,
                                     String[] detailValues, Product product) {
        log.info("ProductController | setProductDetails is started");

        log.info("ProductController | setProductDetails | detailNames : " + detailNames.toString());
        log.info("ProductController | setProductDetails | detailNames : " + detailValues.toString());
        log.info("ProductController | setProductDetails | product : " + product.toString());

        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }

        log.info("ProductController | setProductDetails | product with its detail : " + product.getDetails().toString());
        log.info("ProductController | setProductDetails is completed");
    }

    public static void saveUploadedImages(MultipartFile mainImageMultipart,
                                          MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {

        log.info("ProductController | saveUploadedImages is started");
        log.info("ProductController | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

            log.info("ProductController | setMainImageName | fileName : " + fileName);
            String uploadDir = "../product-images/" + savedProduct.getId();
            log.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

            FileUploadUtil.cleanDir(uploadDir);

            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        log.info("ProductController | setMainImageName | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            log.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

            for (MultipartFile multipartFile : extraImageMultiparts) {

                log.info("ProductController | setMainImageName | multipartFile.isEmpty() : " + multipartFile.isEmpty());
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                log.info("ProductController | setMainImageName | fileName : " + fileName);

                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
        log.info("ProductController | saveUploadedImages is completed");
    }

    public static void deleteExtraImagesWeredRemovedOnForm(Product product) {
        log.info("ProductController | deleteExtraImagesWeredRemovedOnForm is started");

        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        log.info("ProductController | deleteExtraImagesWeredRemovedOnForm | dirPath  : " + dirPath);

        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        log.info("Deleted extra image: " + filename);

                    } catch (IOException e) {
                        log.error("Could not delete extra image: " + filename);
                    }
                }
            });
        } catch (IOException ex) {
            log.error("Could not list directory: " + dirPath);
        }
    }
}
