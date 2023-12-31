package com.spring.signalMate.domain.post.controller;

import com.spring.signalMate.domain.post.model.dto.PostDto;
import com.spring.signalMate.global.dto.ResponseDto;
import com.spring.signalMate.domain.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/new")
    public ResponseDto<?> post(@RequestBody PostDto requestBody) {
        ResponseDto<?> result = postService.createPost(requestBody);
        return result;
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDto<List<PostDto>>> listPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        ResponseDto<List<PostDto>> response = postService.getList(page, size);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostDto requestBody) {
        ResponseDto<?> result = postService.updatePost(postId, requestBody);
        return result;
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ResponseDto<String>> deletePost(@PathVariable Long postId) {
        ResponseDto<String> response = postService.deletePost(postId);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDto<List<PostDto>>> getSearchList(@RequestParam String title,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        ResponseDto<List<PostDto>> response = postService.getSearchList(title, page, size);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }
}
