package py.una.server;

import py.una.server.tcp.TCPMultiServer;
import py.una.server.udp.ReceptorMedicionesUDP;
import py.una.server.udp.SimuladorEstacionUDP;
import py.una.server.udp.UDPServer;
import py.una.bd.ClimaTechSeed;

public class ClimaTech {

    public static void main(String[] args) {

        // Inicializamos los datos para las pruebas
        // generacion aleatoria de datos por cada inicio
        ClimaTechSeed.generarDatos();

        // Servicio TCP
        new Thread(() -> {
            try {
                TCPMultiServer tms = new TCPMultiServer();
                tms.ejecutar(5002);
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

        // Receptor UDP de mediciones (motor de alertas)
        new Thread(() -> {
            try {
                ReceptorMedicionesUDP receptor = new ReceptorMedicionesUDP();
                receptor.ejecutar(5003);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Estación simulada: emite mediciones al receptor cada 5 segundos
        new Thread(() -> {
            SimuladorEstacionUDP simulador = new SimuladorEstacionUDP();
            simulador.ejecutarSimulacion("127.0.0.1", 5003, 5000);
        }).start();

    }
}