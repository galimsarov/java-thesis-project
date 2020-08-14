package main.service.impl;

import main.mapper.PostDTOMapper;
import main.model.Post;
import main.repository.PostRepository;
import main.response.PostDTO;
import main.service.ListOfPostsService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListOfPostsServiceImpl implements ListOfPostsService {
    @Autowired
    private final PostRepository postRepository;

    public ListOfPostsServiceImpl
            (PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDTO> getListOfPostsDTO(int offset, int limit, String mode) {
        List<PostDTO> postDTOList = new ArrayList<>();
        PostDTOMapper mapper = Mappers.getMapper(PostDTOMapper.class);
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Post> postPage = null;
        switch (mode) {
            case "best":
                postPage = postRepository.findPostsSortedByLikesCount(pageable);
                break;
            case "popular":
                postPage = postRepository.findPostsSortedByCommentsCount(pageable);
                break;
            case "early":
                postPage = postRepository.findEarlyPosts(pageable);
                break;
            default:
                postPage = postRepository.findRecentPosts(pageable);
        }
        for (Post post : postPage) {
            PostDTO postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    @Override
    public List<PostDTO> searchForPostsDTO(int offset, int limit, String query) {
        List<PostDTO> postDTOList = new ArrayList<>();
        PostDTOMapper mapper = Mappers.getMapper(PostDTOMapper.class);
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                searchForPostsByQuery(query, pageable);
        for (Post post : postList) {
            PostDTO postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    @Override
    public List<PostDTO> getPostsByDate(int offset, int limit, String date) {
        List<PostDTO> postDTOList = new ArrayList<>();
        PostDTOMapper mapper = Mappers.getMapper(PostDTOMapper.class);
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                getPostsByDate(dateBefore(date), dateAfter(date), pageable);
        for (Post post : postList) {
            PostDTO postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    @Override
    public List<PostDTO> getPostsByTag(int offset, int limit, String tag) {
        List<PostDTO> postDTOList = new ArrayList<>();
        PostDTOMapper mapper = Mappers.getMapper(PostDTOMapper.class);
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                getPostsByTag(tag, pageable);
        for (Post post : postList) {
            PostDTO postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        return postDTOList;
    }

    private String dateBefore(String date) {
        return date + " 00:00:00.000000";
    }

    private String dateAfter(String date) {
        return date.substring(0, 8) +
                (Integer.parseInt(date.substring(8, 10)) + 1) +
                " 00:00:00.000000";
    }
}