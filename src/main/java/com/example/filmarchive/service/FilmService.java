package com.example.filmarchive.service;

import com.example.filmarchive.entity.Film;
import com.example.filmarchive.repository.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    // Tüm filmleri getir
    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    // ID'ye göre film bul
    public Optional<Film> findById(Long id) {
        return filmRepository.findById(id);
    }

    // Film ekle/güncelle
    public Film save(Film film) {
        return filmRepository.save(film);
    }
    public List<Film> findByGenreContainingIgnoreCase(String genre) {
        return filmRepository.findByGenreContainingIgnoreCase(genre);
    }
    public List<String> findAllGenres() {
        return filmRepository.findAll().stream()
                .map(Film::getGenre)
                .distinct()
                .toList();
    }


    public List<Film> findByTitleContaining(String title) {
        return filmRepository.findByTitleContainingIgnoreCase(title);
    }

    public void deleteById(Long id) {
        filmRepository.deleteById(id); // 📌 Direkt ID ile silme işlemi
    }
}
