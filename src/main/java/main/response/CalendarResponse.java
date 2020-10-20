package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CalendarResponse extends AbstractResponse {
    private List<Integer> years;
    private Map<String, Integer> posts;
}
