package main.mapper;

import main.model.Post;
import main.model.PostVote;
import main.response.PostDTO;
import main.response.UserDTOBasic;
import org.mapstruct.Mapper;

@Mapper
public class PostDTOMapper {

    public PostDTO postToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
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

    private String getAnnounceDTO(Post post) {
        String answer = post.getText();
        int sizeOfAnnounce = 30;
        if (answer.length() < sizeOfAnnounce)
            return answer;
        else {
            answer = answer.substring(0, sizeOfAnnounce);
            return answer.trim() + "...";
        }
    }

    private UserDTOBasic getUserDTO(Post post) {
        UserDTOBasic userDTO = new UserDTOBasic();
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
}
