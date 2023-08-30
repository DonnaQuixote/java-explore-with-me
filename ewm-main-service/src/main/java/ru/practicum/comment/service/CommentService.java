package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentRequest;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.EditingProhibitedException;
import ru.practicum.exception.WrongEventStatusException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        if (!EventState.PUBLISHED.equals(event.getState())) throw new WrongEventStatusException("Event not published");

        return CommentMapper.toCommentDto(repository.save(CommentMapper.toComment(newCommentDto, user, event)));
    }

    public CommentDto patchComment(Long userId, Long commentId, UpdateCommentRequest request) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id=%d was not found", userId)));
        Comment comment = repository.findById(commentId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Comment with id=%d was not found", commentId)));

        if (!userId.equals(comment.getAuthor().getId())) throw new EditingProhibitedException(
                "Only author can update the comment");

        comment.setText(request.getText());
        return CommentMapper.toCommentDto(repository.save(comment));
    }

    public List<CommentDto> getCommentsByUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id=%d was not found", userId)));

        return repository.findByAuthor_Id(userId, PageRequest.of(from / size, size)).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public List<CommentDto> getCommentsByEvent(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Event with id=%d was not found", eventId)));

        return repository.findByEvent_Id(eventId, PageRequest.of(from / size, size)).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public CommentDto getComment(Long commentId) {
        return CommentMapper.toCommentDto(repository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(
                String.format("Comment with id=%d was not found", commentId))));
    }

    public void deleteComment(Long commentId) {
        try {
            repository.deleteById(commentId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId));
        }
    }
}