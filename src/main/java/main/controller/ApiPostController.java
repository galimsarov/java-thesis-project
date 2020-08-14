package main.controller;

import main.repository.PostRepository;
import main.response.ListOfPostsDTO;
import main.response.PostDTO;
import main.service.impl.ListOfPostsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ListOfPostsDTO listOfPosts(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {
        ListOfPostsServiceImpl service = new ListOfPostsServiceImpl(postRepository);
        List<PostDTO> posts = service.getListOfPostsDTO(offset, limit, mode);
        ListOfPostsDTO answer = new ListOfPostsDTO();
        answer.setCount(posts.size());
        answer.setPosts(posts);
        return answer;
    }

    @GetMapping("/search")
    public ListOfPostsDTO searchForPosts(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String query) {
        ListOfPostsServiceImpl service = new ListOfPostsServiceImpl(postRepository);
        List<PostDTO> posts = service.searchForPostsDTO(offset, limit, query);
        ListOfPostsDTO answer = new ListOfPostsDTO();
        answer.setCount(posts.size());
        answer.setPosts(posts);
        return answer;
    }

    @GetMapping("/byDate")
    public ListOfPostsDTO postsByDate(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String date) {
        ListOfPostsServiceImpl service = new ListOfPostsServiceImpl(postRepository);
        List<PostDTO> posts = service.getPostsByDate(offset, limit, date);
        ListOfPostsDTO answer = new ListOfPostsDTO();
        answer.setCount(posts.size());
        answer.setPosts(posts);
        return answer;
    }

    @GetMapping("/byTag")
    public ListOfPostsDTO postsByTag(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String tag) {
        ListOfPostsServiceImpl service = new ListOfPostsServiceImpl(postRepository);
        List<PostDTO> posts = service.getPostsByTag(offset, limit, tag);
        ListOfPostsDTO answer = new ListOfPostsDTO();
        answer.setCount(posts.size());
        answer.setPosts(posts);
        return answer;
    }
}
