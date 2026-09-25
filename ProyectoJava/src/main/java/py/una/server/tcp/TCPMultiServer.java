package py.una.server.tcp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class TCPMultiServer {

    // variables compartidas
    boolean listening = true;
    public List<TCPServerHilo> hilosClientes;

    public void ejecutar(int puerto) throws IOException {
        ServerSocket serverSocket = null;

        if (hilosClientes == null) {
            hilosClientes = new ArrayList<TCPServerHilo>();
        }

        try {
            serverSocket = new ServerSocket(puerto);
        } catch (IOException e) {
            System.err.println("No se puede abrir el puerto: 5002.");
            System.exit(1);
        }
        System.out.println("Servidor ClimaTech TCP - Puerto abierto: 5002.");

        while (listening) {
            TCPServerHilo hilo = new TCPServerHilo(serverSocket.accept(), this);
            hilosClientes.add(hilo);
            hilo.start();
        }

        serverSocket.close();
    }
}
