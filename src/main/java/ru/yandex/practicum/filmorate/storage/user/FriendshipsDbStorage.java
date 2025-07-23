package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.FriendshipsRowMapper;

import java.util.List;

@Slf4j
@Primary
@Repository
public class FriendshipsDbStorage extends BaseRepository<Friendship> implements FriendshipsStorage {

    public FriendshipsDbStorage(JdbcTemplate jdbc, FriendshipsRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_USER_FRIENDS_QUERY = "SELECT * FROM friendships WHERE user_id = ?";
    private static final String GET_CONFIRMED_USER_FRIENDS_QUERY = "SELECT f1.friend_id AS approved_friend_id " +
            "FROM friendships f1 " +
            "INNER JOIN friendships f2 ON f1.user_id = f2.friend_id AND f1.friend_id = f2.user_id " +
            "WHERE f1.user_id = ?;";
    private static final String ADD_TO_FRIENDS_QUERY = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FROM_FRIENDS_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_ALL_FRIENDS_QUERY = "DELETE FROM friendships WHERE user_id = ?";

    @Override
    public List<Friendship> getAllUserFriends(int userId) {
        return findMany(GET_ALL_USER_FRIENDS_QUERY, userId);
    }

    @Override
    public List<Friendship> getConfirmedUserFriends(int userId) {
        return findMany(GET_CONFIRMED_USER_FRIENDS_QUERY, userId);
    }

    @Override
    public void addUserToFriends(int userId, int friendId) {
        insert(ADD_TO_FRIENDS_QUERY, userId, friendId);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        delete(DELETE_FROM_FRIENDS_QUERY, userId, friendId);
    }

    @Override
    public void deleteAllFriends(int userId) {
        delete(DELETE_ALL_FRIENDS_QUERY, userId);
    }

}
