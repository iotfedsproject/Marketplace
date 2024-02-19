package eu.iotfeds.marketplace.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreamingProduct extends Product {
    private String dateTimeTo;
    private long frequency;
}
