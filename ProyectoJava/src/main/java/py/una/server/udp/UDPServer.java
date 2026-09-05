package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import py.una.server.controladores.ConsultaClimaController;

public class UDPServer {

    public void ejecutar(int puertoServidor) {
        DatagramSocket serverSocket = null;
        try {
            // 1) Creamos el socket Servidor de Datagramas (UDP)
            serverSocket = new DatagramSocket(puertoServidor);
            System.out.println("Servidor Sistemas Distribuidos - UDP ");

            // 2) buffer de datos a enviar y recibir
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            // 3) Servidor siempre esperando
            while (true) {

                receiveData = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                System.out.println("Esperando a algun cliente... ");

                // 4) Receive LLAMADA BLOQUEANTE
                serverSocket.receive(receivePacket);

                System.out.println("________________________________________________");
                System.out.println("Aceptamos un paquete");

                // Datos recibidos e Identificamos quien nos envio
                String datoRecibido = new String(receivePacket.getData());
                datoRecibido = datoRecibido.trim();
                // Ip
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                System.out.println("De : " + IPAddress + ":" + port);
                System.out.println("Consulta Recibida : " + datoRecibido);
                System.out.println("DatoRecibido: ");

                // Procesamos la solicitud
                ConsultaClimaController controller = new ConsultaClimaController();
                String jsonRespuesta = controller.manejarSolicitud(datoRecibido);

                // Enviamos la respuesta inmediatamente a ese mismo cliente
                // Es no bloqueante
                sendData = jsonRespuesta.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

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
