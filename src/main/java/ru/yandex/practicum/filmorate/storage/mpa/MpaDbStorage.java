package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.List;


@Slf4j
@Primary
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM mpa_ratings ORDER BY id ASC";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    @Override
    public List<Mpa> getAllMpa() {
        return findMany(GET_ALL_MPA_QUERY);
    }

    @Override
    public Mpa getMpaById(int id) {
        return findOne(GET_MPA_BY_ID_QUERY, id)
                .orElseThrow(() -> new ResourceNotFoundException("Mpa c id %d не найден".formatted(id)));
    }

}
