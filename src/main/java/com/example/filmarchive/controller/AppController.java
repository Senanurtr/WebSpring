package com.example.filmarchive.controller;

import com.example.filmarchive.entity.*;
import com.example.filmarchive.service.*;
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
    private final RatingService ratingService;

    public AppController(UserService userService, RoleService roleService, FilmService filmService,
                         CommentService commentService, RatingService ratingService) {
        this.userService = userService;
        this.roleService = roleService;
        this.filmService = filmService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    // ✅ LOGIN SAYFASI
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // ✅ REGISTER SAYFASI
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

    // ✅ TÜM FİLMLERİ LİSTELE
    @GetMapping("/films")
    public String listFilms(Model model) {
        List<Film> films = filmService.findAll();
        model.addAttribute("films", films);
        return "film_list";
    }

    // ✅ FİLM DETAYI + YORUMLAR + PUAN ORTALAMASI
    @GetMapping("/films/{id}")
    public String getFilmDetail(@PathVariable Long id, Model model) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        List<Comment> comments = commentService.findByFilmId(id);
        double averageRating = ratingService.calculateAverageRating(film);

        model.addAttribute("film", film);
        model.addAttribute("comments", comments);
        model.addAttribute("averageRating", averageRating);

        return "film_detail";
    }

    // ✅ FİLME YORUM EKLE
    @PostMapping("/films/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails principal) {

        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        User user = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Comment comment = new Comment();
        comment.setUsername(user.getUsername());
        comment.setContent(content);
        comment.setFilm(film);
        commentService.save(comment);

        return "redirect:/films/" + id;
    }

    // ✅ FİLME PUAN VER
    @PostMapping("/films/{id}/rate")
    public String rateFilm(@PathVariable Long id,
                           @RequestParam int score,
                           @AuthenticationPrincipal UserDetails principal) {

        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        User user = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Optional<Rating> existingRating = ratingService.findByUserAndFilm(user, film);

        Rating rating = existingRating.orElseGet(() -> new Rating(user, film, score));
        rating.setScore(score); // güncelle veya ilk defa ver

        ratingService.save(rating);
        return "redirect:/films/" + id;
    }

    // ✅ FİLM EKLEME FORMU
    @GetMapping("/films/add")
    public String showFilmForm(Model model) {
        model.addAttribute("film", new Film());
        return "film_form";
    }

    // ✅ FİLM KAYDET
    @PostMapping("/films")
    public String addFilm(@ModelAttribute Film film,
                          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            film.setImage(imageFile.getBytes());
        }
        filmService.save(film);
        return "redirect:/films";
    }

    // ✅ GÖRSEL GETİR
    @GetMapping("/films/{id}/image")
    public ResponseEntity<byte[]> getFilmImage(@PathVariable Long id) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        byte[] image = film.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
