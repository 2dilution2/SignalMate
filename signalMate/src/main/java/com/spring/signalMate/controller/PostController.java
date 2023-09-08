package com.spring.signalMate.controller;

import com.spring.signalMate.dto.PostDto;
import com.spring.signalMate.dto.ResponseDto;
import com.spring.signalMate.service.PostService;
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

    @PostMapping("/update/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostDto requestBody) {
        ResponseDto<?> result = postService.updatePost(postId, requestBody);
        return result;
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDto<List<PostDto>>> listPosts() {
        ResponseDto<List<PostDto>> response = postService.getList();
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
