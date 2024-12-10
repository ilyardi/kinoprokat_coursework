package com.example.cinema.repository;

import com.example.cinema.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r ORDER BY r.date DESC")
    List<Review> findTopNByOrderByDateDesc(@Param("limit") int limit);
    List<Review> findByMovieId(Long movieId);

}


