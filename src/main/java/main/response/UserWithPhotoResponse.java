package main.response;

import lombok.Data;

/**
 * Класс UserWithPhotoResponse
 * Реализован отдельный класс для авторов комментариев к постам. Теоретически
 * можно использовать и BasicResponse, но тогда получаю StackOverflow
 *
 * @version 1.0
 */
@Data
public class UserWithPhotoResponse {
    private int id;
    private String name;
    private String photo;
}
