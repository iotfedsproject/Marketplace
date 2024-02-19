package eu.iotfeds.marketplace.dtos.marketplace;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

    @NotNull
    private String productId;

    @NotNull
    private String sellerId;

    @NotNull
    private double price;

    private boolean supportStream;

    @NotNull
    private String dateTimeFrom;

    @NotNull
    private String dateTimeTo;

    @NotNull
    private String fedMarketplaceId;

    @NotNull
    private boolean globalMarketplaceId;

    @NotNull
    private String name;

    @NotNull
    private List<String> description;

    private String productDetails;

    @NotNull
    private List<String> observedProperties;

    @NotNull
    private List<String> resourceIds;

    @NotNull
    private List<String> vertical;

    @NotNull
    private double access;

    @NotNull
    private long frequency;


}
