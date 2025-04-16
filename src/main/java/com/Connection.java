package com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class Connection {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String connectionId;
    private int retryCount;

    public Connection(long count) {
        connectionId = UUID.randomUUID().toString()+ "##" +count;
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed() || socket.isConnected();
    }

    public Socket getSocket() {return socket;}

    public void setSocket(Socket socket) {this.socket = socket;}

    public DataOutputStream getOut() {return out;}
    public void setOut(DataOutputStream out) {this.out = out;}

    public DataInputStream getIn() {return in;}
    public void setIn(DataInputStream in) {this.in = in;}

    public String getConnectionId() {return connectionId;}

    public int getRetryCount() {return retryCount;}
    public void setRetryCount(int retryCount) {this.retryCount = retryCount;}

    @Override
    public boolean equals(Object o) {
        if(o instanceof Connection) {
            return this.connectionId.equals(((Connection)o).getConnectionId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(connectionId);
    }
}
