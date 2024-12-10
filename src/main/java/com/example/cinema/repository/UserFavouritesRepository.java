package com.example.cinema.repository;

import com.example.cinema.entity.Movie;
import com.example.cinema.entity.UserFavourites;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavouritesRepository extends JpaRepository<UserFavourites, UserFavourites.UserFavouritesId> {

    @Query("SELECT m FROM Movie m JOIN UserFavourites uf ON m.id = uf.movieId WHERE uf.userId = :userId")
    List<Movie> findFavouritesByUserId(@Param("userId") Long userId);
    boolean existsById(UserFavourites.UserFavouritesId id);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFavourites uf WHERE uf.userId = :userId AND uf.movieId = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

}


