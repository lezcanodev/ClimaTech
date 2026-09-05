package py.una.server;

import py.una.server.tcp.TCPMultiServer;
import py.una.server.udp.UDPServer;
import py.una.bd.ClimaTechSeed;

public class ClimaTech {

    public static void main(String[] args) {

        // Inicializamos los datos para las pruebas
        ClimaTechSeed.generarDatos();

        // Servicio TCP
        new Thread(() -> {
            try {
                TCPMultiServer tms = new TCPMultiServer();
                tms.ejecutar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // servicios UDP para consulta de clima
        new Thread(() -> {
            try {
                UDPServer udpServer = new UDPServer();
                udpServer.ejecutar(5001);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}