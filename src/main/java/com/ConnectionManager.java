package com;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionManager {

    public boolean isConnected= false;
    AtomicInteger count = new AtomicInteger(0);
    private ConnectionOperationPoolManager poolManager;

    public ConnectionManager(ConnectionOperationPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    public ConnectionManager() {
    }

    public Connection createHSMConnection(HSMConnection connectionParameter) {
        String url = connectionParameter.getIp();
        int port = connectionParameter.getPort();
        int timeout = connectionParameter.getTimeout();
        return createConnection(url,port,timeout);
    }

    private Connection createConnection(String url, int port, int timeout) {
        Socket socket = null;
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(url,port),timeout);
            socket.setSoTimeout(timeout);
            return createConnection(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Connection createConnection(Socket socket) throws IOException {
        // creating connectionId over here for every connection request which arrived here
        Connection conn = new Connection(count.incrementAndGet());
        if(socket != null) {
            isConnected = true;
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            conn.setSocket(socket);
            conn.setOut(dos);
            conn.setIn(dis);
        }
        return conn;
    }

    public void closeConnection(Connection conn) {
        try{
            conn.getOut().close();
            conn.getIn().close();
            conn.getSocket().close();
            conn.setSocket(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
