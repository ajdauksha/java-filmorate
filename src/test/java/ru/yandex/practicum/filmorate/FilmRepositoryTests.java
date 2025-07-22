package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FilmRepository.class, FilmRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class FilmRepositoryTests {

    private final FilmRepository filmRepository;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2020, 1, 1));
        testFilm.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
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
        Optional<Film> result = filmRepository.getFilmById(999);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void getFilmById_shouldReturnFilm_whenFilmExists() {
        Film addedFilm = filmRepository.addFilm(testFilm);
        Optional<Film> result = filmRepository.getFilmById(addedFilm.getId());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .usingRecursiveComparison()
                                .ignoringFields("genres", "likes", "mpa")
                                .isEqualTo(addedFilm)
                );
    }

}