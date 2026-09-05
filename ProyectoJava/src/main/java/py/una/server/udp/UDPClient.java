package py.una.server.udp;

import java.io.*;
import java.net.*;

import org.json.simple.JSONObject;

class UDPClient {

    public static void main(String a[]) throws Exception {

        // Datos necesario
        String direccionServidor = "127.0.0.1";

        if (a.length > 0) {
            direccionServidor = a[0];
        }

        int puertoServidor = 5001;

        try {

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            DatagramSocket clientSocket = new DatagramSocket();

            InetAddress IPAddress = InetAddress.getByName(direccionServidor);
            System.out.println("Intentando conectar a = " + IPAddress + ":" + puertoServidor + " via UDP...");

            byte[] sendData;
            byte[] receiveData = new byte[1024];

            System.out.print("Ingrese la latitud: ");
            Double latitud = Double.parseDouble(inFromUser.readLine());

            System.out.print("Ingrese la longitud: ");
            Double longitud = Double.parseDouble(inFromUser.readLine());

            // Armamos la solicitud según el contrato del servicio "clima_actual"
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", latitud);
            ubicacion.put("longitud", longitud);

            JSONObject solicitud = new JSONObject();
            solicitud.put("servicio", "clima_actual");
            solicitud.put("ubicacion", ubicacion);
            solicitud.put("unidad_temperatura", "celsius");

            String datoPaquete = solicitud.toJSONString();
            sendData = datoPaquete.getBytes();

            System.out.println("Enviar " + datoPaquete + " al servidor. (" + sendData.length + " bytes)");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);

            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            System.out.println("Esperamos si viene la respuesta.");

            // Vamos a hacer una llamada BLOQUEANTE entonces establecemos un timeout maximo
            // de espera
            clientSocket.setSoTimeout(10000);

            try {
                // ESPERAMOS LA RESPUESTA, BLOQUENTE
                clientSocket.receive(receivePacket);

                String respuesta = new String(receivePacket.getData(), 0, receivePacket.getLength());

                InetAddress returnIPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                System.out.println("Respuesta desde =  " + returnIPAddress + ":" + port);
                System.out.println("Clima: " + respuesta);

            } catch (SocketTimeoutException ste) {

                System.out.println("TimeOut: El paquete udp se asume perdido.");
            }
            clientSocket.close();
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}