package ru.practicum.shareit.item.comment.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.comment.model.Comment;
import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemIds);

    //Сделал тут JOIN FETCH иначе в интеграционном тесте метода getItemsByOwner получал LazyException
    @Query("SELECT c FROM Comment c JOIN FETCH c.item i JOIN FETCH c.author a WHERE i.id IN :itemIds")
    List<Comment> findAllByItemIdIn(@Param("itemIds") List<Long> itemIds);
}