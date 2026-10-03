package py.una.servicios;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import py.una.bd.ClimaTechDAO;
import py.una.entidad.SuscripcionAlerta;

public class MotorEventosAlertas {

    private final NotificadorWebhook notificador = new NotificadorWebhook();
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public void procesarMedicion(String idEstacion, String condicion, int probabilidadLluvia, Integer vientoKmh) {
        try {
            ClimaTechDAO dao = new ClimaTechDAO();
            double[] coords = dao.obtenerCoordenadasEstacion(idEstacion);
            if (coords == null) {
                System.out.println("Estación desconocida: " + idEstacion);
                return;
            }

            List<String> alertas = detectarAlertas(condicion, probabilidadLluvia, vientoKmh);
            if (alertas.isEmpty()) {
                System.out.println("Medición sin alertas adversas.");
                return;
            }

            List<SuscripcionAlerta> suscripciones = dao.listarSuscripcionesActivas();
            for (SuscripcionAlerta suscripcion : suscripciones) {
                double distancia = distanciaKm(suscripcion.getLatitud(), suscripcion.getLongitud(), coords[0],
                        coords[1]);
                if (distancia > suscripcion.getRadioKm()) {
                    continue;
                }

                List<String> tiposSuscritos = parseTiposAlerta(suscripcion.getTiposAlertaJson());
                for (String tipoAlerta : alertas) {
                    if (!tiposSuscritos.contains(tipoAlerta)) {
                        continue;
                    }
                    String json = construirNotificacion(suscripcion.getIdSuscripcion(), tipoAlerta, condicion);
                    notificador.enviar(suscripcion.getUrlCallback(), json);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> detectarAlertas(String condicion, int probabilidadLluvia, Integer vientoKmh) {
        List<String> alertas = new ArrayList<>();
        String condicionLower = condicion != null ? condicion.toLowerCase() : "";

        if (condicionLower.contains("tormenta") || probabilidadLluvia >= 70) {
            alertas.add("tormenta");
        }
        if (vientoKmh != null && vientoKmh >= 50) {
            alertas.add("viento_fuerte");
        }
        if (condicionLower.contains("granizo")) {
            alertas.add("granizo");
        }
        return alertas;
    }

    private List<String> parseTiposAlerta(String tiposJson) {
        List<String> tipos = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(tiposJson);
            for (Object item : array) {
                tipos.add((String) item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tipos;
    }

    private String construirNotificacion(String idSuscripcion, String tipoAlerta, String condicion) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(2);

        JSONObject notificacion = new JSONObject();
        notificacion.put("id_suscripcion", idSuscripcion);
        notificacion.put("tipo_alerta", tipoAlerta);
        notificacion.put("severidad", "alta");
        notificacion.put("descripcion", descripcionPara(tipoAlerta, condicion));
        notificacion.put("fecha_hora_inicio", formatoFecha.format(inicio));
        notificacion.put("fecha_hora_fin_estimada", formatoFecha.format(fin));
        return notificacion.toJSONString();
    }

    private String descripcionPara(String tipoAlerta, String condicion) {
        if ("viento_fuerte".equals(tipoAlerta)) {
            return "Vientos fuertes en la zona";
        }
        if ("granizo".equals(tipoAlerta)) {
            return "Posible caída de granizo";
        }
        return condicion != null ? condicion : "Tormenta en la zona";
    }

    private double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c;
    }
}
