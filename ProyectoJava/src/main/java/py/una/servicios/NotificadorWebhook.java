package py.una.servicios;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NotificadorWebhook {

    private static final int MAX_INTENTOS = 3;
    private static final long BACKOFF_BASE_MS = 1000L;

    public boolean enviar(String urlCallback, String jsonNotificacion) {
        if (urlCallback != null && urlCallback.startsWith("tcp://")) {
            return enviarPorTcp(urlCallback, jsonNotificacion);
        }
        return enviarPorHttp(urlCallback, jsonNotificacion);
    }

    // HTTP: webhook típico (POST sobre TCP, capa aplicación HTTP)
    private boolean enviarPorHttp(String urlCallback, String jsonNotificacion) {
        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            if (intento > 1) {
                aplicarBackoff(intento);
            }
            try {
                URL url = new URL(urlCallback);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(5000);

                byte[] cuerpo = jsonNotificacion.getBytes(StandardCharsets.UTF_8);
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(cuerpo);
                }

                int codigo = conexion.getResponseCode();
                if (codigo >= 200 && codigo < 300) {
                    System.out.println("Webhook HTTP entregado (intento " + intento + ")");
                    return true;
                }
                System.out.println("Webhook HTTP intento " + intento + " respondió HTTP " + codigo);
            } catch (Exception e) {
                System.out.println("Webhook HTTP intento " + intento + " falló: " + e.getMessage());
            }
        }
        return false;
    }

    // TCP crudo: Socket cliente, envía una línea JSON y espera acuse (como en el
    // PDF)
    private boolean enviarPorTcp(String urlCallback, String jsonNotificacion) {
        String destino = urlCallback.substring("tcp://".length());
        String host;
        int puerto;
        int separador = destino.lastIndexOf(':');
        if (separador < 0) {
            return false;
        }
        host = destino.substring(0, separador);
        puerto = Integer.parseInt(destino.substring(separador + 1));

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            if (intento > 1) {
                aplicarBackoff(intento);
            }
            try {
                // connect: abre conexión TCP orientada a flujo hacia el callback
                Socket socket = new Socket(host, puerto);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(jsonNotificacion);

                String acuse = in.readLine();
                socket.close();

                if (acuse != null && acuse.contains("recibido")) {
                    System.out.println("Webhook TCP entregado (intento " + intento + ")");
                    return true;
                }
                System.out.println("Webhook TCP intento " + intento + " sin acuse válido");
            } catch (Exception e) {
                System.out.println("Webhook TCP intento " + intento + " falló: " + e.getMessage());
            }
        }
        return false;
    }

    private void aplicarBackoff(int intento) {
        long esperaMs = BACKOFF_BASE_MS * (intento - 1);
        System.out.println("Webhook: esperando " + esperaMs + " ms antes del intento " + intento);
        try {
            Thread.sleep(esperaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
