package org.javeriana.sdg3.Central;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class CentralBroker {
    public static void main(String[] args)
    {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            //  Socket facing clients
            Socket cliente = context.createSocket(SocketType.ROUTER);
            cliente.bind("tcp://*:5555");

            //  Socket facing services
            Socket central = context.createSocket(SocketType.DEALER);
            central.bind("tcp://*:5556");

            //  Start the proxy
            ZMQ.proxy(cliente, central, null);
        }
    }
}
