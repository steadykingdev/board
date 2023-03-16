package com.study.board.controller;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.dto.CommentRequest;
import com.study.board.domain.dto.CommentResponse;
import com.study.board.domain.dto.CommonResponseFormat;
import com.study.board.service.CommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{post-id}/comments")
    public ResponseEntity<CommonResponseFormat> createComment(@PathVariable("post-id") Long postId,
                                                              @RequestAttribute("user") JwtPayload jwtPayload,
                                                              @RequestBody CommentRequest commentRequest) {
        commentService.createComment(postId, jwtPayload.getId(), commentRequest);

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{post-id}/comments/list")
    public ResponseEntity<CommonResponseFormat<List<CommentResponse>>> getCommentList(@PathVariable("post-id") Long postId,
                                                                                      Pageable pageable) {
        List<CommentResponse> commentList =commentService.getCommentList(postId, pageable);

        return new ResponseEntity<>(CommonResponseFormat.createSuccess(commentList), HttpStatus.OK);
    }

    @PutMapping("/comments/{comment-id}")
    public ResponseEntity<CommonResponseFormat> updateComment(@PathVariable("comment-id") Long commentId,
                                                              @RequestAttribute("user") JwtPayload jwtPayload,
                                                              @RequestBody CommentRequest commentRequest
    ) {
        commentService.updateComment(commentId, jwtPayload.getId(), commentRequest);

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{comment-id}")
    public ResponseEntity<CommonResponseFormat> deleteComment(@PathVariable("comment-id") Long commentId,
                                                              @RequestAttribute("user") JwtPayload jwtPayload) {
        commentService.deleteComment(commentId, jwtPayload.getId());

        return new ResponseEntity<>(CommonResponseFormat.createSuccessWithNoContent(), HttpStatus.OK);
    }
}
