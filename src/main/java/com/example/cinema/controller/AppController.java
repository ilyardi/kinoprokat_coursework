package com.example.cinema.controller;

import com.example.cinema.UserContext;
import com.example.cinema.entity.Movie;
import com.example.cinema.entity.Review;
import com.example.cinema.entity.User;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.ReviewService;
import com.example.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class AppController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String viewHomePage(Model model, @Param("keyword") String keyword) {
        List<Movie> listMovies = movieService.listAll(keyword);
        model.addAttribute("listMovies", listMovies);
        model.addAttribute("keyword", keyword);
        List<Movie> movies = movieService.findTop6Movies();
        model.addAttribute("movies", movies);
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/movies/{id}")
    public String showMovieDetails(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);

        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "movie_details";
    }

    @RequestMapping("/new")
    public String showNewMovieForm(Model model) {
        Movie movie = new Movie();
        model.addAttribute("movies", movie);
        return "new_movie";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<Object> saveMovie(@RequestBody Movie movie) {
        movieService.save(movie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.get(id);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    @RequestMapping("/delete/{id}")
    public String deleteMovie(@PathVariable(name = "id") Long id) {
        movieService.delete(id);
        return "redirect:/movies";
    }

    @RequestMapping("/movies")
    public String movies(Model model, @Param("keyword") String keyword) {
        List<Movie> listMovies = movieService.listAll(keyword);
        model.addAttribute("listMovies", listMovies);
        model.addAttribute("keyword", keyword);

        boolean noMoviesFound = listMovies.isEmpty();
        model.addAttribute("noMoviesFound", noMoviesFound);

        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "movies";
    }
    @GetMapping("/genreCount")
    @ResponseBody
    public Map<String, Integer> getGenreCount() {
        List<Movie> movies = movieService.listAll(null); // Получаем все фильмы
        Map<String, Integer> genreCount = new HashMap<>();

        for (Movie movie : movies) {
            String genre = movie.getGenre(); // Получаем жанр фильма
            genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1); // Увеличиваем счетчик для жанра
        }
        return genreCount; // Возвращаем карту с данными
    }


    @GetMapping("/reviews")
    public String showReviewsPage(@RequestParam(required = false) Long movieId, Model model) {
        List<Review> reviews;

        if (movieId != null) {
            // Получить отзывы для конкретного фильма
            reviews = reviewService.getReviewsByMovieId(movieId);
        } else {
            // Получить все отзывы
            reviews = reviewService.getAllReviews();
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("movieId", movieId); // Для сохранения состояния фильтра
        model.addAttribute("movies", movieService.getAllMovies()); // Список фильмов
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "reviews";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam String text, @RequestParam Long movieId) {
        User currentUser = UserContext.getInstance().getCurrentUser();

        if (currentUser != null) {
            Movie movie = movieService.getMovieById(movieId);
            if (movie != null) {
                Review review = new Review();
                review.setUser(currentUser);
                review.setText(text);
                review.setMovie(movie);
                review.setDate(LocalDateTime.now());
                reviewService.saveReview(review);
            }
        }
        return "redirect:/reviews";
    }
    @GetMapping("/admin")
    public String showAdminPanel(Model model, @Param("keyword") String keyword) {
        User currentUser = UserContext.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            return "redirect:/"; // Перенаправление, если пользователь не администратор
        }

        List<User> users = userService.findAllUsers(); // Получаем список всех пользователей
        model.addAttribute("users", users);
        model.addAttribute("user", currentUser);
        List<Movie> listMovies = movieService.listAll(keyword);
        model.addAttribute("listMovies", listMovies);
        model.addAttribute("keyword", keyword);

        boolean noMoviesFound = listMovies.isEmpty();
        model.addAttribute("noMoviesFound", noMoviesFound);


        return "admin"; // Возвращаем страницу админа
    }

    @PostMapping("/admin/changeRole")
    public String changeUserRole(@RequestParam Long userId, @RequestParam String newRole, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserById(userId);

        if (user.isEmpty()) {
            // Добавляем сообщение об ошибке в атрибуты редиректа
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/admin"; // Редирект с ошибкой
        }

        user.get().setRole(newRole);
        userService.saveUser(user.get()); // Сохранение изменений в базе данных

        // Добавляем сообщение об успешном изменении роли
        redirectAttributes.addFlashAttribute("successMessage", "Роль успешно изменена");

        return "redirect:/admin"; // Редирект на страницу /blog/admin
    }

}