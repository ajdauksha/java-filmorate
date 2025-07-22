package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;


public interface FilmGenreStorage {

    List<FilmGenre> getFilmGenres(int filmId);

    List<FilmGenre> getFilmsByGenre(int genreId);

    void addGenreToFilm(int filmId, int genreId);

    void deleteGenreFromFilm(int filmId, int genreId);

    void deleteAllFilmGenres(int filmId);
}
