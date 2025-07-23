package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    public static final String GET_ALL_QUERY = "SELECT * FROM films";
    public static final String ADD_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    public static final String GET_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    public static final String ADD_ALL_GENRES_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
    public static final String SELECT_MPA_QUERY = "SELECT f.id as film_id, m.* FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id IN (%s)";
    public static final String SELECT_GENRES_QUERY = "SELECT * FROM film_genres WHERE film_id IN (%s)";
    public static final String SELECT_LIKES_QUERY = "SELECT film_id, user_id FROM likes WHERE film_id IN (%s)";
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper,
                         GenreStorage genreStorage, MpaStorage mpaStorage,
                         FilmGenreStorage filmGenreStorage, LikeStorage likeStorage) {
        super(jdbc, filmRowMapper);
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = findMany(GET_ALL_QUERY);

        if (films.isEmpty()) {
            return films;
        }

        List<Integer> filmIds = films.stream().map(Film::getId).toList();

        Map<Integer, Mpa> filmMpas = getMpasForFilms(filmIds);

        Map<Integer, Set<Genre>> filmGenres = getGenresForFilms(filmIds);

        Map<Integer, Set<Integer>> filmLikes = getLikesForFilms(filmIds);

        films.forEach(film -> {
            film.setMpa(filmMpas.get(film.getId()));
            film.setGenres(filmGenres.getOrDefault(film.getId(), Collections.emptySet()));
            film.setLikedByUsers(filmLikes.getOrDefault(film.getId(), Collections.emptySet()));
        });

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));

        int filmId = insert(ADD_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> uniqueGenres = new LinkedHashSet<>(film.getGenres());
            film.setGenres(uniqueGenres);

            List<Object[]> batchArgs = uniqueGenres.stream()
                    .map(genre -> new Object[]{filmId, genre.getId()})
                    .collect(Collectors.toList());

            try {
                batchUpdate(ADD_ALL_GENRES_QUERY, batchArgs);
            } catch (DataIntegrityViolationException e) {
                throw new ResourceNotFoundException("Ошибка при добавлении жанров фильма: один или несколько жанров не существуют");
            }

        }

        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        filmGenreStorage.deleteAllFilmGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> uniqueGenres = new LinkedHashSet<>(film.getGenres());
            film.setGenres(uniqueGenres);

            List<Object[]> batchArgs = uniqueGenres.stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .collect(Collectors.toList());

            batchUpdate(ADD_ALL_GENRES_QUERY, batchArgs);
        }

        log.info("Обновлен фильм {}", film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = findOne(GET_FILM_BY_ID_QUERY, id)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм c id %d не найден".formatted(id)));

        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        }

        List<FilmGenre> filmGenres = filmGenreStorage.getFilmGenres(film.getId());
        if (!filmGenres.isEmpty()) {
            Set<Genre> genres = new LinkedHashSet<>();
            filmGenres.forEach(fg -> genres.add(genreStorage.getGenreById(fg.getGenreId())));
            film.setGenres(genres);
        }

        List<Like> likes = likeStorage.getFilmLikes(film.getId());
        if (!likes.isEmpty()) {
            film.setLikedByUsers(likes.stream().map(Like::getUserId).collect(Collectors.toSet()));
        }

        return film;
    }

    private Map<Integer, Mpa> getMpasForFilms(List<Integer> filmIds) {
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        return jdbc.query(
                String.format(SELECT_MPA_QUERY, inSql),
                rs -> {
                    Map<Integer, Mpa> result = new HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getInt("film_id"), new Mpa(
                                rs.getInt("id"),
                                rs.getString("name")
                        ));
                    }
                    return result;
                },
                filmIds.toArray()
        );
    }

    private Map<Integer, Set<Genre>> getGenresForFilms(List<Integer> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        List<FilmGenre> filmGenres = jdbc.query(
                String.format(SELECT_GENRES_QUERY, inSql),
                (rs, rowNum) -> new FilmGenre(
                        rs.getInt("id"),
                        rs.getInt("film_id"),
                        rs.getInt("genre_id")
                ),
                filmIds.toArray()
        );

        if (filmGenres.isEmpty()) return Collections.emptyMap();

        Set<Integer> genreIds = filmGenres.stream()
                .map(FilmGenre::getGenreId)
                .collect(Collectors.toSet());

        Map<Integer, Genre> genres = genreStorage.getGenresByIds(genreIds);

        Map<Integer, Set<Genre>> result = new HashMap<>();
        filmGenres.forEach(fg -> {
            result.computeIfAbsent(fg.getFilmId(), k -> new LinkedHashSet<>())
                    .add(genres.get(fg.getGenreId()));
        });

        return result;
    }

    private Map<Integer, Set<Integer>> getLikesForFilms(List<Integer> filmIds) {
        if (filmIds.isEmpty()) return Collections.emptyMap();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        return jdbc.query(
                String.format(SELECT_LIKES_QUERY, inSql),
                rs -> {
                    Map<Integer, Set<Integer>> result = new HashMap<>();
                    while (rs.next()) {
                        result.computeIfAbsent(rs.getInt("film_id"), k -> new HashSet<>())
                                .add(rs.getInt("user_id"));
                    }
                    return result;
                },
                filmIds.toArray()
        );
    }
}