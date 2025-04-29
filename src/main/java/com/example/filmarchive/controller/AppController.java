package com.example.filmarchive.controller;


import com.example.filmarchive.entity.*;
import com.example.filmarchive.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class AppController {

    private final UserService userService;
    private final RoleService roleService;
    private final FilmService filmService;
    private final CommentService commentService;

    public AppController(UserService userService, RoleService roleService, FilmService filmService, CommentService commentService) {
        this.userService = userService;
        this.roleService = roleService;
        this.filmService = filmService;
        this.commentService = commentService;
    }

    // 📌 GİRİŞ SAYFASI
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 📌 KAYIT SAYFASI
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        Role userRole = roleService.findByName("USER");
        if (userRole != null) {
            user.setRoles(Collections.singleton(userRole));
        }
        userService.save(user);
        return "redirect:/login?success";
    }

    // 📌 TÜM FİLMLER
    @GetMapping("/films")
    public String listFilms(Model model) {
        List<Film> films = filmService.findAll();
        model.addAttribute("films", films);
        return "film_list";
    }
    @GetMapping("/search")
    public String searchFilms(@RequestParam("query") String query, Model model) {
        List<Film> foundFilms = filmService.findByTitleContaining(query);
        model.addAttribute("films", foundFilms);
        return "film_list"; // search_results.html sayfasına yönlendir
    }


    // 📌 FİLM DETAY
    @GetMapping("/films/{id}")
    public String getFilmDetail(@PathVariable Long id, Model model) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        List<Comment> comments = commentService.findByFilmId(id);
        model.addAttribute("film", film);
        model.addAttribute("comments", comments);
        return "film_detail";
    }

    // 📌 YORUM EKLE
    @PostMapping("/films/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails principal) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        User user = userService.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Comment comment = new Comment();
        comment.setUsername(user.getUsername());
        comment.setContent(content);
        comment.setFilm(film);
        commentService.save(comment);

        return "redirect:/films/" + id;
    }

    // 📌 FİLM EKLE FORMU
    @GetMapping("/films/add")
    public String showFilmForm(Model model) {
        model.addAttribute("film", new Film());
        return "film_form";
    }

    // 📌 FİLM EKLEME POST
    @PostMapping("/films")
    public String addFilm(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            film.setImage(imageFile.getBytes());
        }
        filmService.save(film);
        return "redirect:/films";
    }

    // 📌 GÖRSEL GETİR
    @GetMapping("/films/{id}/image")
    public ResponseEntity<byte[]> getFilmImage(@PathVariable Long id) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        byte[] image = film.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
