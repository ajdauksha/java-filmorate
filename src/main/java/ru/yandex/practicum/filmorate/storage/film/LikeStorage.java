package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {

    List<Like> getFilmLikes(int filmId);

    List<Like> getUserLikedFilms(int userId);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

}
