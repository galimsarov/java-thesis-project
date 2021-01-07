package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Класс AdditionalRequest
 * Дополнительный класс для запросов
 *
 * @version 1.0
 */
@Data
public class AdditionalRequest {
    @JsonProperty("post_id")
    private int postId;

    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;

    @JsonProperty("POST_PREMODERATION")
    private boolean postPreModeration;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;
}
