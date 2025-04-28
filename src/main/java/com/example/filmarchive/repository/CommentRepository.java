package com.example.filmarchive.repository;

import com.example.filmarchive.entity.Comment;
import com.example.filmarchive.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByFilmId(Long filmId);
}
