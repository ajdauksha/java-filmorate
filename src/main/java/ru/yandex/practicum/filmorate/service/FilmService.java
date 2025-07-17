package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(int filmId, int userId) {
        userService.getUserById(userId);

        Film film = filmStorage.getFilmById(filmId);
        Set<Integer> likedByUsers = film.getLikedByUsers();

        if (likedByUsers.contains(userId)) {
            throw new ValidationException("Пользователь %d уже поставил лайк фильму %d".formatted(userId, filmId));
        }

        likedByUsers.add(userId);

        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        userService.getUserById(userId);

        Film film = filmStorage.getFilmById(filmId);
        Set<Integer> likedByUsers = film.getLikedByUsers();

        if (!likedByUsers.contains(userId)) {
            throw new ValidationException("Пользователь %d не ставил лайк фильму %d".formatted(userId, filmId));
        }

        likedByUsers.remove(userId);

        return film;
    }

    public List<Film> getMostLikedFilms(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o2.getLikedByUsers().size() - o1.getLikedByUsers().size())
                .limit(count == null ? 10 : count)
                .collect(Collectors.toList());
    }

}
