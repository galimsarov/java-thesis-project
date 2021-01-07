package main.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Класс AdditionalResponse
 * Реализован отдельный класс для комментариев к постам. Теоретически можно
 * использовать и BasicResponse, но тогда получаю StackOverflow
 * Так же используем для получения списка тэгов, потому что BasicResponse уже
 * есть поле tags
 * Ещё для календаря из-за posts
 *
 * @version 1.0
 */
@Data
public class AdditionalResponse {
    private int id;
    private int parentId;

    private float weight;

    private long timestamp;

    private String name;
    private String text;

    private UserWithPhotoResponse user;

    private List<AdditionalResponse> tags;
    private List<Integer> years;
    private Map<String, Integer> posts;
}
