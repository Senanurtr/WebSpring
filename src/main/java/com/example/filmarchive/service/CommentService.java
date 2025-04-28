package com.example.filmarchive.service;

import com.example.filmarchive.entity.Comment;
import com.example.filmarchive.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Film ID'ye göre yorumları getir
    public List<Comment> findByFilmId(Long filmId) {
        return commentRepository.findByFilmId(filmId);
    }

    // Yorum ekle
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }
}
