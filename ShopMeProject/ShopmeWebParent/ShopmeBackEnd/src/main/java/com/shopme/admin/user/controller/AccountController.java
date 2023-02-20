package com.shopme.admin.user.controller;


import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController   {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
                              Model model){
        String email = loggedUser.getUsername();

        User user = userService.getByEmail(email);
        model.addAttribute("user", user);

        return  "users/account_form";
    }

    @RequestMapping(value = "/account/update", method = RequestMethod.POST,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam(value = "image") MultipartFile multipartFile,
                           @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {

        if (multipartFile!= null &&  !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.updateAccount(user);
        }

        loggedUser.setLastName(user.getLastName());
        loggedUser.setFirstName(user.getFirstName());

        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");

        return  "redirect:/account";
    }
}
