package com.texas.developers.ams.controller;


import com.texas.developers.ams.configuration.service.user.UserService;
import com.texas.developers.ams.dto.UserDto;
import com.texas.developers.ams.enums.RoleEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("usersDtoList", userService.getAllUsers());
        return "usermanagement/user-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", Arrays.asList(RoleEnum.values()));
        return "usermanagement/user";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute UserDto dto) {
        userService.saveUser(dto);
        return "redirect:/users";
    }
    @GetMapping("/toggle/{id}")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status!");
        }
        return "redirect:/users";
    }
}