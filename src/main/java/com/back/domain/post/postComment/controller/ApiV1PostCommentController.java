package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    @GetMapping
    public List<PostCommentDto> getItems(@PathVariable int postId) {
        Post post = postService.findById(postId).get();

        return post.getComments().stream()
                .map(PostCommentDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PostCommentDto getItem(@PathVariable int postId, @PathVariable int id) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();
        return new PostCommentDto(postComment);
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int postId, @PathVariable int id) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        postService.deleteComment(post, postComment);

        return "%d번 댓글이 삭제되었습니다.".formatted(id);
    }
}