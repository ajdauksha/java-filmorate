package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static int id = 1;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilmId(film.getId());
        films.put(film.getId(), film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    public Film getFilmById(int id) {
        validateFilmId(id);
        return films.get(id);
    }

    private void validateFilmId(int id) {
        if (!films.containsKey(id)) {
            throw new ResourceNotFoundException("Фильм c id %d не найден".formatted(id));
        }
    }

}
