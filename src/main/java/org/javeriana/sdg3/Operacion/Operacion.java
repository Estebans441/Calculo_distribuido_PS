package org.javeriana.sdg3.Operacion;


import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.Math.pow;

public class Operacion {
    private static String[] opBrokerIp = {"tcp://localhost:5559", "tcp://localhost:5550"};

    public static void main(String[] args){
        try (ZContext context = new ZContext()) {
            // Obtener el número de servidor de operación
            int serverNum = Integer.parseInt(args[0]);

            //  Socket to talk to server
            Socket responder = context.createSocket(SocketType.REP);
            responder.connect(opBrokerIp[serverNum]);

            System.out.println("----------------------------------------------");
            System.out.println("Servidor de operación " + (serverNum + 1) + " en ejecución");
            System.out.println("----------------------------------------------");

            while (!Thread.currentThread().isInterrupted()) {
                // Recibir la matriz y la parte de la operacion a realizar
                byte[] bytes = responder.recv(0);

                // Conversion a float[][]
                int size = bytes.length / (2 * Float.BYTES);
                float[][] data = new float[size][2];
                for (int i = 0; i < size; i++) {
                    // Obtiene los bytes correspondientes a la fila actual
                    byte[] rowBytes = Arrays.copyOfRange(bytes, i * 2 * Float.BYTES, (i + 1) * 2 * Float.BYTES);

                    // Convierte los bytes a valores float y los almacena en la fila actual del arreglo data
                    ByteBuffer buffer = ByteBuffer.wrap(rowBytes);
                    data[i][0] = buffer.getFloat();
                    data[i][1] = buffer.getFloat();
                }

                // Calcular la parte correspondiente de la operacion
                float res;
                if (serverNum == 0) res = op1(data);
                else res = op2(data);

                System.out.println("        - Respuesta de una parte: " + String.valueOf(res));
                System.out.println();

                // Envia la respuesta
                responder.send(String.valueOf(res));
            }

            responder.close();
        }
    }

    //Parte 1 de la operacion de varianza en la que se calcula el valor esperado de x al cuadrado
    public static float op1(float[][] data) {
        float res = 0;
        for (int i = 0; i < data.length; i++) {
            res += pow(data[i][0], 2) * data[i][1];
        }
        return res;
    }

    //Parte 2 de la operacion de varianza en la que se calcula el valor esperado al cuadrado
    public static float op2(float[][] data) {
        float res = 0;
        for (int i = 0; i < data.length; i++)
            res += data[i][0] * data[i][1];
        res = (float) (pow(res, 2) * -1);
        return res;
    }
}

