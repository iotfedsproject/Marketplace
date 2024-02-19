package eu.iotfeds.marketplace.dtos.marketplace.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlatformCredentials {
    @JsonProperty("platformId")
    @ApiModelProperty(notes = "Platform from which the user requests a token", example = "icom-platform", required = true)
    private String localPlatformId;
    @JsonProperty("username")
    @ApiModelProperty(notes = "Platform user", example = "username", required = true)
    private String username;
    @JsonProperty("password")
    @ApiModelProperty(notes = "Platform user password", example = "password", required = true)
    private String password;
    @JsonProperty("clientId")
    @ApiModelProperty(notes = "Client Id that will be used for the requests", example = "Test_Client", required = false)
    private String clientId;

    public boolean isInValid() {
        return localPlatformId == null || "".equals(localPlatformId)
                || username == null || "".equals(username)
                || password == null || "".equals(password)
                || clientId == null || "".equals(clientId);
    }

    @Override
    public String toString() {
        return "PlatformCredentials{" +
                "localPlatformId='" + localPlatformId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
