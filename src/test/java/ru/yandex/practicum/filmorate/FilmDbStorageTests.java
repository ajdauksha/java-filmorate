package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        FilmGenreDbStorage.class, FilmGenresRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class,
        LikesDbStorage.class, LikesRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class FilmDbStorageTests {

    private final FilmDbStorage filmRepository;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        testFilm.setDuration(120);
        Mpa mpa = new Mpa(1, "G");
        testFilm.setMpa(mpa);
    }

    @Test
    void getAllFilms_shouldReturnEmptyList_whenNoFilms() {
        List<Film> films = filmRepository.getAllFilms();

        assertThat(films)
                .isEmpty();
    }

    @Test
    void addFilm_shouldAddFilmAndReturnWithId() {
        Film addedFilm = filmRepository.addFilm(testFilm);

        assertThat(addedFilm)
                .isNotNull();

        assertThat(addedFilm.getId())
                .isPositive();

        assertThat(addedFilm)
                .usingRecursiveComparison()
                .ignoringFields("id", "genres", "likes")
                .isEqualTo(testFilm);
    }

    @Test
    void getFilmById_shouldReturnEmptyOptional_whenFilmNotFound() {
        assertThatThrownBy(() -> filmRepository.getFilmById(999)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getFilmById_shouldReturnFilm_whenFilmExists() {
        Film addedFilm = filmRepository.addFilm(testFilm);
        Film result = filmRepository.getFilmById(addedFilm.getId());

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("genres", "likes", "mpa")
                .isEqualTo(addedFilm);
    }

}