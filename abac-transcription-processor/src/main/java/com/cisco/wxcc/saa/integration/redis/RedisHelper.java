package com.cisco.wxcc.saa.integration.redis;

import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import io.lettuce.core.SetArgs;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class RedisHelper implements Serializable {
    private transient RedisAdvancedClusterAsyncCommands<String, String> clusterAsyncCmd;

    public RedisHelper() throws ConfigurationException {
        RedisClusterConfig redisClusterConfig = new RedisClusterConfig();
        clusterAsyncCmd = redisClusterConfig.redisAsyncCommands();
    }

    /** Get all Set values. */
    public Set<String> smembers(String key) throws ExecutionException, InterruptedException {

            return clusterAsyncCmd.smembers(key).get();
    }

    /** Add members in Set. */
    public void sadd(String key, String... members) {

            clusterAsyncCmd.sadd(key, members);
    }

    /** Check isMember of Set. */
    public boolean sismember(String key, String member) throws ExecutionException, InterruptedException {

            return clusterAsyncCmd.sismember(key, member).get();
    }

    /** Set Remove. */
    public long srem(String key, String member) throws ExecutionException, InterruptedException {

            return clusterAsyncCmd.srem(key, member).get();
    }

    /** HGET operation. */
    public String hget(String hasName, String keyName)
            throws InterruptedException, ExecutionException {

        return clusterAsyncCmd.hget(hasName, keyName).get();
    }

    /** HGETALL operation. */
    public Map<String, String> hgetall(String eachHashKey)
            throws ExecutionException, InterruptedException {

            return clusterAsyncCmd.hgetall(eachHashKey).get();
    }

    /** hexists operation. */
    public boolean hexists(String hasName, String keyName)
            throws InterruptedException, ExecutionException {

        return clusterAsyncCmd.hexists(hasName, keyName).get();
    }

    /** GET operation. */
    public String get(String key) throws InterruptedException, ExecutionException {

            return clusterAsyncCmd.get(key).get();
    }

    /** HDEL operation. */
    public void hdel(String key, String... fields) {

            clusterAsyncCmd.hdel(key, fields);
    }

    /** HSET operation. */
    public void hset(String key, String field, String value) {

            clusterAsyncCmd.hset(key, field, value);
    }


    /** SET operation. */
    public void set(String key, String value) {

            clusterAsyncCmd.set(key, value);
    }

    public void setex(String key, long ttlInSeconds, String value)  {

        clusterAsyncCmd.setex(key, ttlInSeconds, value);
    }

    /** DEL operation. */
    public void del(String... key) {

            clusterAsyncCmd.del(key);
    }

    /** SET operation. */
    public void set(String key, String value, SetArgs args) {

            clusterAsyncCmd.set(key, value, args);
    }

    public void hmset(String key, Map<String, String> value) {

            clusterAsyncCmd.hmset(key, value);
    }

    public void expire(String key, long expiry)  {

            clusterAsyncCmd.expire(key, expiry);
    }

}
