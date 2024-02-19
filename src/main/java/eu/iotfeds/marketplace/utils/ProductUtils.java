package eu.iotfeds.marketplace.utils;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductUtils {

    @Value("${symbIoTe.coreUser.username}")
    public static String coreUserName = "icom1";

    @Value("${symbIoTe.coreUser.password}")
    public static String coreUserPassword = "icom";

    public static JSONObject getObservation(String resourceId, String platformID){
        /////////////////////// REMOVE IT ////////////////
        coreUserName     = "icom1";
        coreUserPassword = "icom";
        /////////////////////////////////////////////////

        String POST_ACCESS_L1_URL = "https://symbiote-core.iotfeds.intracom-telecom.com/symbioteapi/resources/access/l1/Id/";

        RestTemplate restTemplate = new RestTemplate();

        /*
         * Build headers.
         */

        HttpHeaders httpHeaders   = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        JSONObject body1 = new JSONObject();
        body1.put("clientId","observationClient");
        body1.put("username",coreUserName);
        body1.put("password",coreUserPassword);
        body1.put("platformId","SymbIoTe_Core_AAM");

        JSONObject body = new JSONObject();
        body.put("platformCredentials",body1);
        body.put("remotePlatformId",platformID);

        System.out.println("resourceId = " + resourceId);
        System.out.println("platformID = " + platformID);
        System.out.println(body);

        HttpEntity<String> entity = new HttpEntity<String>(body.toString(), httpHeaders);
        System.out.println("11");
        ResponseEntity<?> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(POST_ACCESS_L1_URL + resourceId, entity, String.class);
        }catch(Exception ex){
            System.out.println("Exception in restTemplate.postForEntity");
            JSONObject jsonObject = new JSONObject();
            return jsonObject;
        }
        System.out.println("22");
        HttpStatus httpStatus = responseEntity.getStatusCode();
        System.out.println("33");
        String observation = "";
        if(httpStatus.value() == 200) {
            String receivedBody   = (String) responseEntity.getBody();
            System.out.println("44");
            JSONObject jsonObject = new JSONObject(receivedBody);
            System.out.println("Observation = " + jsonObject);
            System.out.println("55");
            return jsonObject;
        }else {
            System.out.println("Observation failed for resourceId = " + resourceId + " platformId = " + platformID);
            System.out.println("Response code: " + httpStatus.value());
            JSONObject jsonObject = new JSONObject();
            return jsonObject;
        }

    }//end
    //------------------------------------------------------------------------------------
    public static String getProductId(){
        return Constants.PRODUCT_ID_PREFIX + System.currentTimeMillis();
    }//end

    public static String convertListToString(List<String> list){
        if(list == null || list.isEmpty()) {
            return "";
        } else {
            return list.stream()
                    .map(n -> String.valueOf(n))
                    .collect(Collectors.joining(", ", "{", "}"));
        }
    }

    public static String convertMapToString(Map<String, Object> map) {
        if(map == null || map.isEmpty()) {
            return "";
        }else {
            return map.keySet().stream()
                    .map(key -> key + "=" + map.get(key))
                    .collect(Collectors.joining(", ", "{", "}"));
        }
    }
}
