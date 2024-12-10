package com.example.cinema.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_favourites")
@IdClass(UserFavourites.UserFavouritesId.class)
public class UserFavourites {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "movie_id")
    private Long movieId;

    public UserFavourites() {
    }

    public UserFavourites(Long userId, Long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFavourites that = (UserFavourites) o;
        return Objects.equals(userId, that.userId) && Objects.equals(movieId, that.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, movieId);
    }

    // Вспомогательный класс для составного ключа
    public static class UserFavouritesId implements Serializable {
        private Long userId;
        private Long movieId;

        public UserFavouritesId() {
        }

        public UserFavouritesId(Long userId, Long movieId) {
            this.userId = userId;
            this.movieId = movieId;
        }

        // геттеры, сеттеры, equals и hashCode аналогично
    }
}
