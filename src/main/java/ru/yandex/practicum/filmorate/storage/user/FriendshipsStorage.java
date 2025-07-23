package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendshipsStorage {

    List<Friendship> getAllUserFriends(int userId);

    List<Friendship> getConfirmedUserFriends(int userId);

    void addUserToFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);

    void deleteAllFriends(int userId);
}
