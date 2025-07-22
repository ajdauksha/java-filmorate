package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FilmRepository extends BaseRepository<Film> {

    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_QUERY = "SELECT * FROM films";
    private static final String ADD_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET id = ?, name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String GET_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    public List<Film> getAllFilms() {
        return findMany(GET_ALL_QUERY);
    }

    public Film addFilm(Film film) {
        int id = insert(ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.info("Обновлен фильм {}", film);
        return film;
    }

    public Optional<Film> getFilmById(int id) {
        return findOne(GET_FILM_BY_ID_QUERY, id);
    }

}
