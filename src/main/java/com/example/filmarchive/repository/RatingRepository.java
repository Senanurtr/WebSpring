package com.example.filmarchive.repository;

import com.example.filmarchive.entity.Rating;
import com.example.filmarchive.entity.Film;
import com.example.filmarchive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndFilm(User user, Film film);
    List<Rating> findByFilm(Film film);
}