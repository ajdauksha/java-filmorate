package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private int id;
    private int filmId;
    private int genreId;
}
