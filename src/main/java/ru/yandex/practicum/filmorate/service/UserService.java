package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User addUserToFriends(int userId, int friendId) {
        User user = getUserById(userId);
        Set<Integer> friends = user.getFriends();

        if (friends.contains(friendId)) {
            throw new ValidationException("Пользователи %d и %d уже являются друзьями".formatted(userId, friendId));
        }

        getUserById(userId).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(userId);
        return getUserById(userId);
    }

    public User deleteFromFriends(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Set<Integer> userFriends = user.getFriends();

        if (userFriends == null || userFriends.isEmpty()) {
            log.warn("У пользователя {} список друзей пуст", userId);
            return user;
        }

        if (!userFriends.contains(friendId)) {
            log.warn("Пользователи {} и {} не являются друзьями", userId, friendId);
            return user;
        }

        userFriends.remove(friendId);
        friend.getFriends().remove(userId);
        return getUserById(userId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        Set<Integer> friends = user.getFriends();

        if (friends == null || friends.isEmpty()) {
            return List.of();
        }

        return friends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        Set<Integer> friends = user.getFriends();
        Set<Integer> otherFriends = otherUser.getFriends();
        return friends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

}
