package eu.iotfeds.marketplace.services.platform;

import eu.h2020.symbiote.core.cci.PlatformRegistryResponse;
import eu.iotfeds.marketplace.rabbit.CommunicationException;
import eu.iotfeds.marketplace.rabbit.RabbitManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {
    private static Log log = LogFactory.getLog(PlatformService.class);
    @Autowired
    private RabbitManager rabbitManager;

    public ResponseEntity getPlatformDetailsFromRegistry(String platformId) {
        try {
            PlatformRegistryResponse registryResponse = rabbitManager.sendGetPlatformDetailsMessage(platformId);
            if (registryResponse != null) {
                if (registryResponse.getStatus() != HttpStatus.OK.value()) {
                    log.debug(registryResponse.getMessage());
                    return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>(registryResponse, new HttpHeaders(), HttpStatus.OK);
                }
            } else {
                String message = "Registry unreachable!";
                log.warn(message);
                return new ResponseEntity<>(message,
                        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (CommunicationException e) {
            String message = "Registry threw CommunicationException";

            log.warn(message, e);
            return new ResponseEntity<>("Registry threw CommunicationException: " + e.getMessage(),
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
