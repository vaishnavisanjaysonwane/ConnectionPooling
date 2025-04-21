package com;

import org.apache.commons.pool2.impl.GenericObjectPool;

public class ConnectionPool {
    private final HSMConnection config;
    private final GenericObjectPool<Connection> objectPool;

    public ConnectionPool(HSMConnection config, GenericObjectPool<Connection> objectPool) {
        this.config = config;
        this.objectPool = objectPool;
    }

    public HSMConnection getConfig() {
        return config;
    }

    public GenericObjectPool<Connection> getObjectPool() {
        return objectPool;
    }
}

