package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY id ASC";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    public static final String SELECT_MULTIPLE_GENRES_QUERY = "SELECT * FROM genres WHERE id IN (%s)";

    @Override
    public List<Genre> getAllGenres() {
        return findMany(GET_ALL_GENRES_QUERY);
    }

    @Override
    public Genre getGenreById(int id) {
        return findOne(GET_GENRE_BY_ID_QUERY, id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь c id %d не найден".formatted(id)));
    }

    @Override
    public Map<Integer, Genre> getGenresByIds(Set<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        String query = SELECT_MULTIPLE_GENRES_QUERY.formatted(inSql);

        return findMany(query, genreIds.toArray()).stream().collect(Collectors.toMap(Genre::getId, genre -> genre));
    }

}
