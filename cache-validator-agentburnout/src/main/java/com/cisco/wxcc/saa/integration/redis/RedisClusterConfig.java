package com.cisco.wxcc.saa.integration.redis;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import com.cisco.wxcc.saa.helper.ConfigHelper;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.models.partitions.RedisClusterNode;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.Delay;
import io.lettuce.core.resource.DirContextDnsResolver;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.App.orgId;
import static com.cisco.wxcc.saa.constants.AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME;
import static com.cisco.wxcc.saa.constants.AppConstants.KIBANA_ORG_ID_FIELD_NAME;

@Slf4j
public class RedisClusterConfig {

    private Configuration appProps;

    private String redisHostname;

    private String redisPort;

    private String redisCredsFilePath;

    private RedisClusterClient redisClusterClient;
    public StatefulRedisClusterConnection<String, String> statefulRedisClusterConnection;
    public static final int SOCKET_CONNECTION_TIMEOUT_MILLIS = 10000;
    public static final int COMMAND_TIMEOUT_SECONDS = 60;

    public RedisClusterConfig() {
        try {
            appProps = ConfigHelper.loadAppConfig();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            log.info("Unable to load configs : "+ e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
        }
        redisHostname = appProps.getString("REDIS_HOST") ;
        redisPort = appProps.getString("REDIS_PORT");
        redisCredsFilePath= appProps.getString("VAULT_REDIS_FILE_PATH");
    }

    private String getRedisPassword() throws ConfigurationException, IOException {
        Properties redisCreds = ConfigHelper.loadFromFile(redisCredsFilePath);
        return redisCreds.getProperty(AppConstants.PASSWORD);
    }

    public RedisAdvancedClusterAsyncCommands<String, String> redisAsyncCommands() throws ConfigurationException, IOException {

        final var  clientResources = getClientResources();
        final String password = getRedisPassword();
        if (!password.isEmpty()) {
            redisClusterClient = RedisClusterClient.create(
                    clientResources,
                    RedisURI.builder()
                            .withHost(redisHostname)
                            .withPort(Integer.parseInt(redisPort))
                            .withPassword(password)
                            .withSsl(true)
                            .build());

            final var socketOptions = getSocketOptions();

            final var topologyOptions = getClusterTopologyRefreshOptions();

            final var timeoutOptions = getTimeoutOptions();

            final var clusterClientOptions = getClusterClientOptions(socketOptions, topologyOptions, timeoutOptions);

            redisClusterClient.setOptions(clusterClientOptions);

            statefulRedisClusterConnection = redisClusterClient.connect();
            log.info("StatefulRedisClusterConnection instance :: {}", statefulRedisClusterConnection);
            return statefulRedisClusterConnection.async();
        } else {
            log.error("Invalid redis credentials");
            return null;
        }
    }

    private ClusterClientOptions getClusterClientOptions(SocketOptions socketOptions, ClusterTopologyRefreshOptions topologyOptions, TimeoutOptions timeoutOptions) {
        return ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyOptions)
                .socketOptions(socketOptions)
                .autoReconnect(true)
                .timeoutOptions(timeoutOptions)
                .nodeFilter(it ->
                        !(it.is(RedisClusterNode.NodeFlag.FAIL)
                                || it.is(RedisClusterNode.NodeFlag.EVENTUAL_FAIL)
                                || it.is(RedisClusterNode.NodeFlag.NOADDR))) // Filter out Predicate
                .validateClusterNodeMembership(false)
                .build();
    }

    private ClusterTopologyRefreshOptions getClusterTopologyRefreshOptions() {
        return ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                .enablePeriodicRefresh()
                .dynamicRefreshSources(true)
                .build();
    }

    private ClientResources getClientResources() {
        return DefaultClientResources.builder()
                .reconnectDelay(
                        Delay.fullJitter(
                                Duration.ofMillis(100), // minimum 100 millisecond delay
                                Duration.ofSeconds(5), // maximum 5 second delay
                                100, TimeUnit.MILLISECONDS)) // 100 millisecond base
                .dnsResolver(new DirContextDnsResolver())
                .build();
    }
    private TimeoutOptions getTimeoutOptions() {
        return TimeoutOptions.builder()
                .timeoutCommands(true)
                .fixedTimeout(Duration.ofSeconds(COMMAND_TIMEOUT_SECONDS))
                .build();
    }

    private SocketOptions getSocketOptions() {
        return SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(SOCKET_CONNECTION_TIMEOUT_MILLIS))
                .keepAlive(true)
                .build();
    }

    public void destroy() {
        statefulRedisClusterConnection.close();
        redisClusterClient.shutdown();
    }
}
