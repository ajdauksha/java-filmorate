package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Slf4j
@Component
@Primary
public class GenreDbStorage implements GenreStorage {

    private final GenreRepository genreRepository;

    public GenreDbStorage(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }


    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreRepository.getGenreById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь c id %d не найден".formatted(id)));
    }

}
