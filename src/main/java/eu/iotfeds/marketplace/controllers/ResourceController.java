package eu.iotfeds.marketplace.controllers;

import eu.h2020.symbiote.core.ci.QueryResourceResult;
import eu.iotfeds.marketplace.dtos.marketplace.request.*;
import eu.iotfeds.marketplace.services.ResourceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @ApiOperation(value = "End-point to search for resources that match specific criteria")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful search", response = QueryResourceResult.class, responseContainer = "List")})
    @RequestMapping(path = "/search/l1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

//    public ResponseEntity<?> searchL1Resources(@ApiIgnore @AuthenticationPrincipal Principal principal,
//                                               @RequestBody SearchResourceL1Request request){
//    return ResponseEntity.ok(resourceService.searchResourcesL1(principal,request));
//}
    public ResponseEntity<?> searchL1Resources(@RequestBody SearchResourceL1Request request){
        return new ResponseEntity<>(resourceService.searchResourcesL1(request), HttpStatus.OK);
    }

//    @PostMapping("/search/L2")
//    public ResponseEntity<?> searchL2Resources(@ApiIgnore @AuthenticationPrincipal Principal principal,
//                                             @RequestBody SearchResourceL2Request request){
    @PostMapping("/search/l2")
    public ResponseEntity<?> searchL2Resources(@RequestBody SearchResourceL2Request request){
        return ResponseEntity.ok(resourceService.searchResourcesL2(request));
    }

    @PostMapping("/access/l1/Id/{resourceId}")
    public ResponseEntity<?> accessResourceL1WithId(@PathVariable String resourceId,
                                                    @RequestParam(required = false) String fromDate,
                                                    @RequestParam(required = false) String toDate,
                                                    @RequestParam(required = false) Integer top,
                                                    @RequestBody IotfedsAccessL1Request request){
        return ResponseEntity.ok(resourceService.iotfedsAccessL1Id(resourceId, fromDate, toDate, top, request));
    }

    @PostMapping("/access/l1/{resourceInternalId}")
    public ResponseEntity<?> accessResourceL1WithInternalId(@PathVariable String resourceInternalId,
                                                            @RequestParam(required = false) String fromDate,
                                                            @RequestParam(required = false) String toDate,
                                                            @RequestParam(required = false) Integer top,
                                                            @RequestBody IotfedsAccessL1Request request){
        return ResponseEntity.ok(resourceService.iotfedsAccessL1InternalId(resourceInternalId,
                fromDate, toDate, top, request));
    }

    @PostMapping("/access/l2/{resourceInternalId}")
    public ResponseEntity<?> accessResourceL2WithInternalId(@PathVariable String resourceInternalId, @RequestBody IotfedsAccessL2Request request){
        return ResponseEntity.ok(resourceService.iotfedsAccessL2InternalId(resourceInternalId, request));
    }

    @PostMapping("/add/l1")
    public ResponseEntity<?> addL1Resource(@RequestBody RegisterResourceL1Request request) {
        return ResponseEntity.ok(resourceService.iotfedsRegisterL1(request));
    }

    @PostMapping("/share")
    public ResponseEntity<?> shareResource(@RequestBody ShareResourceRequest request) {
        if(request.getPlatformCredentials().isInValid()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resourceService.iotfedsShareL2(request));
    }
}
