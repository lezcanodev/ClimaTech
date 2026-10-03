package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

// Para probar pronostico extendido
public class TCPClient {

    public static void main(String[] args) throws Exception {

        String direccionServidor = "127.0.0.1";
        if (args.length > 0) {
            direccionServidor = args[0];
        }

        int puertoServidor = 5002;

        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Intentando conectar a = " + direccionServidor + ":" + puertoServidor + " via TCP...");

            Socket socket = new Socket(direccionServidor, puertoServidor);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print("Ingrese la latitud: ");
            Double latitud = Double.parseDouble(inFromUser.readLine());

            System.out.print("Ingrese la longitud: ");
            Double longitud = Double.parseDouble(inFromUser.readLine());

            System.out.print("Ingrese cantidad de días: ");
            int dias = Integer.parseInt(inFromUser.readLine());

            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", latitud);
            ubicacion.put("longitud", longitud);

            JSONObject solicitud = new JSONObject();
            solicitud.put("servicio", "pronostico_extendido");
            solicitud.put("ubicacion", ubicacion);
            solicitud.put("dias", dias);

            String jsonSolicitud = solicitud.toJSONString();
            System.out.println("Enviar " + jsonSolicitud + " al servidor.");
            out.println(jsonSolicitud);

            String respuesta = in.readLine();
            System.out.println("Pronóstico: " + respuesta);

            out.close();
            in.close();
            socket.close();
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
