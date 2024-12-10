package com.example.cinema.service;

import com.example.cinema.entity.Movie;
import com.example.cinema.entity.UserFavourites;
import com.example.cinema.repository.MovieRepository;
import com.example.cinema.repository.UserFavouritesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repo;

    public List<Movie> listAll(String keyword) {
        if (keyword != null) {
            return repo.search(keyword);
        }
        return repo.findAll();
    }

    public void save(Movie movie) {
        repo.save(movie);
    }

    public Movie get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Movie> findTop6Movies() {
        return repo.findTop6ByOrderByIdDesc();
    }

    public Movie getMovieById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
    }
    private final UserFavouritesRepository userFavouritesRepository;

    @Autowired
    public MovieService(UserFavouritesRepository userFavouritesRepository) {
        this.userFavouritesRepository = userFavouritesRepository;
    }

    public List<Movie> getFavouritesByUser(Long userId) {
        return userFavouritesRepository.findFavouritesByUserId(userId);
    }

    public void removeFavourite(Long userId, Long movieId) {
        userFavouritesRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
    public void addFavourite(Long userId, Long movieId) {
        if (!userFavouritesRepository.existsById(new UserFavourites.UserFavouritesId(userId, movieId))) {
            UserFavourites userFavourite = new UserFavourites(userId, movieId);
            userFavouritesRepository.save(userFavourite);
        }
    }
    public List<Movie> getAllMovies() {
        return repo.findAll();
    }

}
