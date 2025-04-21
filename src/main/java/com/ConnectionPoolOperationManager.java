package com;

import com.unused.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionPoolOperationManager {

    ConnectionPoolCacheManager cacheManager;

    HealthCheckManager healthCheckManager;
    CommandManager commandManager;
    HSMConnectionMetric metric;
    HSMShutdownHandler shutdownHandler;
    HazelCastDataFetcher hzClient;


    public void setHsmId(Integer hsmId) {
        this.hsmId = hsmId;
    }

    public void setRklMode(String rklMode) {
        this.rklMode = rklMode;
    }

    private Integer hsmId;
    private String rklMode;

    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolOperationManager.class);
    private AtomicBoolean isStartedOnThisInstance = new AtomicBoolean(false);
    private Map<Integer, ConnectionPoolManager> pools = new HashMap<>();

    public Map<Integer, ConnectionPoolManager> getPools() {
        return pools;
    }

    public void setCacheManager(ConnectionPoolCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    public void startConnections(Integer hsmId) {
        startConnections(hsmId, false);
    }


    public HSMConnectionExecutionResponse sendCommandToHsm(Object command) {
        ConnectionPoolManager connectionPoolManager = pools.get(hsmId);
        if (connectionPoolManager == null) {
            log.error("HSM connection cache not found");
            return null;
        }
        commandManager.setRklMode(rklMode);
        log.info("Rkl mode set to command manager");
        return commandManager.sendCommand(command, connectionPoolManager.getAllPoolsForHSM(), connectionPoolManager.getRetryCount());
    }


    public void startConnections(Integer hsmId, boolean isEventTriggered) {
        if (isStartedOnThisInstance.get()) {
            log.info("fetching pool manager");
            ConnectionPoolManager connectionPoolManager = getConnectionPoolManager(hsmId);
            log.info("Initializing pools");
            connectionPoolManager.initilizePools();
            Map<String, List<ConnectionPool>> allPools = connectionPoolManager.getAllPoolsForHSM();

            //true → if all HSMs have either null or empty connection pool lists.
            //false → if any HSM has a non-empty connection pool list.
            boolean isEmptyPools = allPools.values().stream().allMatch(p -> p == null || p.isEmpty());
            if (isEmptyPools) {
                log.error("No connection pools created for selected profile");
            }
            pools.put(hsmId, connectionPoolManager);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted");
            }
            updateStartFlags();

            if (!isEventTriggered) {
                hzClient.updateStartStatus(hsmId, Boolean.TRUE);
            } else {
                log.info("Caching hsm config");
                CacheDataContainer.getInstance().loadData(hzClient);
            }
        } else {
            if (!testConnection(hsmId)) {

            }
        }
    }


    public void restartHSM(Integer hsmId) {
        stopConnections(hsmId);
        startConnections(hsmId);
    }


    private boolean testConnection(Integer hsmId) {
        try {
            log.info("Getting connection pool manager for " + hsmId);
            ConnectionPoolManager connectionPoolManager = pools.get(hsmId);
            log.info("Pool Manager for hsm {} : {}", hsmId, connectionPoolManager);
            healthCheckManager.setPoolManager(connectionPoolManager);
            return healthCheckManager.performConnectionStatusCheck();
        } catch (Exception e) {
            log.error("Unable to perform status check : {}", e.getMessage());
            return false;
        }
    }


    private ConnectionPoolManager getConnectionPoolManager(Integer hsmId) {
        ConnectionPoolManager connectionPoolManager = pools.get(hsmId);
        if (connectionPoolManager == null) {
            PoolConfigData poolConfig = null;
            try {
                log.info("Fetching hsm configuration from cache");
                poolConfig = getConnectionPoolConfigFromCache(hsmId);
            } catch (Exception e) {
                log.info("Fetching configuration from database");
                cacheManager.cacheHSMConfiguration(hsmId);
                poolConfig = getConnectionPoolConfigFromCache(hsmId);
            }
            connectionPoolManager = new ConnectionPoolManager(poolConfig);
            connectionPoolManager.setHealthCheckManager(healthCheckManager);
        }
        return connectionPoolManager;
    }

    private void updateStopFlags() {
//        metric.setConnectionStatus(Boolean.FALSE.booleanValue());
//        shutdownHandler.getReadinessStatus().set(Redinessstate.Not_Ready);
        isStartedOnThisInstance.compareAndSet(true, false);
    }

    private void updateStartFlags() {
//      metric.setConnectionStatus(Boolean.TRUE.booleanValue());
//      shutdownHandler.getReadinessStatus().set(Redinessstate.Ready);
        isStartedOnThisInstance.compareAndSet(false, true);
    }


    public void stopConnections(Integer hsmId) {
        stopConnections(hsmId, false);

    }

    public void stopConnections(Integer hsmId, boolean isEventTriggered) {
        log.info("Received stop connection request");
        ConnectionPoolManager connectionPoolManager = pools.get(hsmId);
        if (connectionPoolManager == null) {
            connectionPoolManager.destroyPools();
            pools.remove(hsmId);
            cacheManager.removeHSMFromCache(hsmId, true);
            updateStopFlags();
            log.info("HSM stopped successfully");
            if (!isEventTriggered) {
                hzClient.updateStartStatus(hsmId, Boolean.FALSE);
            }
        }
    }

    private PoolConfigData getConnectionPoolConfigFromCache(Integer hsmId) {
        return cacheManager.getHSMConnections(String.valueOf(hsmId));
    }
}
