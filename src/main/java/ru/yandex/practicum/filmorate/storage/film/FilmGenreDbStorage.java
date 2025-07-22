package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;

import java.util.List;

@Component
@Slf4j
@Primary
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final FilmGenreRepository filmGenreRepository;

    public FilmGenreDbStorage(FilmGenreRepository filmGenreRepository) {
        this.filmGenreRepository = filmGenreRepository;
    }


    @Override
    public List<FilmGenre> getFilmGenres(int filmId) {
        return filmGenreRepository.getFilmGenres(filmId);
    }

    @Override
    public List<FilmGenre> getFilmsByGenre(int genreId) {
        return filmGenreRepository.getFilmsByGenre(genreId);
    }

    @Override
    public void addGenreToFilm(int filmId, int genreId) {
        filmGenreRepository.addGenreToFilm(filmId, genreId);

    }

    @Override
    public void deleteGenreFromFilm(int filmId, int genreId) {
        filmGenreRepository.deleteGenreFromFilm(filmId, genreId);
    }

    @Override
    public void deleteAllFilmGenres(int filmId) {
        filmGenreRepository.deleteAllFilmGenres(filmId);
    }

}
