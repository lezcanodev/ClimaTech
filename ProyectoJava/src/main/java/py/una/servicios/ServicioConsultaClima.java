package py.una.servicios;

import java.sql.SQLException;

import py.una.bd.ClimaTechDAO;
import py.una.entidad.Medicion;

public class ServicioConsultaClima {

    public Medicion consultarClima(Double latitud, Double longitud) {
        try {
            ClimaTechDAO climaTechDAO = new ClimaTechDAO();
            return climaTechDAO.obtenerUltimaMedicion(latitud, longitud);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}