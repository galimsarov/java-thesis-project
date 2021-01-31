package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.others.ImageResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageError extends ResultResponse {
    private ImageResponse errors;
}
