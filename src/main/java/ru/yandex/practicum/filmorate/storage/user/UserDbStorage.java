package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.BaseRepository;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String GET_FRIENDS = "SELECT friend_id FROM friendships WHERE user_id = ?";
    private static final String CHECK_FRIENDSHIP = "SELECT COUNT(*) > 0 FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_ALL_FRIENDS = "DELETE FROM friendships WHERE user_id = ?";
    private static final String GET_FRIENDS_WITH_DETAILS = """
            SELECT u.* FROM users u 
            JOIN friendships f ON u.id = f.friend_id 
            WHERE f.user_id = ?""";
    private static final String GET_FRIENDS_FOR_USERS = """
            SELECT user_id, friend_id FROM friendships 
            WHERE user_id IN (%s)""";

    private final FriendshipsDbStorage friendshipsDbStorage;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper userRowMapper, FriendshipsDbStorage friendshipsDbStorage) {
        super(jdbc, userRowMapper);
        this.friendshipsDbStorage = friendshipsDbStorage;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = findMany(GET_ALL_USERS);

        if (users.isEmpty()) {
            return users;
        }

        List<Integer> userIds = users.stream().map(User::getId).toList();
        Map<Integer, Set<Integer>> userFriends = getFriendsForUsers(userIds);

        users.forEach(user ->
                user.setFriends(userFriends.getOrDefault(user.getId(), Collections.emptySet()))
        );

        return users;
    }

    @Override
    public User addUser(User user) {
        validateUserName(user);

        int id = insert(
                INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);

        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());

        update(
                UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        updateUserFriends(user.getId(), user.getFriends());

        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = findOne(GET_USER_BY_ID, id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пользователь c id %d не найден".formatted(id))
                );

        Set<Integer> friends = friendshipsDbStorage.getAllUserFriends(id).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        user.setFriends(friends);
        return user;
    }

    public User deleteFromFriends(int userId, int friendId) {
        User user = getUserById(userId);
        getUserById(friendId);

        int rowsDeleted = jdbc.update(DELETE_FRIEND, userId, friendId);
        if (rowsDeleted == 0) {
            log.warn("Пользователи {} и {} не являются друзьями", userId, friendId);
            return user;
        }
        user.getFriends().remove(friendId);
        return user;
    }

    public List<User> getFriends(int userId) {
        getUserById(userId);
        return findMany(GET_FRIENDS_WITH_DETAILS, userId);
    }

    private void updateUserFriends(int userId, Set<Integer> friends) {
        delete(DELETE_ALL_FRIENDS, userId);

        if (friends != null && !friends.isEmpty()) {
            List<Object[]> batchArgs = friends.stream()
                    .map(friendId -> new Object[]{userId, friendId})
                    .toList();

            batchUpdate(ADD_FRIEND, batchArgs);
        }
    }

    private Map<Integer, Set<Integer>> getFriendsForUsers(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inSql = String.join(",", Collections.nCopies(userIds.size(), "?"));
        String query = String.format(GET_FRIENDS_FOR_USERS, inSql);

        return jdbc.query(query, rs -> {
            Map<Integer, Set<Integer>> result = new HashMap<>();
            while (rs.next()) {
                result.computeIfAbsent(rs.getInt("user_id"), k -> new HashSet<>())
                        .add(rs.getInt("friend_id"));
            }
            return result;
        }, userIds.toArray());
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано. Заполняется логином.");
            user.setName(user.getLogin());
        }
    }

}