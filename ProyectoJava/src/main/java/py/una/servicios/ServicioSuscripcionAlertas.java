package py.una.servicios;

import java.sql.SQLException;

import org.json.simple.JSONArray;

import py.una.bd.ClimaTechDAO;
import py.una.entidad.SuscripcionAlerta;

public class ServicioSuscripcionAlertas {

    public SuscripcionAlerta registrar(String sistemaSuscriptor, Double latitud, Double longitud, int radioKm,
            JSONArray tiposAlerta, String urlCallback) {
        try {
            ClimaTechDAO dao = new ClimaTechDAO();
            return dao.registrarSuscripcion(sistemaSuscriptor, latitud, longitud, radioKm,
                    tiposAlerta.toJSONString(), urlCallback);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
