package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.repository.mappers.LikesRowMapper;

import java.util.List;

@Repository
public class LikeRepository extends BaseRepository<Like> {

    public LikeRepository(JdbcTemplate jdbc, LikesRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_FILM_LIKES_QUERY = "SELECT * FROM likes WHERE film_id = ? ORDER BY user_id ASC";
    private static final String GET_USER_LIKED_FILMS_QUERY = "SELECT * FROM likes WHERE user_id = ? ORDER BY film_id ASC";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    public List<Like> getFilmLikes(int filmId) {
        return findMany(GET_FILM_LIKES_QUERY, filmId);
    }

    public List<Like> getUserLikedFilms(int userId) {
        return findMany(GET_USER_LIKED_FILMS_QUERY, userId);
    }

    public void addLike(int filmId, int userId) {
        insert(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        delete(DELETE_LIKE_QUERY, filmId, userId);
    }

}
