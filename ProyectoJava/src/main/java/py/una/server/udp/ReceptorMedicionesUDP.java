package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import py.una.server.controladores.ReceptorMedicionesController;

public class ReceptorMedicionesUDP {

    public void ejecutar(int puertoServidor) {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(puertoServidor);
            System.out.println("Receptor de mediciones climáticas - UDP puerto " + puertoServidor);

            byte[] receiveData = new byte[2048];

            while (true) {
                receiveData = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String datoRecibido = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress ipCliente = receivePacket.getAddress();
                int puertoCliente = receivePacket.getPort();

                System.out.println("Medición UDP de " + ipCliente + ":" + puertoCliente + " -> " + datoRecibido);

                ReceptorMedicionesController controller = new ReceptorMedicionesController();
                String jsonRespuesta = controller.manejarMedicion(datoRecibido);

                byte[] sendData = jsonRespuesta.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipCliente, puertoCliente);
                serverSocket.send(sendPacket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}
