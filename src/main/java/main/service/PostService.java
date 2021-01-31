package main.service;

import main.request.others.PostRequest;
import main.request.postids.PostIdRequest;
import main.response.others.ThePosts;
import main.response.results.ResultResponse;

public interface PostService {
    ThePosts getListOfPostResponse(int offset, int limit, String mode);
    ThePosts searchForPostResponse(int offset, int limit, String query);
    ThePosts getPostsByDate(int offset, int limit, String date);
    ThePosts getPostsByTag(int offset, int limit, String tag);
    ThePosts getPostsForModeration(int offset, int limit, String status);
    ThePosts getMyPosts(int offset, int limit, String status);
    Object getPost(int id);
    Object addPost(PostRequest request);
    Object editPost(int id, PostRequest request);
    ResultResponse like(PostIdRequest request);
    ResultResponse dislike(PostIdRequest request);
}