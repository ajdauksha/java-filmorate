package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    private static final String GET_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET id = ?, email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_TO_FRIENDS_QUERY = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";
    private static final String GET_FRIENDS_QUERY = "SELECT * " +
                                                    "FROM users " +
                                                    "WHERE id IN (SELECT friend_id from friendships where friendships.user_id = ?)";
    private static final String DELETE_FROM_FRIENDS_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";

    public List<User> getAllUsers() {
        return findMany(GET_ALL_QUERY);
    }

    public User addUser(User user) {
        int id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public Optional<User> getUserById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void addUserToFriends(int userId, int friendId) {
        insert(ADD_TO_FRIENDS_QUERY, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return findMany(GET_FRIENDS_QUERY, userId);
    }

}
