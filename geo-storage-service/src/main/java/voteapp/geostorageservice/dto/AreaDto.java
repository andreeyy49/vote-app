package voteapp.geostorageservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AreaDto {
    private Long id;

    @JsonProperty("parent_id")
    private Long parentId;
    private String name;
    private List<AreaDto> areas = new ArrayList<>();
}
