package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;

    public FilmDbStorage(FilmRepository filmRepository, GenreStorage genreStorage, MpaStorage mpaStorage,
                         FilmGenreStorage filmGenreStorage, LikeStorage likeStorage) {
        this.filmRepository = filmRepository;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmRepository.getAllFilms().stream()
                .map(film -> {
                    addMpaInfoToResponse(film);
                    addGenresInfoToResponse(film);
                    addLikesInfoToResponse(film);
                    return film;
                })
                .toList();
    }

    @Override
    public Film addFilm(Film film) {
        Mpa mpa = film.getMpa();
        if (mpa != null) {
            film.setMpa(mpaStorage.getMpaById(mpa.getId()));
        }

        filmRepository.addFilm(film);

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            Set<Genre> genresWithNames = new LinkedHashSet<>();
            for (Genre genre : genres) {
                genre = genreStorage.getGenreById(genre.getId());
                genresWithNames.add(genre);
                filmGenreStorage.addGenreToFilm(film.getId(), genre.getId());
            }
            film.setGenres(genresWithNames);
        }

        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());

        Mpa mpa = film.getMpa();

        if (mpa != null) {
            film.setMpa(mpaStorage.getMpaById(mpa.getId()));
        }

        filmRepository.updateFilm(film);
        if (film.getGenres() != null) {
            filmGenreStorage.deleteAllFilmGenres(film.getId());
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.addGenreToFilm(film.getId(), genre.getId());
            }
        }
        log.info("Обновлен фильм {}", film);
        return film;
    }

    public Film getFilmById(int id) {
        Film film = filmRepository.getFilmById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фильм c id %d не найден".formatted(id)));
        addMpaInfoToResponse(film);
        addGenresInfoToResponse(film);
        addLikesInfoToResponse(film);
        return film;
    }

    private Film addGenresInfoToResponse(Film film) {
        List<FilmGenre> filmGenres = filmGenreStorage.getFilmGenres(film.getId());
        if (filmGenres.isEmpty()) {
            return film;
        }
        Set<Genre> genres = new LinkedHashSet<>();
        filmGenres.forEach(filmGenre -> genres.add(genreStorage.getGenreById(filmGenre.getGenreId())));
        film.setGenres(genres);
        return film;
    }

    private Film addMpaInfoToResponse(Film film) {
        if (film.getMpa() == null) {
            return film;
        }
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
        return film;
    }

    private Film addLikesInfoToResponse(Film film) {
        List<Like> likedByUsers = likeStorage.getFilmLikes(film.getId());

        if (!likedByUsers.isEmpty()) {
            film.setLikedByUsers(likedByUsers.stream().map(Like::getUserId).collect(Collectors.toSet()));

        }
        return film;
    }

}
