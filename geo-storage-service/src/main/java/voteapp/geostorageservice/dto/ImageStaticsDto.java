package voteapp.geostorageservice.dto;

import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ConditionalOnNotWebApplication
public class ImageStaticsDto {

    private String url;

    private Long size;

    private String lastModified;

    private Integer count;

    public ImageStaticsDto(String url, Long size, String lastModified) {
        this.url = url;
        this.size = size;
        this.lastModified = lastModified;
    }
}
