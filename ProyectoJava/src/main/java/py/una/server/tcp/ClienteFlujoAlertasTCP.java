package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * para probar servicio de webhooks
 * Crea un servidor tcp dinamico que sera el receptor del webhook y luego
 * se subscribe al servicio de notificaciones ofrecido
 */
public class ClienteFlujoAlertasTCP {

    private static final String HOST_CLIMA = "127.0.0.1";
    private static final int PUERTO_CLIMA_TCP = 5002;
    // Límite de radio_km en PostgreSQL (columna SMALLINT)
    private static final int RADIO_KM_MIN = 1;
    private static final int RADIO_KM_MAX = 32767;

    public static void main(String[] args) throws Exception {
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("=== Prueba manual: alertas (suscripción + callback TCP) ===");

        System.out.print("Puerto local para recibir notificaciones (ej. 9100): ");
        int puertoCallback = Integer.parseInt(teclado.readLine());

        // ServerSocket: socket pasivo en escucha (rol servidor del subscriptor)
        iniciarReceptorNotificaciones(puertoCallback);

        System.out.print("Latitud (ej. -25.2637): ");
        double latitud = Double.parseDouble(teclado.readLine());
        System.out.print("Longitud (ej. -57.5759): ");
        double longitud = Double.parseDouble(teclado.readLine());
        System.out.print("Radio km (ej. 10, entero; columna SMALLINT 1-32767): ");
        int radioKm = Integer.parseInt(teclado.readLine().trim());
        if (radioKm < RADIO_KM_MIN || radioKm > RADIO_KM_MAX) {
            System.err.println("Radio inválido: debe ser un entero entre " + RADIO_KM_MIN + " y " + RADIO_KM_MAX);
            return;
        }

        String urlCallback = "tcp://" + HOST_CLIMA + ":" + puertoCallback;
        registrarSuscripcionEnClimaTech(latitud, longitud, radioKm, urlCallback);

        System.out.println();
        System.out.println("Suscripción lista. Callback: " + urlCallback);
        System.out.println("Dejá corriendo ClimaTech (simulador UDP cada 5s). Las alertas aparecerán aquí.");
        System.out.println("Pulsa Enter para salir.");
        teclado.readLine();
    }

    // Hilo aparte: accept() bloqueante por cada notificación que envía ClimaTech
    private static void iniciarReceptorNotificaciones(int puerto) {
        Thread hiloReceptor = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(puerto)) {
                System.out.println("Receptor callback TCP escuchando en puerto " + puerto);

                while (true) {
                    // accept: entrega un Socket conectado cuando ClimaTech abre la conexión
                    Socket conexionEntrante = serverSocket.accept();
                    atenderNotificacion(conexionEntrante);
                }
            } catch (IOException e) {
                System.err.println("Receptor callback detenido: " + e.getMessage());
            }
        });
        hiloReceptor.setDaemon(true);
        hiloReceptor.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void atenderNotificacion(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String jsonAlerta = in.readLine();
            System.out.println();
            System.out.println(">>> ALERTA RECIBIDA <<<");
            System.out.println(jsonAlerta);
            System.out.println(">>> FIN ALERTA <<<");
            System.out.println();

            // Acuse al notificador de ClimaTech (cierra el intercambio de este evento)
            out.println("{\"recibido\":true}");

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error atendiendo notificación: " + e.getMessage());
        }
    }

    private static void registrarSuscripcionEnClimaTech(double latitud, double longitud, int radioKm,
            String urlCallback) throws IOException {
        // Socket cliente hacia el servicio TCP de ClimaTech (puerto 5002)
        Socket socket = new Socket(HOST_CLIMA, PUERTO_CLIMA_TCP);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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

        out.println(solicitud.toJSONString());

        String confirmacion = in.readLine();
        System.out.println("Respuesta ClimaTech (suscripción): " + confirmacion);

        in.close();
        out.close();
        socket.close();
    }
}
