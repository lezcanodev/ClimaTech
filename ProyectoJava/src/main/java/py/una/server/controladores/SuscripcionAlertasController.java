package py.una.server.controladores;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import py.una.entidad.SuscripcionAlerta;
import py.una.servicios.ServicioSuscripcionAlertas;

public class SuscripcionAlertasController {

    public String manejarSolicitud(JSONObject solicitud) {
        try {
            String sistemaSuscriptor = (String) solicitud.get("sistema_suscriptor");
            JSONObject ubicacion = (JSONObject) solicitud.get("ubicacion");
            Double latitud = (Double) ubicacion.get("latitud");
            Double longitud = (Double) ubicacion.get("longitud");
            Number radioNum = (Number) ubicacion.get("radio_km");
            JSONArray tiposAlerta = (JSONArray) solicitud.get("tipos_alerta");
            String urlCallback = (String) solicitud.get("url_callback");

            if (sistemaSuscriptor == null || urlCallback == null || tiposAlerta == null || tiposAlerta.isEmpty()) {
                return construirError("Faltan datos obligatorios de la suscripción");
            }
            if (radioNum == null || radioNum.intValue() < 1) {
                return construirError("El radio en km debe ser al menos 1");
            }

            ServicioSuscripcionAlertas servicio = new ServicioSuscripcionAlertas();
            SuscripcionAlerta resultado = servicio.registrar(sistemaSuscriptor, latitud, longitud,
                    radioNum.intValue(), tiposAlerta, urlCallback);

            if (resultado == null) {
                return construirError("No se pudo registrar la suscripción");
            }

            return construirRespuestaJSON(resultado);
        } catch (Exception e) {
            return construirError("No se pudo procesar la solicitud");
        }
    }

    private String construirError(String mensaje) {
        JSONObject error = new JSONObject();
        error.put("error", mensaje);
        return error.toJSONString();
    }

    private String construirRespuestaJSON(SuscripcionAlerta resultado) {
        JSONObject obj = new JSONObject();
        obj.put("id_suscripcion", resultado.getIdSuscripcion());
        obj.put("estado", resultado.getEstado());
        obj.put("fecha_registro", resultado.getFechaRegistro());
        return obj.toJSONString();
    }
}
