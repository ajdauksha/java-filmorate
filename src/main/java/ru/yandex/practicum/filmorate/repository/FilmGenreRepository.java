package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.repository.mappers.FilmGenresRowMapper;

import java.util.List;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> {

    public FilmGenreRepository(JdbcTemplate jdbc, FilmGenresRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_FILM_GENRES_QUERY = "SELECT * FROM film_genres WHERE film_id = ? ORDER BY genre_id ASC";
    private static final String GET_FILMS_BY_GENRE_QUERY = "SELECT * FROM film_genres WHERE genre_id = ? ORDER BY film_id ASC";
    private static final String ADD_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
    private static final String DELETE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public List<FilmGenre> getFilmGenres(int filmId) {
        return findMany(GET_FILM_GENRES_QUERY, filmId);
    }

    public List<FilmGenre> getFilmsByGenre(int genreId) {
        return findMany(GET_FILMS_BY_GENRE_QUERY, genreId);
    }

    public void addGenreToFilm(int filmId, int genreId) {
        insert(ADD_GENRE_QUERY, filmId, genreId);
    }

    public void deleteGenreFromFilm(int filmId, int genreId) {
        delete(DELETE_GENRE_QUERY, filmId, genreId);
    }

    public void deleteAllFilmGenres(int filmId) {
        delete(DELETE_ALL_FILM_GENRES_QUERY, filmId);
    }

}
