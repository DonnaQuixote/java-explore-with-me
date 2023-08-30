package ru.practicum.comment.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthor_Id(Long author, Pageable pageable);

    List<Comment> findByEvent_Id(Long event, Pageable pageable);
}
