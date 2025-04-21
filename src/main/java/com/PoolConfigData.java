package com;

import java.util.List;

public class PoolConfigData {

    private List<HSMConnection> hsmConnections;

    public List<HSMConnection> getHsmConnections() {
        return hsmConnections;
    }

    public void setHsmConnections(List<HSMConnection> hsmConnections) {
        this.hsmConnections = hsmConnections;
    }
}
