package com.example.filmarchive.service;

import com.example.filmarchive.entity.Film;
import com.example.filmarchive.entity.Rating;
import com.example.filmarchive.entity.User;
import com.example.filmarchive.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Optional<Rating> findByUserAndFilm(User user, Film film) {
        return ratingRepository.findByUserAndFilm(user, film);
    }

    public List<Rating> findByFilm(Film film) {
        return ratingRepository.findByFilm(film);
    }

    public double calculateAverageRating(Film film) {
        List<Rating> ratings = ratingRepository.findByFilm(film);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        double total = ratings.stream().mapToInt(Rating::getScore).sum();
        return total / ratings.size();
    }
}
