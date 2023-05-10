package org.javeriana.sdg3.Operacion;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Op1Broker {
    public static void main(String[] args)
    {
        //  Prepare our context and sockets
        try (ZContext context = new ZContext()) {
            //  Socket facing clients
            ZMQ.Socket central = context.createSocket(SocketType.ROUTER);
            central.bind("tcp://*:5557");

            //  Socket facing services
            ZMQ.Socket op = context.createSocket(SocketType.DEALER);
            op.bind("tcp://*:5559");

            //  Start the proxy
            ZMQ.proxy(central, op, null);
        }
    }
}
