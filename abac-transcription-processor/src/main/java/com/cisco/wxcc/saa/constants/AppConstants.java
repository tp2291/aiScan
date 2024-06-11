package com.cisco.wxcc.saa.constants;

public final class
AppConstants {

    private AppConstants() {}
    public static final String SCHEMA_REGISTRY_URL_JSON_PATH = "/dataPlaneConfig/schemaRegistryUrl";
    public static final String SCHEMA_REGISTRY_CONFIG_KEY="schema.registry.url";
    public static final String KAFKA_URL_JSON_PATH = "/dataPlaneConfig/kafkaUrl";
    public static final String KEY_DESERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String VALUE_DESERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String SPECIFIC_AVRO_READER_CONFIG = "true";
    public static final String AUTO_OFFSET_RESET_CONFIG = "latest";
    public static final String MAX_POLL_RECORDS_CONFIG = "5000";
    public static final String MAX_POLL_INTERVAL_MS_CONFIG = "1800000";
    public static final String RECORDING_AVAILABLE_EVENT = "recording-available";
    public static final String REDIS_ABAC_CSR_PREFIX = "abac_csr_";
    public static final long REDIS_TTL_IN_SECONDS = 4 * 60 * 60; // 4 hours in seconds
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ORG_ID = "orgId";
    public static final String GRANT_TYPE = "GRANT_TYPE";
    public static final String BROKER_URL = "BROKER_URL";
    public static final String CLIENT_TOKEN = "CLIENT_TOKEN";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final String SCOPE = "SCOPE";
    public static final int TOKEN_CACHE_EXPIRY_IN_MIN = 10;
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCOUNT_EXPIRATION = "accountExpiration";
    public static final String ASSERTION = "assertion";
    public static final String BEARER_TOKEN = "BearerToken";
    public static final String EXPIRES_IN = "expires_in";
    public static final String REQ_GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_SAML2_BEARER =
            "urn:ietf:params:oauth:grant-type:saml2-bearer";
    public static final String GRANT_TYPE_REFRESH_TOKEN ="refresh_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";
    public static final String REQ_SCOPE = "scope";
    public static final String SERVICE_SECRET = "password";
    public static final String TOKEN_TYPE = "token_type";
    public static final String TRACKING_ID = "trackingID";
    public static final String KIBANA_INTERACTION_ID_FIELD_NAME = "interaction_id";
    public static final String KIBANA_ORG_ID_FIELD_NAME = "org_id";
    public static final String KIBANA_AGENT_ID_FIELD_NAME = "agent_id";


}
