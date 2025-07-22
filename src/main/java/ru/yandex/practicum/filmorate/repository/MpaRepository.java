package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM mpa_ratings ORDER BY id ASC";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    public List<Mpa> getAllMpa() {
        return findMany(GET_ALL_MPA_QUERY);
    }

    public Optional<Mpa> getMpaById(int id) {
        return findOne(GET_MPA_BY_ID_QUERY, id);
    }

}
