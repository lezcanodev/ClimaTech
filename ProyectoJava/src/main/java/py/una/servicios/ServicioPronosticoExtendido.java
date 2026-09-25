package py.una.servicios;

import java.sql.SQLException;

import py.una.bd.ClimaTechDAO;
import py.una.entidad.PronosticoExtendido;

public class ServicioPronosticoExtendido {

    public PronosticoExtendido consultarPronostico(Double latitud, Double longitud, int dias) {
        try {
            ClimaTechDAO climaTechDAO = new ClimaTechDAO();
            return climaTechDAO.obtenerPronosticoExtendido(latitud, longitud, dias);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
