package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;


@Slf4j
@Component
@Primary
public class MpaDbStorage implements MpaStorage {

    private final MpaRepository mpaRepository;

    public MpaDbStorage(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return mpaRepository.getAllMpa();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaRepository.getMpaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mpa c id %d не найден".formatted(id)));
    }

}
