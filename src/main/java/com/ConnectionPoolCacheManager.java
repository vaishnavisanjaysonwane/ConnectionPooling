package com;

import java.util.*;

public class ConnectionPoolCacheManager {

    // Simulated in-memory cache
    private final Map<String, PoolConfigData> hsmCache = new HashMap<>();

    public ConnectionPoolCacheManager() {
        preloadFixedHSMConfigs();
    }

    private void preloadFixedHSMConfigs() {
        cacheHSMConfiguration(2);
        cacheHSMConfiguration(16);
    }

    // Simulated cache load
    public void cacheHSMConfiguration(Integer hsmId) {

        List<HSMConnection> hsmConnections = new ArrayList<>();

        // Primary HSM
        HSMConnection hsm1 = new HSMConnection();
        hsm1.setIp("127.0.0.1");
        hsm1.setPort(6543);
        hsm1.setTimeout(5000);
        hsm1.setService("payment");
        hsm1.setTag("primary");
        hsm1.setMaxConnections(5);
        hsm1.setMinConnections(1);

        // Failover HSM
        HSMConnection hsm2 = new HSMConnection();
        hsm2.setIp("127.0.0.2");
        hsm2.setPort(6544);
        hsm2.setTimeout(5000);
        hsm2.setService("payment");
        hsm2.setTag("failover");
        hsm2.setMaxConnections(3);
        hsm2.setMinConnections(1);

        hsmConnections.add(hsm1);
        hsmConnections.add(hsm2);

        PoolConfigData configData = new PoolConfigData();
        configData.setHsmConnections(hsmConnections);

        hsmCache.put(hsmId.toString(), configData);
    }

    public PoolConfigData getHSMConnections(String hsmId) {
        return hsmCache.get(hsmId);
    }

    public void removeHSMFromCache(Integer hsmId, boolean log) {
        // For simplicity, assume hsmId always maps to "payment"
        hsmCache.remove(hsmId.toString());

        if (log) {
            System.out.println("Removed HSM config for id " + hsmId);
        }
    }

    public List<HsmProfileDto> getHSMProfiles() {
        List<HsmProfileDto> profiles = new ArrayList<>();

        // HSM ID 16
        HsmProfileDto dto16 = new HsmProfileDto();
        dto16.setId(16);
        HSMProfile profile16 = new HSMProfile();
        profile16.setIp("192.168.10.16");
        profile16.setPort(6543);
        profile16.setSsl(true);
        profile16.setFailover(false);
        profile16.setStatus("active");
        profile16.setType("primary");
        profile16.setTimeout(5000);
        dto16.setHsmProfile(profile16);

        // HSM ID 2
        HsmProfileDto dto2 = new HsmProfileDto();
        dto2.setId(2);
        HSMProfile profile2 = new HSMProfile();
        profile2.setIp("192.168.10.2");
        profile2.setPort(6544);
        profile2.setSsl(false);
        profile2.setFailover(true);
        profile2.setStatus("active");
        profile2.setType("failover");
        profile2.setTimeout(5000);
        dto2.setHsmProfile(profile2);

        profiles.add(dto16);
        profiles.add(dto2);
        return profiles;
    }
}

