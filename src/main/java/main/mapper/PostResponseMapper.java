package main.mapper;

import main.model.*;
import main.response.*;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public class PostResponseMapper {

    public PostResponse postToPostDTO(Post post) {
        PostResponse postDTO = new PostResponse();
        postDTO.setId(post.getId());
        postDTO.setTimestamp(post.getTime().getTime());
        postDTO.setUser(getUserDTO(post));
        postDTO.setTitle(post.getTitle());
        postDTO.setAnnounce(getAnnounceDTO(post));
        postDTO.setLikeCount(getLikes(post));
        postDTO.setDislikeCount(getDislikes(post));
        postDTO.setCommentCount(post.getPostCommentList().size());
        postDTO.setViewCount(post.getViewCount());
        return postDTO;
    }

    public SpecificPostResponse postToSpecificPost(Post post) {
        SpecificPostResponse response = new SpecificPostResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().getTime());
        response.setActive(post.isActive());
        response.setUser(getUserDTO(post));
        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setLikeCount(getLikes(post));
        response.setDislikeCount(getDislikes(post));
        response.setViewCount(post.getViewCount());
        List<PostComment> postComments = post.getPostCommentList();
        if (postComments.size() != 0)
            response.setComments(
                    getCommentsResponse(postComments));
        else
            response.setComments(new ArrayList<>());
        Set<Tag> tagSet = post.getTagSet();
        if (tagSet.size() != 0)
            response.setTags(getStringTags(tagSet));
        else
            response.setTags(new HashSet<>());
        return response;
    }

    private String getAnnounceDTO(Post post) {
        String answer = post.getText();
        int sizeOfAnnounce = 200;
        if (answer.length() < sizeOfAnnounce)
            return answer;
        else {
            answer = answer.substring(0, sizeOfAnnounce);
            return answer.trim() + "...";
        }
    }

    private UserBasicResponse getUserDTO(Post post) {
        UserBasicResponse userDTO = new UserBasicResponse();
        userDTO.setId(post.getUser().getId());
        userDTO.setName(post.getUser().getName());
        return userDTO;
    }

    private int getLikes(Post post) {
        int likesCount = 0;
        for (PostVote postVote : post.getPostVoteList())
            if (postVote.getValue() == 1)
                likesCount++;
        return likesCount;
    }

    private int getDislikes(Post post) {
        int disLikesCount = 0;
        for (PostVote postVote : post.getPostVoteList())
            if (postVote.getValue() == -1)
                disLikesCount++;
        return disLikesCount;
    }

    private List<CommentResponse> getCommentsResponse
            (List<PostComment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (PostComment comment : comments) {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setTimestamp(comment.getTime().getTime());
            response.setText(comment.getText());
            response.setUser(getUserWithPhoto(comment.getUser()));
        }
        return commentResponses;
    }

    private UserWithPhotoResponse getUserWithPhoto(User user) {
        UserWithPhotoResponse response = new UserWithPhotoResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setPhoto(user.getPhoto());
        return response;
    }

    private Set<String> getStringTags(Set<Tag> tags) {
        Set<String> stringSet = new HashSet<>();
        for (Tag tag : tags)
            stringSet.add(tag.getName());
        return stringSet;
    }
}
