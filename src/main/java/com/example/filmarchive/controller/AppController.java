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

    private final FilmService filmService;
    private final CommentService commentService;
    private final RatingService ratingService;

    public AppController(UserService userService, FilmService filmService,
                         CommentService commentService, RatingService ratingService) {
        this.userService = userService;
        this.filmService = filmService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    // ✅ LOGIN SAYFASI
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    // ✅ Film Kaydetme ve Ana Sayfaya Dönme
    @PostMapping("/films")
    public String saveFilm(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            film.setImage(imageFile.getBytes());
        }
        filmService.save(film);
        return "redirect:/films";
    }

    // ✅ REGISTER SAYFASI
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam String confirmPassword,
                               Model model) {
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Bu kullanıcı adı zaten alınmış!");
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("passwordError", "Şifreler uyuşmuyor!"); // ✅ Şifreler eşleşmezse hata mesajı gönder
            return "register";
        }

        user.setRole(User.Role.USER);
        userService.save(user);
        return "redirect:/login?success";
    }

    @GetMapping("/films")
    public String listFilms(@RequestParam(value = "genre", required = false) String genre,
                            @RequestParam(value = "sort", required = false) String sort,
                            @RequestParam(value = "query", required = false) String query,
                            Model model) {

        List<Film> films;

        if (query != null && !query.isEmpty()) {
            films = filmService.findByTitleContaining(query);
        } else if (genre != null && !genre.isEmpty()) {
            films = filmService.findByGenreContainingIgnoreCase(genre);
        } else {
            films = filmService.findAll();
        }

        if (sort != null) {
            switch (sort) {
                case "yearAsc" -> films.sort(Comparator.comparing(Film::getYear));
                case "yearDesc" -> films.sort(Comparator.comparing(Film::getYear).reversed());
                case "ratingAsc" -> films.sort(Comparator.comparing(Film::getRating));
                case "ratingDesc" -> films.sort(Comparator.comparing(Film::getRating).reversed());
            }
        }

        // 📌 ✅ Türler artık film listesine bağlı değil, her zaman tam listeyi alıyoruz!
        List<String> genres = filmService.findAllGenres();

        model.addAttribute("films", films);
        model.addAttribute("genres", genres);
        model.addAttribute("selectedGenre", genre);

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
        if (existingRating.isPresent()) {
            double totalRating = film.getRating() * film.getVoteCount() - existingRating.get().getScore() + score;
            film.setRating(totalRating / film.getVoteCount());
        } else {
            double totalRating = film.getRating() * film.getVoteCount() + score;
            film.setVoteCount(film.getVoteCount() + 1);
            film.setRating(totalRating / film.getVoteCount());
        }

        rating.setScore(score);
        filmService.save(film);
        ratingService.save(rating);
        return "redirect:/films/" + id;
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

    // 📌 ADMIN: Film Ekleme Formu
    @GetMapping("/admin/films/add")
    public String showAdminFilmForm(Model model) {
        model.addAttribute("film", new Film());
        return "add_film";
    }

    @PostMapping("/admin/films")
    public String addFilmAdmin(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (film.getId() != null) { // 🔹 Güncellenen bir film mi?
            Film existingFilm = filmService.findById(film.getId())
                    .orElseThrow(() -> new RuntimeException("Film bulunamadı"));

            if (!imageFile.isEmpty()) {
                film.setImage(imageFile.getBytes()); // ✅ Yeni resim varsa güncelle
            } else {
                film.setImage(existingFilm.getImage()); // ✅ Eski resmi koru!
            }
        }
        else { // 📌 Yeni bir film ekleniyorsa, resim kontrolünü ekle!
            if (!imageFile.isEmpty()) {
                film.setImage(imageFile.getBytes()); // ✅ Yeni resim ekleniyorsa kaydet!
            } else {
                throw new IllegalArgumentException("Film eklerken resim zorunludur!"); // 📌 Uyarı ekleyebilirsin
            }
        }

        filmService.save(film);
        return "redirect:/films?updateSuccess"; // 🔹 Film başarıyla kaydedildiğinde yönlendir
    }

    @GetMapping("/admin/films/edit/{id}")
    public String showEditFilmForm(@PathVariable Long id, Model model) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        model.addAttribute("film", film); // Mevcut bilgileri modele ekle
        return "add_film"; // Güncellenmiş sayfa ile aç
    }

    @PostMapping("/admin/films/delete/{id}")
    public String deleteFilm(@PathVariable Long id) {
        try {
            Film film = filmService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Film bulunamadı"));

            filmService.deleteById(id);
            return "redirect:/films?deleteSuccess"; // ✅ Silme başarılı olursa yönlendirme yap
        } catch (RuntimeException e) {
            return "redirect:/films?error=FilmSilinemedi"; // ❌ Silme hatası varsa hata mesajı ile yönlendir
        }
    }
}