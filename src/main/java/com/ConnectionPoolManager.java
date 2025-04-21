package com;

import com.unused.HealthCheckManager;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionPoolManager {

    private final PoolConfigData poolConfigData;

    private final Map<String, List<ConnectionPool>> poolMap = new HashMap<>();
    public Integer getRetryCount(){
        return 5;
    };

    public ConnectionPoolManager(PoolConfigData poolConfig) {
        this.poolConfigData = poolConfig;
    }

    public void initilizePools() {

        ConnectionPoolCacheManager connectionPoolCacheManager = new ConnectionPoolCacheManager();
        List<HsmProfileDto> hsmProfiles = connectionPoolCacheManager.getHSMProfiles();

        for (HsmProfileDto profileDto : hsmProfiles) {
            Integer hsmId = profileDto.getId();
            HSMProfile profile = profileDto.getHsmProfile();

            HSMConnection hsmConnection = new HSMConnection();
            hsmConnection.setIp(profile.getIp());
            hsmConnection.setPort(profile.getPort());
            hsmConnection.setTimeout(profile.getTimeout());
            hsmConnection.setService(profile.getType());  // Optional, based on your needs
            hsmConnection.setTag(profile.isFailover() ? "failover" : "primary");
            hsmConnection.setMaxConnections(5); // hardcoded or profile-driven
            hsmConnection.setMinConnections(1); // hardcoded or profile-driven

            GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(hsmConnection.getMaxConnections());
            config.setMinIdle(hsmConnection.getMinConnections());
            config.setMaxIdle(hsmConnection.getMaxConnections());
            config.setBlockWhenExhausted(true);
            config.setTestOnBorrow(true);

            ConnectionFactory factory = new ConnectionFactory(hsmConnection);
            GenericObjectPool<Connection> objectPool = new GenericObjectPool<>(factory, config);

            ConnectionPool connectionPool = new ConnectionPool(hsmConnection, objectPool);

            poolMap.computeIfAbsent(hsmId.toString(), k -> new ArrayList<>()).add(connectionPool);
        }
    }


    public Map<String, List<ConnectionPool>> getAllPoolsForHSM() {
        return poolMap;
    }

    public void destroyPools() {
        for (List<ConnectionPool> poolList : poolMap.values()) {
            for (ConnectionPool pool : poolList) {
                pool.getObjectPool().close();
            }
        }
        poolMap.clear();
    }

    public void setHealthCheckManager(HealthCheckManager healthCheckManager) {
    }
}
