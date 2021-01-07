package main.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Класс BasicResponse
 * Базовый класс для ответов. Если какие-то поля не нужны, они не
 * инициализируются
 *
 * @version 1.0
 */
@Data
public class BasicResponse {
    private int id;
    private int count;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private int parentId;
    private int moderationCount;

    // это для статистики. Не путать likeCount и likesCount, а также дизлайки и
    // просмотры
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;

    private boolean active;
    private boolean result;
    private boolean moderation;
    private boolean settings;

    private String announce;
    private String name;
    private String text;
    private String title;
    private String photo;
    private String image;
    private String email;
    private String password;
    private String secret;

    private long timestamp;

    private BasicResponse user;
    private BasicResponse errors;

    private List<BasicResponse> posts;
    private List<AdditionalResponse> comments;
    private Set<String> tags;

    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPreModeration;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;
}
