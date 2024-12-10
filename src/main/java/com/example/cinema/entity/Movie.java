package com.example.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор фильма

    @Getter
    private String title; // Название фильма

    @Getter
    private String description; // Описание фильма

    @Getter
    private String director; // Режиссер фильма

    @Getter
    private String genre; // Жанр фильма

    @Getter
    private Boolean isFavorite = false; // Статус: избранный (true) или нет (false)

    @Getter
    private String trailerUrl; // Ссылка на трейлер фильма

    @Getter
    private String movieUrl; // Ссылка на сам фильм

    public Movie() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id
                + ", title=" + title
                + ", description=" + description
                + ", director=" + director
                + ", genre=" + genre
                + ", isFavorite=" + isFavorite
                + ", trailerUrl=" + trailerUrl
                + ", movieUrl=" + movieUrl + "]";
    }
    // Геттеры и сеттеры
    public Boolean getIsFavourite() {
        return isFavorite;
    }

    public void setIsFavourite(Boolean isFavourite) {
        this.isFavorite = isFavourite;
    }
}
