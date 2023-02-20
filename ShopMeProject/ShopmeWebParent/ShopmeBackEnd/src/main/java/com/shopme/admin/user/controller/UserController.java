package com.shopme.admin.user.controller;


import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.admin.error.ResourceNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.common.entity.Role;
import com.shopme.admin.user.common.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "id", "asc",null);
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = userService.listRoles();

        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New User");

        model.addAttribute("listRoles", listRoles);

        return "users/user_form";
    }


    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {
        logger.info("Sort field: {}", sortField);
        logger.info("Sort field: {}", sortDir);

        final Page<User> page = userService.listByPage(pageNum, sortField, sortDir,keyword);
        final List<User> listUsers = page.getContent();


        long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalPages()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "users/users";
    }

    @RequestMapping(value = "/users/save", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveUser(User user, RedirectAttributes redirectAttributes
            , @RequestParam(value = "image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = userService.save(user);
            String uploadDir = "../user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has benn saved successfully");

        return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user) {
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID:" + id + ")");

            List<Role> listRoles = userService.listRoles();
            model.addAttribute("listRoles", listRoles);

            return "users/user_form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }


    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id +
                    "has been deleted successfully");

        } catch (UsernameNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {

        userService.updateUsed(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The used ID:" + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }


    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response ) throws IOException {
        final List<User> userList = userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();

        exporter.export(userList, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response ) throws IOException {
        final List<User> userList = userService.listAll();
        UserExcelExporter exporter = new UserExcelExporter();

        exporter.export(userList, response);
    }



    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {

        logger.info("UserController | exportToPDF is called");

        List<User> listUsers = userService.listAll();

        logger.info("UserController | exportToPDF | listUsers.size() : " + listUsers.size());

        UserPdfExporter exporter = new UserPdfExporter();

        logger.info("UserController | exportToPDF | export is starting");

        exporter.export(listUsers, response);

        logger.info("UserController | exportToPDF | export completed");
    }

}
