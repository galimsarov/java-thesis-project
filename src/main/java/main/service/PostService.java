package main.service;

import main.request.PostRequest;
import main.request.PostVoteRequest;
import main.response.AbstractResponse;
import main.response.ListOfPostsResponse;
import main.response.SpecificPostResponse;

public interface PostService {
    ListOfPostsResponse getListOfPostResponse(int offset, int limit, String mode);
    ListOfPostsResponse searchForPostResponse(int offset, int limit, String query);
    ListOfPostsResponse getPostsByDate(int offset, int limit, String date);
    ListOfPostsResponse getPostsByTag(int offset, int limit, String tag);
    ListOfPostsResponse getPostsForModeration(int offset, int limit, String status);
    ListOfPostsResponse getMyPosts(int offset, int limit, String status);
    SpecificPostResponse getPost(int id);
    AbstractResponse addPost(PostRequest postRequest);
    AbstractResponse editPost(int id, PostRequest postRequest);
    AbstractResponse like(PostVoteRequest request);
    AbstractResponse dislike(PostVoteRequest request);
}