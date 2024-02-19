package eu.iotfeds.marketplace.services;

import eu.iotfeds.marketplace.dtos.marketplace.exchange.ExchangeOfferCreateDto;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.ExchangeOfferDto;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.TokenExchange;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.UserToken;
import eu.iotfeds.marketplace.http.baas.BaasRest;
import eu.iotfeds.marketplace.persistence.exchange.ExchangeOffer;
import eu.iotfeds.marketplace.persistence.exchange.OfferStatus;
import eu.iotfeds.marketplace.repository.exchange.ExchangeRepository;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeService {

    @Autowired
    private BaasRest baasRest;

    @Autowired
    private ExchangeRepository exchangeRepository;

    public ExchangeOfferDto createOffer(ExchangeOfferCreateDto exchangeOfferCreateDto, String userId){
        log.info("New exchange offer submitted");

        ExchangeOffer exchangeOffer = new ExchangeOffer(
                null,
                exchangeOfferCreateDto.getUserId(),
                exchangeOfferCreateDto.getToken(),
                exchangeOfferCreateDto.getTargetToken(),
                OfferStatus.pending,
                false,
                null
        );

        exchangeOffer = exchangeRepository.save(exchangeOffer);

        //TODO: create notification

        return convertToDto(exchangeOffer);

    }

    public ExchangeOfferDto getExchangeOffer(String offerId){
        log.info("Retrieving exchange offer with ID: [{}]", offerId);

        ExchangeOffer exchangeOffer = exchangeRepository.
                findById(offerId).orElseThrow(()-> new EntityNotFoundException("Exchange Offer with ID: [" +offerId+ "] not found!"));
        return convertToDto(exchangeOffer);
    }

    public String cancelOffer(String offerId){
        log.info("Retrieving exchange offer with ID: [{}]", offerId);

        ExchangeOffer exchangeOffer = exchangeRepository.
                findById(offerId).orElseThrow(()-> new EntityNotFoundException("Exchange Offer with ID: [" +offerId+ "] not found!"));

        exchangeOffer.setStatus(OfferStatus.cancelled);
        exchangeRepository.save(exchangeOffer);

        return "Exchange offer successfully cancelled";
    }

    public String acceptOffer(String offerId){

        log.info("Retrieving exchange offer with ID: [{}]", offerId);

        ExchangeOffer exchangeOffer = exchangeRepository.
                findById(offerId).orElseThrow(()-> new EntityNotFoundException("Exchange Offer with ID: [" +offerId+ "] not found!"));

        exchangeOffer.setAccepted(true);
        exchangeOffer.setStatus(OfferStatus.completed);
        exchangeRepository.save(exchangeOffer);

        //TODO: create notification

        return "Exchange offer successfully accepted";
    }

    public String declineOffer(String offerId){
        log.info("Retrieving exchange offer with ID: [{}]", offerId);

        ExchangeOffer exchangeOffer = exchangeRepository.
                findById(offerId).orElseThrow(()-> new EntityNotFoundException("Exchange Offer with ID: [" +offerId+ "] not found!"));

        exchangeOffer.setStatus(OfferStatus.completed);
        exchangeRepository.save(exchangeOffer);

        //TODO: create notification

        return "Exchange offer successfully declined";
    }

    public List<ExchangeOfferDto> getOfferList(String userId, OfferStatus status){
        log.info("Getting exchange offers for user: [{}]", userId);
        List<ExchangeOffer> offerList = exchangeRepository.getOfferList(userId, status);

        return offerList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserToken> getUserTokensFromBaas(String userId){
        List<UserToken> tokenList = new ArrayList<>();
        try{
            String response = baasRest.getUserTokens(userId);
            tokenList = extractTokenList(response);
        }
        catch(Exception e){
            log.info("Error: [{}]", e.getMessage());
        }

        return tokenList;
    }

    private List<UserToken> extractTokenList(String json){
        List<UserToken> tokenList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        for(String key: jsonObject.keySet()){
            JSONObject nestedObject = (JSONObject) jsonObject.get(key);
            UserToken token = new UserToken(
                    key,
                    (int)nestedObject.get("AccessTimes"),
                    (String)nestedObject.get("DataAvailableFrom"),
                    (String)nestedObject.get("DataAvailableUntil"),
                    (String)nestedObject.get("Marketplace"),
                    (boolean)nestedObject.get("toBeExchanged"),
                    (boolean)nestedObject.get("userHasRated")
            );
            tokenList.add(token);
        }

        return tokenList
                .stream()
                .filter(UserToken::isToBeExchanged)
                .collect(Collectors.toList());
    }

    public String putTokenForExchange(String userId, String productId){
        log.info("Registering token to blockchain for exchange");

        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("product_id", productId);
        try{
            ResponseEntity<String> resp = baasRest.putToken(body);
            if(resp.getStatusCode()== HttpStatus.OK)
                return "Token successfully registered";
            else
                return "Error in registering token: "+resp.getStatusCode();
        }
        catch(Exception e){
            log.error(e.getMessage());
            return "Error in registering token: "+e.getMessage();
        }

    }

    public String removeTokenFromExchange(String userId, String productId){
        log.info("Removing token from exchange");

        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("product_id", productId);
        try{
            ResponseEntity<String> resp = baasRest.removeToken(body);
            if(resp.getStatusCode()== HttpStatus.OK)
                return "Token successfully unregistered";
            else
                return "Error in unregistering token: "+resp.getStatusCode();
        }
        catch(Exception e){
            log.error(e.getMessage());
            return "Error in unregistering token: "+e.getMessage();
        }

    }

    public String exchangeTokens(TokenExchange tokenExchange){
        log.info("Exchanging tokens");

        try{
            ResponseEntity<String> resp = baasRest.exchangeTokens(tokenExchange);
            if(resp.getStatusCode()== HttpStatus.OK)
                return "Tokens successfully exchanged";
            else
                return "Error in exchanging tokens: "+resp.getStatusCode();
        }
        catch(Exception e){
            log.error(e.getMessage());
            return "Error in exchanging tokens: "+e.getMessage();
        }

    }

    private ExchangeOfferDto convertToDto(ExchangeOffer exchangeOffer){
        return new ExchangeOfferDto(
                exchangeOffer.getId(),
                exchangeOffer.getUserId(),
                exchangeOffer.getToken(),
                exchangeOffer.getTargetToken(),
                exchangeOffer.getStatus(),
                exchangeOffer.isAccepted(),
                exchangeOffer.getDateSubmitted()
        );
    }

}
