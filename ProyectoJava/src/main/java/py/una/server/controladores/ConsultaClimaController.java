package py.una.server.controladores;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import py.una.entidad.Medicion;
import py.una.servicios.ServicioConsultaClima;

public class ConsultaClimaController {

    // Recibe el JSON crudo, delega al Service, devuelve el JSON de respuesta
    public String manejarSolicitud(String jsonRecibido) {
        try {
            // 1. Parseamos la solicitud json
            JSONParser parser = new JSONParser();
            JSONObject solicitud = (JSONObject) parser.parse(jsonRecibido);

            // 2. obtenemos los datos
            JSONObject ubicacion = (JSONObject) solicitud.get("ubicacion");
            Double latitud = (Double) ubicacion.get("latitud");
            Double longitud = (Double) ubicacion.get("longitud");

            // 3. Consultamos al servicio
            ServicioConsultaClima servicio = new ServicioConsultaClima();
            Medicion medicion = servicio.consultarClima(latitud, longitud);

            return this.construirRespuestaJSON(medicion);
        } catch (ParseException e) {
            return construirError("JSON de solicitud inválido");
        } catch (Exception e) {
            return construirError("No se pudo procesar la solicitud");
        }
    }

    private String construirError(String mensaje) {
        JSONObject error = new JSONObject();
        error.put("error", mensaje);
        return error.toJSONString();
    }

    private String construirRespuestaJSON(Medicion medicion) {
        JSONObject obj = new JSONObject();
        obj.put("ubicacion", medicion.getUbicacion());
        obj.put("fecha_hora_medicion", medicion.getFechaHoraMedicion());
        obj.put("temperatura", medicion.getTemperatura());
        obj.put("sensacion_termica", medicion.getSensacionTermica());
        obj.put("condicion", medicion.getCondicion());
        obj.put("probabilidad_lluvia", medicion.getProbabilidadLluvia());
        return obj.toJSONString();
    }
}