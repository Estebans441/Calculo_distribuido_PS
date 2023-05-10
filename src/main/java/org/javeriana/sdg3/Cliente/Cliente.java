package org.javeriana.sdg3.Cliente;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class Cliente {
    private static String centralServerIp = "tcp://localhost:5555";

    public static void main(String[] args){
        try (ZContext context = new ZContext()) {
            //  Socket to talk to server
            Socket requester = context.createSocket(SocketType.REQ);
            requester.connect(centralServerIp);

            System.out.println("-----------------------------------------------------------------------");
            System.out.println("Conexión establecida con el servidor central de cálculo");
            System.out.println("-----------------------------------------------------------------------");

            // Pedir al usuario que introduzca los datos
            Scanner scanner = new Scanner(System.in);

            System.out.print("Introduce el tamaño del rango del problema: ");
            int size = scanner.nextInt();
            float[][] data = new float[size][2];
            for (int i = 0; i < size; i++) {
                System.out.print("Variable aleatoria " + (i + 1) + ": ");
                data[i][0] = scanner.nextFloat();
                System.out.print("f(x): ");
                data[i][1] = scanner.nextFloat();
            }

            // Conversion de float[][] a byte[]
            int dataSize = size * 2 * Float.BYTES;
            byte[] bytes = new byte[dataSize];
            for (int i = 0; i < size; i++) {
                // Obtiene los valores float de la fila actual
                float[] row = data[i];
                // Convierte los valores float a bytes y los agrega al arreglo bytes
                ByteBuffer buffer = ByteBuffer.allocate(2 * Float.BYTES);
                buffer.putFloat(row[0]);
                buffer.putFloat(row[1]);
                byte[] rowBytes = buffer.array();
                System.arraycopy(rowBytes, 0, bytes, i * 2 * Float.BYTES, 2 * Float.BYTES);
            }
            // envia los datos
            requester.send(bytes, 0);


            // Recibe la respuesta
            String var = requester.recvStr();
            // Mostrar la respuesta al usuario
            System.out.println("La varianza es: " + var);

            requester.close();
        }
    }
}
