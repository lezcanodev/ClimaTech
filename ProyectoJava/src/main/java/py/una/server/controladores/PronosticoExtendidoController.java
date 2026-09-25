package py.una.server.controladores;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import py.una.entidad.PronosticoDia;
import py.una.entidad.PronosticoExtendido;
import py.una.servicios.ServicioPronosticoExtendido;

public class PronosticoExtendidoController {

    public String manejarSolicitud(JSONObject solicitud) {
        try {
            JSONObject ubicacion = (JSONObject) solicitud.get("ubicacion");
            Double latitud = (Double) ubicacion.get("latitud");
            Double longitud = (Double) ubicacion.get("longitud");

            Number diasNum = (Number) solicitud.get("dias");
            if (diasNum == null || diasNum.intValue() < 1) {
                return construirError("La cantidad de días debe ser al menos 1");
            }
            int dias = diasNum.intValue();

            ServicioPronosticoExtendido servicio = new ServicioPronosticoExtendido();
            PronosticoExtendido resultado = servicio.consultarPronostico(latitud, longitud, dias);

            if (resultado == null) {
                return construirError("No se encontró pronóstico para la ubicación indicada");
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

    private String construirRespuestaJSON(PronosticoExtendido resultado) {
        JSONObject obj = new JSONObject();
        obj.put("ubicacion", resultado.getUbicacion());

        JSONArray pronosticoArray = new JSONArray();
        for (PronosticoDia dia : resultado.getPronostico()) {
            JSONObject item = new JSONObject();
            item.put("fecha", dia.getFecha());
            item.put("temp_maxima", dia.getTempMaxima());
            item.put("temp_minima", dia.getTempMinima());
            item.put("condicion", dia.getCondicion());
            item.put("probabilidad_lluvia", dia.getProbabilidadLluvia());
            pronosticoArray.add(item);
        }
        obj.put("pronostico", pronosticoArray);

        return obj.toJSONString();
    }
}
