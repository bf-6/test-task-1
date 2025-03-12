package test.task.dto;


import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UrlUpdateDTO {
    private JsonNullable<String> shortId;
}
