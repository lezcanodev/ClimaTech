package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SuscripcionAlertasTCPClient {

    public static void main(String[] args) throws Exception {

        String direccionServidor = "127.0.0.1";
        if (args.length > 0) {
            direccionServidor = args[0];
        }
        int puertoServidor = 5002;

        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Conectando a " + direccionServidor + ":" + puertoServidor + " (suscripción alertas)...");

            Socket socket = new Socket(direccionServidor, puertoServidor);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print("Latitud: ");
            Double latitud = Double.parseDouble(inFromUser.readLine());
            System.out.print("Longitud: ");
            Double longitud = Double.parseDouble(inFromUser.readLine());
            System.out.print("Radio (km): ");
            int radioKm = Integer.parseInt(inFromUser.readLine());
            System.out.print("URL callback (ej. http://127.0.0.1:9090/webhook): ");
            String urlCallback = inFromUser.readLine();

            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", latitud);
            ubicacion.put("longitud", longitud);
            ubicacion.put("radio_km", radioKm);

            JSONArray tiposAlerta = new JSONArray();
            tiposAlerta.add("tormenta");
            tiposAlerta.add("viento_fuerte");
            tiposAlerta.add("granizo");

            JSONObject solicitud = new JSONObject();
            solicitud.put("servicio", "suscripcion_alertas");
            solicitud.put("sistema_suscriptor", "AlquilerCanchas-App");
            solicitud.put("ubicacion", ubicacion);
            solicitud.put("tipos_alerta", tiposAlerta);
            solicitud.put("url_callback", urlCallback);

            String jsonSolicitud = solicitud.toJSONString();
            System.out.println("Enviando: " + jsonSolicitud);
            out.println(jsonSolicitud);

            String respuesta = in.readLine();
            System.out.println("Confirmación: " + respuesta);

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
