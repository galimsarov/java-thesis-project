package main.service;

import main.response.PostDTO;

import java.util.List;

public interface ListOfPostsService {
    List<PostDTO> getListOfPostsDTO(int offset, int limit, String mode);
    List<PostDTO> searchForPostsDTO(int offset, int limit, String query);
    List<PostDTO> getPostsByDate(int offset, int limit, String date);
    List<PostDTO> getPostsByTag(int offset, int limit, String tag);
}