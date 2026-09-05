package py.una.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import py.una.entidad.Medicion;

public class ClimaTechDAO {

    // Busca la estación más cercana a la latitud/longitud recibida y trae su última
    // medición
    public Medicion obtenerUltimaMedicion(Double latitud, Double longitud) throws SQLException {
        String sql = "SELECT m.fecha_hora_medicion, m.temperatura, m.sensacion_termica, "
                + "m.condicion, m.probabilidad_lluvia, e.nombre "
                + "FROM estaciones_meteorologicas e "
                + "JOIN mediciones_climaticas m ON m.id_estacion = e.id_estacion "
                + "ORDER BY (e.latitud - ?) * (e.latitud - ?) + (e.longitud - ?) * (e.longitud - ?), "
                + "m.fecha_hora_medicion DESC "
                + "LIMIT 1";

        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, latitud);
            stmt.setDouble(2, latitud);
            stmt.setDouble(3, longitud);
            stmt.setDouble(4, longitud);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medicion medicion = new Medicion();
                    medicion.setUbicacion(rs.getString("nombre"));
                    medicion.setFechaHoraMedicion(rs.getTimestamp("fecha_hora_medicion").toString());
                    medicion.setTemperatura(rs.getDouble("temperatura"));
                    medicion.setSensacionTermica(rs.getDouble("sensacion_termica"));
                    medicion.setCondicion(rs.getString("condicion"));
                    medicion.setProbabilidadLluvia(rs.getInt("probabilidad_lluvia"));
                    return medicion;
                }
                return null;
            }
        }
    }

}