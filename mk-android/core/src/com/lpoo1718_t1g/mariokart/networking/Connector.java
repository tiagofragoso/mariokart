package com.lpoo1718_t1g.mariokart.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connector {

    private static Connector ourInstance = new Connector();

    private Socket socket;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;


    private Connector() {}

    public static Connector getInstance(){
        return ourInstance;
    }

    public Socket connect(String addr, int port) {
        final String cAddress = addr;
        final int cPort = port;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(cAddress, cPort);
                    socket.setTcpNoDelay(true);
                    ostream = new ObjectOutputStream(socket.getOutputStream());
                    startReceiver();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return socket;
    }

    private void startReceiver(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    istream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message input;
                try {
                    while ((input = (Message) istream.readObject()) != null){
                    }
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void write(Message o){
        final Message obj = o;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (ostream != null) {
                    try {
                        ostream.writeObject(obj);
                        ostream.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
