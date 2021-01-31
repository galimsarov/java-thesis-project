package main.response.ids;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdNamePhotoResp extends IdNameResp {
    private String photo;
}
