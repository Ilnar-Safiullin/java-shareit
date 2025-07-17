package ru.practicum.shareit.item.comment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.comment.model.Comment;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemIds);

    //Сделал тут JOIN FETCH так как мы в ItemServiceImpl часто будем отправлять comment.getItem().getId() - что будет заставлять постоянно подгружать данные
    @Query("SELECT c FROM Comment c JOIN FETCH c.item WHERE c.item.id IN :itemIds")
    List<Comment> findAllByItemIdIn(@Param("itemIds") List<Long> itemIds);
}