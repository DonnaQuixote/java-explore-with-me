package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentController {
    private final CommentService service;

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsByEvent(@Positive @PathVariable Long eventId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET comments by event");
        return service.getCommentsByEvent(eventId, from, size);
    }

    @GetMapping("/users/{userId}")
    public List<CommentDto> getCommentsByUser(@Positive @PathVariable Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET comments by user");
        return service.getCommentsByUser(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@Positive @PathVariable Long commentId) {
        log.debug("GET comment");
        return service.getComment(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Positive @PathVariable Long commentId) {
        log.debug("DELETE comment");
        service.deleteComment(commentId);
    }
}