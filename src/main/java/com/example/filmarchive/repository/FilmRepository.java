package com.example.filmarchive.repository;

import com.example.filmarchive.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    // Arama işlemleri için özel metotlar

    List<Film> findByTitleContainingIgnoreCase(String title);

    List<Film> findByGenreContainingIgnoreCase(String genre);

    List<Film> findByYear(int year);

}
