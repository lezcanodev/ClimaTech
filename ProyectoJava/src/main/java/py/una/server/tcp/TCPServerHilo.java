package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import py.una.server.controladores.PronosticoExtendidoController;

public class TCPServerHilo extends Thread {

    private Socket socket = null;

    TCPMultiServer servidor;

    public TCPServerHilo(Socket socket, TCPMultiServer servidor) {
        super("TCPServerHilo");
        this.socket = socket;
        this.servidor = servidor;
    }

    public void run() {

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String jsonRecibido = in.readLine();
            if (jsonRecibido != null) {
                String jsonRespuesta = procesarServicio(jsonRecibido);
                out.println(jsonRespuesta);
            }

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String procesarServicio(String jsonRecibido) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject solicitud = (JSONObject) parser.parse(jsonRecibido);
            String servicio = (String) solicitud.get("servicio");

            if ("pronostico_extendido".equals(servicio)) {
                PronosticoExtendidoController controller = new PronosticoExtendidoController();
                return controller.manejarSolicitud(solicitud);
            }

            JSONObject error = new JSONObject();
            error.put("error", "Servicio no reconocido");
            return error.toJSONString();
        } catch (ParseException e) {
            JSONObject error = new JSONObject();
            error.put("error", "JSON de solicitud inválido");
            return error.toJSONString();
        }
    }
}
