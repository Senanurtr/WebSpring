package com.example.filmarchive.config;

import com.example.filmarchive.entity.Film;
import com.example.filmarchive.entity.User;
import com.example.filmarchive.repository.FilmRepository;
import com.example.filmarchive.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            FilmRepository filmRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ✅ Admin kullanıcı
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(User.Role.ADMIN); // Yeni ENUM kullanımı
            userRepository.save(admin);

            // ✅ Normal kullanıcı
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(User.Role.USER); // Yeni ENUM kullanımı
            userRepository.save(user);



            // ✅ Filmler
            Film film1 = new Film();
            film1.setTitle("Inception");
            film1.setGenre("Sci-Fi");
            film1.setYear(2010);
            film1.setDescription("A mind-bending thriller by Christopher Nolan.");
            film1.setImage(loadImageAsBytes("inception.jpg"));
            film1.setRating(8.7);

            Film film2 = new Film();
            film2.setTitle("The Godfather");
            film2.setGenre("Crime");
            film2.setYear(1972);
            film2.setDescription("A classic mafia story.");
            film2.setImage(loadImageAsBytes("godfather.jpg"));
            film2.setRating(9.2);

            Film film3 = new Film();
            film3.setTitle("Interstellar");
            film3.setGenre("Sci-Fi");
            film3.setYear(2014);
            film3.setDescription("Exploration of time, space, and love.");
            film3.setImage(loadImageAsBytes("interstellar.jpg"));
            film3.setRating(8.6);


            Film film4 = new Film();
            film4.setTitle("Recep İvedik");
            film4.setGenre("Comedy");
            film4.setYear(2008);
            film4.setDescription("Kaba saba ama komik bir karakterin şehir maceraları.");
            film4.setImage(loadImageAsBytes("reco.jpg"));
            film4.setRating(3.1);

            filmRepository.save(film1);
            filmRepository.save(film2);
            filmRepository.save(film3);
            filmRepository.save(film4);
        };
    }

    // Görseli byte dizisine çeviren metod
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