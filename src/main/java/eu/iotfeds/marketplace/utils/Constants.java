package eu.iotfeds.marketplace.utils;

public class Constants {

    static final String PLATFORM_ID = "platformId";
    static final String DO_CREATE_RESOURCES = "/platforms/{platformId}/resources";
    static final String DO_CREATE_RDF_RESOURCES = "/platforms/{platformId}/rdfResources";
    static final String DO_UPDATE_RESOURCES = "/platforms/{platformId}/resources";
    static final String DO_REMOVE_RESOURCES = "/platforms/{platformId}/resources";

    static final String DO_CLEAR_DATA = "/platforms/{platformId}/clearData";

    static final String SUCCESS           = "SUCCESS ";
    static final String FAILED            = "FAILED ";
    static final String PRODUCT_NOT_FOUND = "PRODUCT NOT FOUND ";

    static final String PRODUCT_COLLECTION = "product";
    static final String PRODUCTS_PATH      = "/marketplace/products";
    static final String PRODUCT_PATH       = "/marketplace/product";
    static final String ADD_PRODUCT_PATH   = "/marketplace/add_product";
    static final String PRODUCT_ID_PREFIX  = "PR_";
    static final String RH_RESOURCE_TRUST_UPDATE_QUEUE_NAME = "symbIoTe.trust.rh.resource.update";
    public static final String BAAS_PRODUCTS = "/products";
    public static final String SEARCH_MARKETPLACE = "/search_products";
    public static final String SEARCH_RESOURCES_IN_MARKETPLACE = "/search_resources";
    public static final String IOTFEDSAPI_RESOURCES_SEARCH_BY_INTERNAL_ID = "/resources/getResourceIdFromInternalID";
    public static final String IOTFEDSAPI_RESOURCES_SEARCH_L1 = "/resources/search";
    public static final String IOTFEDSAPI_RESOURCES_SEARCH_L2 = "/resources/search/l2";
    public static final String ACCESSL1_INTERNAL_ID_FEDSAPI = "/resources/access/l1/";
    public static final String ACCESSL1_ID_FEDSAPI = "/resources/access/l1/Id/";
    public static final String ACCESSL2_IOT_FEDSAPI = "/resources/access/l2/";
    public static final String ADD_L1_FEDSAPI = "/resources/add/l1";
    public static final String SHARE_L2_FEDSAPI = "/resources/shareresource";
    public static final String UNSHARE_L2_FEDSAPI = "/resources/unshareresource";

//    Baas constunts
    public static final String BAAS_ADD_RESOURCE_TO_FEDERATION = "/add_resource_fed";
    public static final String BAAS_GET_GLOBAL_PRODUCTS = "/get_global_products";
    public static final String BAAS_GET_FEDERATED_PRODUCTS = "/get_federated_products";
    public static final String BAAS_POST_DECREASE_ACCESS = "/decrease_access";
    public static final String BAAS_GET_USER_TOKENS = "/get_user_tokens";
    public static final String BAAS_POST_CHECK_ACCESS = "/check_access";

}
