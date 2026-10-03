package py.una.server.controladores;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import py.una.bd.ClimaTechDAO;
import py.una.servicios.MotorEventosAlertas;

public class ReceptorMedicionesController {

    public String manejarMedicion(String jsonRecibido) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject medicion = (JSONObject) parser.parse(jsonRecibido);

            String idEstacion = (String) medicion.get("id_estacion");
            String condicion = (String) medicion.get("condicion");
            Number probNum = (Number) medicion.get("probabilidad_lluvia");
            Number tempNum = (Number) medicion.get("temperatura");
            Number vientoNum = (Number) medicion.get("viento_kmh");

            if (idEstacion == null || condicion == null || probNum == null) {
                return construirError("Medición incompleta");
            }

            int probabilidadLluvia = probNum.intValue();
            Double temperatura = tempNum != null ? tempNum.doubleValue() : 25.0;
            Integer vientoKmh = vientoNum != null ? vientoNum.intValue() : null;

            ClimaTechDAO dao = new ClimaTechDAO();
            dao.guardarMedicion(idEstacion, temperatura, condicion, probabilidadLluvia);

            MotorEventosAlertas motor = new MotorEventosAlertas();
            motor.procesarMedicion(idEstacion, condicion, probabilidadLluvia, vientoKmh);

            JSONObject ok = new JSONObject();
            ok.put("estado", "procesada");
            return ok.toJSONString();
        } catch (ParseException e) {
            return construirError("JSON de medición inválido");
        } catch (Exception e) {
            e.printStackTrace();
            return construirError("No se pudo procesar la medición");
        }
    }

    private String construirError(String mensaje) {
        JSONObject error = new JSONObject();
        error.put("error", mensaje);
        return error.toJSONString();
    }
}
