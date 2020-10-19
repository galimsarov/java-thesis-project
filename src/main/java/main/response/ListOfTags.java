package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListOfTags extends AbstractResponse {
    private List<TagWithWeight> tags;
}
