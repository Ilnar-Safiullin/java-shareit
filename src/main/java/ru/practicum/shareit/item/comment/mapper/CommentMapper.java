package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment mapToComment(RequestCommentDto requestCommentDto) {
        Comment comment = new Comment();
        comment.setText(requestCommentDto.getText());
        comment.setCreated(LocalDateTime.now()); // стоит ли так тут оставлять?
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}