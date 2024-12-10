package com.example.cinema.controller;

import com.example.cinema.UserContext;
import com.example.cinema.entity.Movie;
import com.example.cinema.entity.User;
import com.example.cinema.service.MovieService;
import com.example.cinema.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private MovieService movieService;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String index(HttpServletRequest request) {
        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {

        Optional<User> user = userService.findUserByUsername(username);

        if (user.isPresent()) {
            if (BCrypt.checkpw(password, user.get().getPassword())) {
                UserContext.getInstance().setCurrentUser(user.get());

                String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
                if (redirectUrl != null) {
                    return "redirect:" + redirectUrl;
                }
            }
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationForm(HttpServletRequest request, Model model) {
        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);

        if (!model.containsAttribute("username")) {
            model.addAttribute("username", "");
        }
        if (!model.containsAttribute("email")) {
            model.addAttribute("email", "");
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               HttpServletRequest request,
                               Model model) {

        String redirectUrl = request.getHeader("Referer");
        request.getSession().setAttribute("redirectUrl", redirectUrl);

        Optional<User> userByEmail = userService.findByEmail(email);
        Optional<User> userByUsername = userService.findByUsername(username);

        if (userByUsername.isPresent()) {
            model.addAttribute("username", "Имя уже занято");
            return "redirect:" + redirectUrl;
        }

        if (userByEmail.isPresent()) {
            model.addAttribute("email", "Email уже используется");
            return "redirect:" + redirectUrl;
        }

        userService.saveUser(new User(username, password, email, firstName, lastName));

        Optional<User> newUser = userService.findUserByUsername(username);

        if (newUser.isPresent()) {
            UserContext.getInstance().setCurrentUser(newUser.get());
        }

        if (redirectUrl != null) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        String redirectUrl = request.getHeader("Referer");
        UserContext.getInstance().clearCurrentUser();

        if (redirectUrl != null && redirectUrl.contains("/profile")) {
            return "redirect:/";
        }

        if (redirectUrl != null) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String getUserProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/edit_profile")
    public String editProfile(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "redirect:/profile/" + id;
    }

    @PostMapping("/edit_profile/{id}")
    public String updateProfile(@PathVariable("id") Long id,
                                @RequestParam("username") String username,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("user", userService.getUserById(id)); // добавляем пользователя в модель
            return "redirect:/profile/" + id;
        }

        User user = userService.getUserById(id);
        if (user != null) {
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            if (!password.isEmpty()) {
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            }

            userService.saveUser(user);
        }

        return "redirect:/profile/" + id;
    }
    @GetMapping("/aboutAuthor")
    public String showAboutAuthorPage(Model model) {
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) { // Проверяем, что пользователь существует
            model.addAttribute("user", user);
        }
        return "aboutAuthor";
    }

    @GetMapping("/favourites")
    public String viewFavourites(Model model) {
        User user = UserContext.getInstance().getCurrentUser();
        if (user != null) {
            model.addAttribute("user", user);
        }
        User currentUser = UserContext.getInstance().getCurrentUser(); // Получаем текущего пользователя
        if (currentUser != null) {
            List<Movie> favouriteMovies = movieService.getFavouritesByUser(currentUser.getId());
            model.addAttribute("favouriteMovies", favouriteMovies);
        }
        return "favourites"; // Возвращаем страницу избранного
    }


    @PostMapping("/add-to-favourites/{movieId}")
    public String addToFavourites(@PathVariable Long movieId) {
        User currentUser = UserContext.getInstance().getCurrentUser(); // Получаем текущего пользователя
        if (currentUser != null) {
            movieService.addFavourite(currentUser.getId(), movieId);
        }
        return "redirect:/favourites"; // Перенаправление на страницу избранного
    }

    @PostMapping("/remove-from-favourites/{movieId}")
    public String removeFromFavourites(@PathVariable Long movieId) {
        User currentUser = UserContext.getInstance().getCurrentUser(); // Получаем текущего пользователя
        if (currentUser != null) {
            movieService.removeFavourite(currentUser.getId(), movieId);
        }
        return "redirect:/favourites";
    }

}
