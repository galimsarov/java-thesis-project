package main.mapper;

import main.model.*;
import main.response.*;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс PostResponseMapper
 * Класс для подготовки объектов для вида, воспринимаемого фронтом
 *
 * @version 1.1
 */
@Mapper
public class PostResponseMapper {
    /**
     * Метод postToBasicResponse
     * Метод готовит объекты для вывода постов:
     *      - для главной страницы и подразделов "Новые", "Самые обсуждаемые",
     *      "Лучшие" и "Старые"
     *      - соответствующие поисковому запросу - строке query
     *      - посты за указанную дату, переданную в запросе в параметре date
     *      - привязанные к тэгу, который был передан методу в качестве параметра
     *      tag
     *      - которые требуют модерационных действий (которые нужно утвердить или
     *      отклонить)
     *      - посты, которые создал я (в соответствии с полем user_id в таблице
     *      posts базы данных)
     *
     * @param post пост, который нужно преобразовать, к виду PostResponse
     */
    public BasicResponse postToBasicResponse(Post post) {
        BasicResponse response = new BasicResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().getTime()/1000);

        BasicResponse user = new BasicResponse();
        user.setId(post.getUser().getId());
        user.setName(post.getUser().getName());
        response.setUser(user);

        response.setTitle(post.getTitle());
        response.setAnnounce(getAnnounceDTO(post));
        response.setLikeCount(getLikes(post));
        response.setDislikeCount(getDislikes(post));
        response.setCommentCount(post.getPostCommentList().size());
        response.setViewCount(post.getViewCount());
        return response;
    }

    /**
     * Метод postToSpecificPost
     * Метод готовит объекты для вывода конкретного поста для отображения на
     * странице поста, в том числе, список комментариев и тэгов, привязанных
     * к данному посту
     *
     * @param post пост, который нужно преобразовать
     */
    public BasicResponse postToSpecificPost(Post post) {
        BasicResponse response = new BasicResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().getTime()/1000);
        response.setActive(post.isActive());

        BasicResponse userDTO = new BasicResponse();
        userDTO.setId(post.getUser().getId());
        userDTO.setName(post.getUser().getName());
        response.setUser(userDTO);

        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setLikeCount(getLikes(post));
        response.setDislikeCount(getDislikes(post));
        response.setViewCount(post.getViewCount());
        response.setComments(getCommentsResponse(post.getPostCommentList()));

        Set<Tag> tagSet = post.getTagSet();
        if (tagSet.size() != 0) {
            Set<String> stringSet = new HashSet<>();
            for (Tag tag : tagSet)
                stringSet.add(tag.getName());
            response.setTags(stringSet);
        }
        else
            response.setTags(new HashSet<>());

        return response;
    }

    private String getAnnounceDTO(Post post) {
        Pattern pattern = Pattern.compile("</?[a-z]+>");
        Matcher matcher = pattern.matcher(post.getText());
        String answer = matcher.replaceAll("...");

        pattern = Pattern.compile("&nbsp;");
        matcher = pattern.matcher(answer);
        answer = matcher.replaceAll("...");

        pattern = Pattern.compile("<[a-z]+ .+\">");
        matcher = pattern.matcher(answer);
        answer = matcher.replaceAll("...");

        int sizeOfAnnounce = 200;
        if (answer.length() < sizeOfAnnounce)
            return answer;
        else {
            answer = answer.substring(0, sizeOfAnnounce);
            return answer.trim() + "...";
        }
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

    private List<AdditionalResponse> getCommentsResponse
            (List<PostComment> comments) {
        List<AdditionalResponse> additionalRespons = new ArrayList<>();
        for (PostComment comment : comments) {
                AdditionalResponse response = new AdditionalResponse();
                response.setId(comment.getId());
                response.setTimestamp(comment.getTime().getTime()/1000);
                response.setText(comment.getText());

                UserWithPhotoResponse userWithPhoto =
                        new UserWithPhotoResponse();
                userWithPhoto.setId(comment.getUser().getId());
                userWithPhoto.setName(comment.getUser().getName());
                userWithPhoto.setPhoto(comment.getUser().getPhoto());

                response.setUser(userWithPhoto);
                if (comment.getParentId() != null)
                    response.setParentId(comment.getParentId());
                additionalRespons.add(response);
        }
        return additionalRespons;
    }
}
