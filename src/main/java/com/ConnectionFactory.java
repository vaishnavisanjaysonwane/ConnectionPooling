package com;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionFactory extends BasePooledObjectFactory<Connection> {

    public ConnectionFactory(HSMConnection hsmConnectionConfig) {
        this.hsmConnectionConfig = hsmConnectionConfig;
    }

    HSMConnection hsmConnectionConfig;

    @Override
    public Connection create() throws Exception {
        Connection connection = new ConnectionManager().createHSMConnection(hsmConnectionConfig);
        if(!validateConnection(connection)){ throw new Exception();}
        return connection;
    }

    private boolean validateConnection(Connection connection) {
        System.out.println("Validating connection...by sending Hello Command to HSM using HSMCommandExecutor");
        //new HSMCommandExecutor().checkCommand(connection, echoCommand);
        if(true){return true;}
        return false;
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return null;
    }

    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        //p.getObject().closeConnection();
    }

    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        //return !p.getObject().getTcpSocket().isClosed();
        return false;
    }
}
