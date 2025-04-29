
package com.example.filmarchive.config;

import com.example.filmarchive.entity.*;
import com.example.filmarchive.repository.*;
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
            CommentRepository commentRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Kullanıcılar ve roller
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            // Filmler
            Film film1 = new Film();
            film1.setTitle("Inception");
            film1.setGenre("Sci-Fi");
            film1.setYear(2010);
            film1.setDescription("A mind-bending thriller by Christopher Nolan.");
            film1.setImage(loadImageAsBytes("inception.jpg"));
            film1.setRating(4.7);

            Film film2 = new Film();
            film2.setTitle("The Godfather");
            film2.setGenre("Crime");
            film2.setYear(1972);
            film2.setDescription("A classic mafia story.");
            film2.setImage(loadImageAsBytes("godfather.jpg"));
            film2.setRating(4.2);

            Film film3 = new Film();
            film3.setTitle("Interstellar");
            film3.setGenre("Sci-Fi");
            film3.setYear(2014);
            film3.setDescription("Exploration of time, space, and love.");
            film3.setImage(loadImageAsBytes("interstellar.jpg"));
            film3.setRating(3.6);

            Film film4 = new Film();
            film4.setTitle("Recep İvedik");
            film4.setGenre("Comedy");
            film4.setYear(2008);
            film4.setDescription("Kaba saba ama komik bir karakterin şehir maceraları.");
            film4.setImage(loadImageAsBytes("reco.jpg"));
            film4.setRating(2.1);

            filmRepository.save(film1);
            filmRepository.save(film2);
            filmRepository.save(film3);
            filmRepository.save(film4);

            // Kullanıcılar ve yorumlar (Inception)
            User kelly = createUser(userRepository, "Kelly Chan", passwordEncoder, User.Role.USER);
            createComment(commentRepository, kelly, "Inception şüphesiz tüm zamanların en sevdiğim filmlerinden biri.", film1);

            User suman = createUser(userRepository, "Suman Dey", passwordEncoder, User.Role.USER);
            createComment(commentRepository, suman, "Karmaşıklığına rağmen Inception tekrarlanan izlemeleri ödüllendiriyor.", film1);

            User devyansh = createUser(userRepository, "Devyansh Chadha", passwordEncoder, User.Role.USER);
            createComment(commentRepository, devyansh, "Hayatımda tekrar izlediğim tek filmdi, çok güzel ve akılda kalıcı!", film1);

            // Kullanıcılar ve yorumlar (The Godfather)
            User ralph = createUser(userRepository, "Ralph Rahal", passwordEncoder, User.Role.USER);
            createComment(commentRepository, ralph, "Efsanevi Francis Ford Coppola'nın yönettiği Baba (1972), filmde hikaye anlatımını yeniden tanımlayan sinematik bir başyapıttan başka bir şey değil.", film2);

            User facundo = createUser(userRepository, "Facundo Perez Ridela", passwordEncoder, User.Role.USER);
            createComment(commentRepository, facundo, "\"Sana reddedemeyeceğin bir teklif yapacağım.\"\n🌹Baba, gerçek bir Sinematografik Başyapıt.", film2);

            User ricardo = createUser(userRepository, "Ricardo M.", passwordEncoder, User.Role.USER);
            createComment(commentRepository, ricardo, "Gelmiş geçmiş en iyi film, HARİKA bir film! Tarihin en iyi draması, bir klasik!", film2);

            // Kullanıcılar ve yorumlar (Interstellar)
            User jay = createUser(userRepository, "Jay Neill", passwordEncoder, User.Role.USER);
            createComment(commentRepository, jay, "Interstellar'ı izlemek, jenerik yayınlandıktan çok sonra bile beni hayranlık içinde bıraktı.", film3);

            User word = createUser(userRepository, "Word World", passwordEncoder, User.Role.USER);
            createComment(commentRepository, word, "Christopher Nolan'ın en iyi eseri, \"Inception\" ve Batman üçlemesini geride bırakıyor.", film3);

            User priya = createUser(userRepository, "Priyabrata Panda", passwordEncoder, User.Role.USER);
            createComment(commentRepository, priya, "Mükemmel bir film. Hans Zimmer'ın müziği her zaman olduğu gibi bizi duygulandırıyor.", film3);
        };
    }

    // Yeni bir kullanıcı oluşturma metodu
    private User createUser(UserRepository userRepository, String username, PasswordEncoder passwordEncoder, User.Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password")); // Default şifre
        user.setRole(role);
        return userRepository.save(user);
    }

    // Yeni bir yorum oluşturma metodu
    private void createComment(CommentRepository commentRepository, User user, String content, Film film) {
        Comment comment = new Comment();
        comment.setUsername(user.getUsername());
        comment.setContent(content);
        comment.setFilm(film);
        commentRepository.save(comment);
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
