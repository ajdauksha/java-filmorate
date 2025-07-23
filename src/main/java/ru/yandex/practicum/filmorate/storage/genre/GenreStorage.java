package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage {

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    Map<Integer, Genre> getGenresByIds(Set<Integer> genreIds);

}
