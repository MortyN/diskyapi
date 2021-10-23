package com.disky.api.endpoint;

import com.disky.api.Exceptions.PostControllerException;
import com.disky.api.Exceptions.UserLinkException;
import com.disky.api.controller.PostController;
import com.disky.api.controller.UserLinkController;
import com.disky.api.filter.PostFilter;
import com.disky.api.filter.UserLinkFilter;
import com.disky.api.model.Post;
import com.disky.api.model.UserLink;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/post")
@RestController
@CrossOrigin
public class PostEndpoint {

    @PostMapping(path="/create")
    public Post create(@RequestBody(required = true) Post post) throws PostControllerException {
        PostController.create(post);
        return post;
    }

    @DeleteMapping()
    public static void delete(@RequestParam(required = true)  Long postId) throws PostControllerException {
        PostController.delete(new Post(postId));
    }

    @PostMapping("/get")
    public static List<Post> get(@RequestBody(required = true) PostFilter postFilter) throws PostControllerException {
        return PostController.getPost(postFilter);
    }
}
