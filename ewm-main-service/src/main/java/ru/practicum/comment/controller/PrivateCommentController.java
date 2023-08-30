package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.debug("POST comment, user id: {}, event id: {}", userId, eventId);
        return service.postComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patchComment(@PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long commentId,
                                   @Valid @RequestBody UpdateCommentRequest request) {
        log.debug("PATCH comment, user id: {}, comment id: {}", userId, commentId);
        return service.patchComment(userId, commentId, request);
    }
}