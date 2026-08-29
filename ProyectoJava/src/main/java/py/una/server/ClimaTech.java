package py.una.server;

import py.una.server.tcp.TCPMultiServer;
import py.una.server.udp.UDPServer;

public class ClimaTech {

    public static void main(String[] args) {
        
        // Servicio TCP
        new Thread(() -> {
            try {
                TCPMultiServer tms = new TCPMultiServer();
                tms.ejecutar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // servicios udp
        new Thread(() -> {
            try {
                UDPServer udpServer = new UDPServer();
                udpServer.ejecutar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}