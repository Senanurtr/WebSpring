package com.example.filmarchive.controller;

import com.example.filmarchive.entity.User;
import com.example.filmarchive.service.UserService;
import com.example.filmarchive.service.RoleService;
import com.example.filmarchive.entity.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Kayıt formunu göster
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Kayıt formunu işleme
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        Role userRole = roleService.findByName("USER");
        if (userRole != null) {
            user.setRoles(Collections.singleton(userRole));
        }
        userService.save(user);
        return "redirect:/login";
    }
}
