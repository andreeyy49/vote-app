package voteapp.geostorageservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryDto {
    private Long id;

    @JsonIgnore
    private String name;

    @JsonProperty("title")
    private String title;

    private List<CityDto> cities = new ArrayList<>();

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
        this.title = name;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public CountryDto(Long id, String title, List<CityDto> cities) {
        this.title = title;
        this.cities = cities;
        this.name = null;
        this.id = id;
    }
}
