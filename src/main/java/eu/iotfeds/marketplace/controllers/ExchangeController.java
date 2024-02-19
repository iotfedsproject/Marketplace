package eu.iotfeds.marketplace.controllers;

import eu.iotfeds.marketplace.dtos.marketplace.exchange.ExchangeOfferCreateDto;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.ExchangeOfferDto;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.TokenExchange;
import eu.iotfeds.marketplace.dtos.marketplace.exchange.UserToken;
import eu.iotfeds.marketplace.models.CoreUser;
import eu.iotfeds.marketplace.persistence.exchange.OfferStatus;
import eu.iotfeds.marketplace.services.ExchangeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("exchange")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @ApiOperation(value = "Submit new exchange offer")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeOfferDto> createExchangeOffer(@RequestBody ExchangeOfferCreateDto exchangeOfferDto,
                                                                 @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.createOffer(exchangeOfferDto, username(principal)), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve exchange offer")
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeOfferDto> getExchangeOffer(@PathVariable String offerId,
                                                             @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.getExchangeOffer(offerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Cancel exchange offer")
    @RequestMapping(path = "/{id}/cancel", method = RequestMethod.POST)
    public ResponseEntity<String> cancelExchangeOffer(@PathVariable String offerId,
                                                                @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.cancelOffer(offerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Accept exchange offer")
    @RequestMapping(path = "/{id}/accept", method = RequestMethod.POST)
    public ResponseEntity<String> acceptExchangeOffer(@PathVariable String offerId,
                                                      @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.acceptOffer(offerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Decline exchange offer")
    @RequestMapping(path = "/{id}/decline", method = RequestMethod.POST)
    public ResponseEntity<String> declineExchangeOffer(@PathVariable String offerId,
                                                      @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.declineOffer(offerId), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve user's own exchange offers")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExchangeOfferDto>> getExchangeOffers(@ApiParam(value = "Exchange offer status") @RequestParam(required = false) OfferStatus status,
                                                                    @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.getOfferList(username(principal), status), HttpStatus.OK);
    }

    @ApiOperation(value = "Get user tokens")
    @RequestMapping(path = "/tokens/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserToken>> buyProduct(@ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.getUserTokensFromBaas(username(principal)), HttpStatus.OK);
    }

    @ApiOperation(value = "Put token for exchange")
    @RequestMapping(path = "/tokens/", method = RequestMethod.POST)
    public ResponseEntity<String> putToken(@RequestParam String userId,
                                           @RequestParam String productId,
                                           @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {
        return new ResponseEntity<>(exchangeService.putTokenForExchange(userId, productId), HttpStatus.OK);
    }

    @ApiOperation(value = "Remove token from exchange")
    @RequestMapping(path = "/tokens/", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeToken(@RequestParam String userId,
                                              @RequestParam String productId,
                                              @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {
        return new ResponseEntity<>(exchangeService.removeTokenFromExchange(userId, productId), HttpStatus.OK);
    }

    @ApiOperation(value = "Exchange tokens")
    @RequestMapping(path = "/tokens/", method = RequestMethod.PUT)
    public ResponseEntity<String> exchangeTokens(@RequestBody TokenExchange tokenExchange,
                                                 @ApiIgnore @AuthenticationPrincipal Principal principal) throws IOException {

        return new ResponseEntity<>(exchangeService.exchangeTokens(tokenExchange), HttpStatus.OK);
    }

    private String username(Principal principal){
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        CoreUser user = (CoreUser) token.getPrincipal();
       return user.getValidUsername();
    }
}
