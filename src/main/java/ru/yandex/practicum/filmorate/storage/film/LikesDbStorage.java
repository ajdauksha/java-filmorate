package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.List;

@Component
@Slf4j
@Primary
public class LikesDbStorage implements LikeStorage {

    private final LikeRepository likeRepository;

    public LikesDbStorage(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public List<Like> getFilmLikes(int filmId) {
        return likeRepository.getFilmLikes(filmId);
    }

    @Override
    public List<Like> getUserLikedFilms(int userId) {
        return likeRepository.getUserLikedFilms(userId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        likeRepository.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        likeRepository.deleteLike(filmId, userId);
    }
}
