package com.shopme.admin.product;


import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brands.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.error.ProductNotFoundException;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.common.entity.Brand;
import com.shopme.admin.user.common.entity.Category;
import com.shopme.admin.user.common.entity.Product;
import com.shopme.admin.user.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private ProductService productService;

    private BrandService brandService;
    private CategoryService categoryService;


    @Autowired
    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        super();
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        LOGGER.info("ProductController | listFirstPage is started");

        return listByPage(1, model, "name", "asc", null, 0);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") int pageNum, Model model,
            @Param("sortField") String sortField, @Param("sortDir") String sortDir,
            @Param("keyword") String keyword,
            @Param("categoryId")Integer categoryId) {

        LOGGER.info("ProductController | listByPage is started");

        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> listProducts = page.getContent();

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        LOGGER.info("ProductController | listByPage | listProducts size : " + listProducts.size() );

        long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        LOGGER.info("ProductController | listByPage | endCount : " + endCount );
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        LOGGER.info("ProductController | listByPage | reverseSortDir : " + reverseSortDir );

        if(categoryId != null){
            model.addAttribute("categoryId", categoryId);
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {

        LOGGER.info("ProductController | newProduct is started");

        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        LOGGER.info("ProductController | newProduct | product : " + product);
        LOGGER.info("ProductController | newProduct | listBrands : " + listBrands.size());


        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("numberOfExistingExtraImages", 0);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
                              @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopmeUserDetails loggedUser
    ) throws IOException {

        LOGGER.info("ProductController | saveProduct is started");

        LOGGER.info("ProductController | saveProduct | mainImageMultipart.isEmpty() : " + mainImageMultipart.isEmpty());

        LOGGER.info("ProductController | saveProduct | extraImageMultiparts size : " + extraImageMultiparts.length);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
            if (loggedUser.hasRole("Salesperson")) {
                productService.saveProductPrice(product);
                ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");

                return "redirect:/products";
            }
        }

        setMainImageName(mainImageMultipart, product);

        setExistingExtraImageNames(imageIDs, imageNames, product);

        setNewExtraImageNames(extraImageMultiparts, product);

        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        deleteExtraImagesWeredRemovedOnForm(product);

        ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");

        return "redirect:/products";

    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

        LOGGER.info("ProductController | updateCategoryEnabledStatus is started");

        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";

        LOGGER.info("ProductController | updateCategoryEnabledStatus | status : " + status);

        String message = "The Product ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("messageSuccess", message);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        LOGGER.info("ProductController | deleteProduct is started");

        try {
            productService.delete(id);

            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            LOGGER.info("ProductController | deleteProduct | productExtraImagesDir : " + productExtraImagesDir);
            LOGGER.info("ProductController | deleteProduct | productImagesDir : " + productImagesDir);

            FileUploadUtil.removeDir(productExtraImagesDir);

            FileUploadUtil.removeDir(productImagesDir);

            LOGGER.info("ProductController | deleteProduct is done");

            redirectAttributes.addFlashAttribute("messageSuccess",
                    "The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException ex) {

            LOGGER.info("ProductController | deleteProduct | messageError : " + ex.getMessage());

            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }

        return "redirect:/products";
    }

    private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        LOGGER.info("ProductController | setNewExtraImageNames is started");
        LOGGER.info("ProductController | setNewExtraImageNames | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {

            for (MultipartFile multipartFile : extraImageMultiparts) {

                LOGGER.info("ProductController | setNewExtraImageNames | !multipartFile.isEmpty() : " + !multipartFile.isEmpty());

                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    LOGGER.info("ProductController | setNewExtraImageNames | fileName : " + fileName);

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }

        LOGGER.info("ProductController | setExtraImageNames is completed");
    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {

        LOGGER.info("ProductController | setMainImageName is started");

        LOGGER.info("ProductController | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {


            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

            LOGGER.info("ProductController | setMainImageName | fileName : " + fileName);

            product.setMainImage(fileName);

        }


        LOGGER.info("ProductController | setMainImageName is completed");
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart,
                                    MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {

        LOGGER.info("ProductController | saveUploadedImages is started");

        LOGGER.info("ProductController | setMainImageName | !mainImageMultipart.isEmpty() : " + !mainImageMultipart.isEmpty());

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

            LOGGER.info("ProductController | setMainImageName | fileName : " + fileName);

            String uploadDir = "../product-images/" + savedProduct.getId();

            LOGGER.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

            FileUploadUtil.cleanDir(uploadDir);

            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        LOGGER.info("ProductController | setMainImageName | extraImageMultiparts.length : " + extraImageMultiparts.length);

        if (extraImageMultiparts.length > 0) {

            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            LOGGER.info("ProductController | setMainImageName | uploadDir : " + uploadDir);

            for (MultipartFile multipartFile : extraImageMultiparts) {

                LOGGER.info("ProductController | setMainImageName | multipartFile.isEmpty() : " + multipartFile.isEmpty());
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                LOGGER.info("ProductController | setMainImageName | fileName : " + fileName);

                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }
        }


        LOGGER.info("ProductController | saveUploadedImages is completed");
    }

    private void setProductDetails(String[] detailIDs, String[] detailNames,
                                   String[] detailValues, Product product) {
        LOGGER.info("ProductController | setProductDetails is started");

        LOGGER.info("ProductController | setProductDetails | detailNames : " + detailNames.toString());
        LOGGER.info("ProductController | setProductDetails | detailNames : " + detailValues.toString());
        LOGGER.info("ProductController | setProductDetails | product : " + product.toString());


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

        LOGGER.info("ProductController | setProductDetails | product with its detail : " + product.getDetails().toString());

        LOGGER.info("ProductController | setProductDetails is completed");
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra) {

        LOGGER.info("ProductController | editProduct is started");

        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            LOGGER.info("ProductController | editProduct | product  : " + product.toString());
            LOGGER.info("ProductController | editProduct | listBrands : " + listBrands.toString());
            LOGGER.info("ProductController | editProduct | numberOfExistingExtraImages : " + numberOfExistingExtraImages);

            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);


            return "products/product_form";

        } catch (ProductNotFoundException e) {

            LOGGER.info("ProductController | editProduct | error : " + e.getMessage());

            ra.addFlashAttribute("messageError", e.getMessage());

            return "redirect:/products";
        }
    }

    private void deleteExtraImagesWeredRemovedOnForm(Product product) {
        LOGGER.info("ProductController | deleteExtraImagesWeredRemovedOnForm is started");

        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        LOGGER.info("ProductController | deleteExtraImagesWeredRemovedOnForm | dirPath  : " + dirPath);

        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: " + filename);

                    } catch (IOException e) {
                        LOGGER.error("Could not delete extra image: " + filename);
                    }
                }

            });
        } catch (IOException ex) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                            Product product) {

        LOGGER.info("ProductController | setExistingExtraImageNames is started");
        if(imageIDs != null) {
            LOGGER.info("ProductController | deleteExtraImagesWeredRemovedOnForm | imageIDs  : " + imageIDs.toString());
        }
        if(imageNames != null) {
            LOGGER.info("ProductController | deleteExtraImagesWeredRemovedOnForm | imageNames  : " + imageNames.toString());
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

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra) {
        try {
            Product product = productService.get(id);

            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            LOGGER.info("ProductController | editProduct | error : " + e.getMessage());
            ra.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/products";
        }
    }

}