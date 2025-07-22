package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    public GenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY id ASC";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    public List<Genre> getAllGenres() {
        return findMany(GET_ALL_GENRES_QUERY);
    }

    public Optional<Genre> getGenreById(int id) {
        return findOne(GET_GENRE_BY_ID_QUERY, id);
    }

}
