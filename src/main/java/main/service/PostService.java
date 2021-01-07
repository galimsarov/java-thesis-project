package main.service;

import main.request.AdditionalRequest;
import main.request.BasicRequest;
import main.response.BasicResponse;

public interface PostService {
    BasicResponse getListOfPostResponse(int offset, int limit, String mode);
    BasicResponse searchForPostResponse(int offset, int limit, String query);
    BasicResponse getPostsByDate(int offset, int limit, String date);
    BasicResponse getPostsByTag(int offset, int limit, String tag);
    BasicResponse getPostsForModeration(int offset, int limit, String status);
    BasicResponse getMyPosts(int offset, int limit, String status);
    BasicResponse getPost(int id);
    BasicResponse addPost(BasicRequest request);
    BasicResponse editPost(int id, BasicRequest request);
    BasicResponse like(AdditionalRequest request);
    BasicResponse dislike(AdditionalRequest request);
}