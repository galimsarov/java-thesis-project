package main.response.ids;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdNameResp extends IdResponse {
    private String name;
}
