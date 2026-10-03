package py.una.server.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPMultiServer {

    // variables compartidas
    boolean listening = true;

    public void ejecutar(int puerto) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(puerto);
        } catch (IOException e) {
            System.err.println("No se puede abrir el puerto: 5002.");
            System.exit(1);
        }
        System.out.println("Servidor ClimaTech TCP - Puerto abierto: 5002.");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (listening) {
            pool.submit(new TCPServerHilo(serverSocket.accept(), this));
        }

        serverSocket.close();
    }
}
