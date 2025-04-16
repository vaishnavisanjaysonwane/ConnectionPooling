package com;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HSMConnection implements Serializable {

    private static final long serialVersionUID = 1573536425403280361L;

    private String ip;
    private int port;
    private int timeout;

    private boolean failoverEnable;

    private boolean failoverMode;

    //private HSMConnection firstFailover;

    //private HSMConnection secondFailover;

    private int maxConnections;

    private int minConnections;

    private String type;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("service")
    private String service;

    private int retryCount;

    private String tag;

    // Getters and Setters

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isFailoverEnable() {
        return failoverEnable;
    }

    public void setFailoverEnable(boolean failoverEnable) {
        this.failoverEnable = failoverEnable;
    }

    public boolean isFailoverMode() {
        return failoverMode;
    }

    public void setFailoverMode(boolean failoverMode) {
        this.failoverMode = failoverMode;
    }

//    public HSMConnection getFirstFailover() {
//        return firstFailover;
//    }
//
//    public void setFirstFailover(HSMConnection firstFailover) {
//        this.firstFailover = firstFailover;
//    }
//
//    public HSMConnection getSecondFailover() {
//        return secondFailover;
//    }
//
//    public void setSecondFailover(HSMConnection secondFailover) {
//        this.secondFailover = secondFailover;
//    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMinConnections() {
        return minConnections;
    }

    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

