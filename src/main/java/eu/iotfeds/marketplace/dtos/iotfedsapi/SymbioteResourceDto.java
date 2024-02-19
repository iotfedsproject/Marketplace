package eu.iotfeds.marketplace.dtos.iotfedsapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbioteResourceDto {

    private String id;

    private String description;

    private String locationName;

    private double locationLatitude;

    private double locationLongitude;

    private double locationAltitude;
}
