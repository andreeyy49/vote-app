package voteapp.geostorageservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "countries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Country {

    @Id
    private Long id;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @JsonIgnore
    private List<City> cities = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        boolean isCitiesEquals;
        if(country.getCities().size() != getCities().size()) return false;
        for(int i = 0; i < country.getCities().size(); i++) {
            isCitiesEquals = getCities().get(i).equals(country.getCities().get(i));
            if(!isCitiesEquals) return false;
        }
        return Objects.equals(getId(), country.getId()) && Objects.equals(getTitle(), country.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getCities());
    }
}
