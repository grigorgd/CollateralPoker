package server;

import java.net.InetAddress;

public class ServerClient {

    private String name;
    private InetAddress address;
    private int port;
    private int attempt = 0;

    public ServerClient(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() { return name; }
    public InetAddress getAddress() { return address; }
    public int getPort() { return port; }
    public int getAttempt() { return attempt; }

    public void clearAttempt(){
        this.attempt = 0;
    }

    public void increaseAttempt(){
        this.attempt++;
    }

}
