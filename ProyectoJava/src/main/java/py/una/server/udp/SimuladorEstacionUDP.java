package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.json.simple.JSONObject;

public class SimuladorEstacionUDP {

    public void ejecutarSimulacion(String host, int puertoReceptor, long intervaloMs) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress ip = InetAddress.getByName(host);
            clientSocket.setSoTimeout(10000);

            while (true) {
                emitirMedicion(clientSocket, ip, puertoReceptor);
                Thread.sleep(intervaloMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void emitirMedicion(DatagramSocket clientSocket, InetAddress ip, int puertoReceptor) throws Exception {
        JSONObject medicion = new JSONObject();
        medicion.put("id_estacion", "EST-001");
        medicion.put("temperatura", 26.5);
        medicion.put("condicion", "Tormentas aisladas");
        medicion.put("probabilidad_lluvia", 80);
        medicion.put("viento_kmh", 62);

        String json = medicion.toJSONString();
        byte[] sendData = json.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, puertoReceptor);

        System.out.println("Estación simulada -> receptor: " + json);
        clientSocket.send(sendPacket);

        byte[] receiveData = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.receive(receivePacket);
            String respuesta = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Respuesta del receptor: " + respuesta);
        } catch (SocketTimeoutException e) {
            System.out.println("Sin respuesta del receptor (timeout).");
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        if (args.length > 0) {
            host = args[0];
        }
        SimuladorEstacionUDP simulador = new SimuladorEstacionUDP();
        simulador.ejecutarSimulacion(host, 5003, 5000);
    }
}
