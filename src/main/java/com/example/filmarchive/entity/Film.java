package com.example.filmarchive.entity;

import jakarta.persistence.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Entity
@Table(name = "films")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String genre;

    private int year;
    @Column(nullable = false)
    private double rating;
    @Column(nullable = false)
    private int voteCount = 100; // başlangıçta sanki 100 kişi oylamış gibi davranacağız

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Lob
    private byte[] image;

    private String description;

    public Film() {
    }

    public Film(Long id, String title, String genre, int year, byte[] image, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.image = image;
        this.description = description;
    }

    // Getter ve Setter'lar
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] loadImageAsBytes(String filename) {
        try {
            Path imagePath = Path.of("src/main/resources/static/images/" + filename);
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
