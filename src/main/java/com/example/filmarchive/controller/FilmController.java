package com.example.filmarchive.controller;

import com.example.filmarchive.*;
import com.example.filmarchive.entity.Comment;
import com.example.filmarchive.entity.Film;
import com.example.filmarchive.entity.User;
import com.example.filmarchive.service.CommentService;
import com.example.filmarchive.service.FilmService;
import com.example.filmarchive.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.desktop.UserSessionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final CommentService commentService;
    private final UserService userService;

    public FilmController(FilmService filmService, CommentService commentService, UserService userService) {
        this.filmService = filmService;
        this.commentService = commentService;
        this.userService = userService;
    }

    // Film listesi sayfası
    @GetMapping
    public String listFilms(Model model) {
        List<Film> films = filmService.findAll();
        model.addAttribute("films", films);
        return "film_list";
    }

    // Film detay sayfası
    @GetMapping("/{id}")
    public String getFilmDetail(@PathVariable("id") Long id, Model model) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        List<Comment> comments = commentService.findByFilmId(id);
        model.addAttribute("film", film);
        model.addAttribute("comments", comments);
        return "film_detail";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long id,
                             @RequestParam String content,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        User user = userService.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Comment comment = new Comment();
        comment.setUsername(user.getUsername()); // Kullanıcının sistemdeki username'i alınacak
        comment.setContent(content);
        comment.setFilm(film);
        commentService.save(comment);

        return "redirect:/films/" + id;
    }
    // Film ekleme/güncelleme formu sayfası
    @GetMapping("/add")
    public String showFilmForm(Model model) {
        model.addAttribute("film", new Film());
        return "film_form";
    }

    // Film ekleme işlemi (Güncellenmiş hali!)
    @PostMapping
    public String addFilm(@ModelAttribute Film film, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            film.setImage(imageFile.getBytes()); // multipart dosyasını byte dizisine çeviriyoruz
        }
        filmService.save(film);
        return "redirect:/films";
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getFilmImage(@PathVariable("id") Long id) {
        Film film = filmService.findById(id).orElseThrow(() -> new RuntimeException("Film bulunamadı"));
        byte[] image = film.getImage();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // veya PNG'ye göre ayarla
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
