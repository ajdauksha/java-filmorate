package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    List<User> getFriends(int userId);

    User deleteFromFriends(int userId, int friendId);

}
