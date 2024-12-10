package com.example.cinema.repository;

import com.example.cinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findAll();
    @Query("SELECT m FROM Movie m WHERE m.title LIKE %?1% OR m.description LIKE %?1% OR m.director LIKE %?1% OR m.genre LIKE %?1%")
    List<Movie> search(String keyword);
    List<Movie> findTop6ByOrderByIdDesc();
    @Query("SELECT m.genre, COUNT(m) FROM Movie m WHERE m.genre IN ('Комедии', 'Ужасы', 'Фантастика', 'Боевики', 'Детективы', 'Триллеры') GROUP BY m.genre")
    List<Object[]> countMoviesByGenre();
}
