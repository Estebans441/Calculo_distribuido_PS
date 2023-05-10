package org.javeriana.sdg3.Central;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Central {
    private static String brokerIp = "tcp://localhost:5556";
    private static String[] opBrokersIp = {"tcp://localhost:5557", "tcp://localhost:5558"};

    public static void main(String[] args) {
        // Crear un servidor socket en el puerto 12345
        System.out.println("----------------------------------------------");
        System.out.println("Servidor central de cálculo en ejecución");
        System.out.println("----------------------------------------------");

        try (ZContext context = new ZContext()) {
            //  Socket to talk to server
            Socket responder = context.createSocket(SocketType.REP);
            responder.connect(brokerIp);
            Socket requester1 = context.createSocket(SocketType.REQ);
            requester1.connect(opBrokersIp[0]);
            Socket requester2 = context.createSocket(SocketType.REQ);
            requester2.connect(opBrokersIp[1]);

            while (!Thread.currentThread().isInterrupted()) {
                //  Recibe solicitud
                byte[] bytes = responder.recv(0);

                // Enviar la matriz a los servidores de operación

                // Manda la solicitud al primer servidor
                requester1.send(bytes,0);

                // Manda la solicitud al segundo servidor
                requester2.send(bytes,0);

                // Recibir las respuestas de los servidores de operación
                float var = Float.parseFloat(requester1.recvStr()) + Float.parseFloat(requester2.recvStr());
                System.out.println("        - Respuesta: " + String.valueOf(var));
                System.out.println();

                // Enviar la respuesta al cliente
                responder.send(String.valueOf(var));
            }

            responder.close();
            requester1.close();
            requester2.close();
        }
    }
}

