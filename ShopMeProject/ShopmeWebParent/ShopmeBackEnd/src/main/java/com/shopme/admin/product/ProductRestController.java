package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.common.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private ProductService service;

    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
        return service.checkUnique(id, name);
    }

    @PostMapping("/rest/save")
    public ResponseEntity<Product> saveProduct(Product product,
                                                @RequestParam("fileImage") MultipartFile mainImageMultipart,
                                                @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                                                @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                                                @RequestParam(name = "detailNames", required = false) String[] detailNames,
                                                @RequestParam(name = "detailValues", required = false) String[] detailValues,
                                                @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                                                @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {

        log.info("ProductController | saveProduct is started");
        log.info("ProductController | saveProduct | mainImageMultipart.isEmpty() : " + mainImageMultipart.isEmpty());
        log.info("ProductController | saveProduct | extraImageMultiparts size : " + extraImageMultiparts.length);

        FileUploadUtil.setMainImageName(mainImageMultipart, product);
        FileUploadUtil.setExistingExtraImageNames(imageIDs, imageNames, product);
        FileUploadUtil.setNewExtraImageNames(extraImageMultiparts, product);
        FileUploadUtil.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = service.save(product);

        FileUploadUtil.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
        FileUploadUtil.deleteExtraImagesWeredRemovedOnForm(product);

        return ResponseEntity.ok(savedProduct);
    }
}