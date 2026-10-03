package py.una.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import py.una.entidad.Medicion;
import py.una.entidad.PronosticoDia;
import py.una.entidad.PronosticoExtendido;
import py.una.entidad.SuscripcionAlerta;

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

    public PronosticoExtendido obtenerPronosticoExtendido(Double latitud, Double longitud, int dias)
            throws SQLException {
        String sqlEstacion = "SELECT id_estacion, nombre FROM estaciones_meteorologicas "
                + "ORDER BY (latitud - ?) * (latitud - ?) + (longitud - ?) * (longitud - ?) "
                + "LIMIT 1";

        String sqlPronostico = "SELECT fecha, temp_maxima, temp_minima, condicion, probabilidad_lluvia "
                + "FROM pronostico_diario "
                + "WHERE id_estacion = ? AND fecha >= CURRENT_DATE "
                + "ORDER BY fecha ASC "
                + "LIMIT ?";

        try (Connection conn = Bd.connect()) {
            String idEstacion = null;
            String nombreEstacion = null;

            try (PreparedStatement stmtEst = conn.prepareStatement(sqlEstacion)) {
                stmtEst.setDouble(1, latitud);
                stmtEst.setDouble(2, latitud);
                stmtEst.setDouble(3, longitud);
                stmtEst.setDouble(4, longitud);
                try (ResultSet rs = stmtEst.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    idEstacion = rs.getString("id_estacion");
                    nombreEstacion = rs.getString("nombre");
                }
            }

            List<PronosticoDia> diasPronostico = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlPronostico)) {
                stmt.setString(1, idEstacion);
                stmt.setInt(2, dias);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        PronosticoDia dia = new PronosticoDia();
                        dia.setFecha(rs.getDate("fecha").toString());
                        dia.setTempMaxima(rs.getDouble("temp_maxima"));
                        dia.setTempMinima(rs.getDouble("temp_minima"));
                        dia.setCondicion(rs.getString("condicion"));
                        dia.setProbabilidadLluvia(rs.getInt("probabilidad_lluvia"));
                        diasPronostico.add(dia);
                    }
                }
            }

            if (diasPronostico.isEmpty()) {
                return null;
            }

            PronosticoExtendido resultado = new PronosticoExtendido();
            resultado.setUbicacion(nombreEstacion);
            resultado.setPronostico(diasPronostico);
            return resultado;
        }
    }

    public void resetSubscripciones() throws SQLException {
        String sql = "DELETE FROM suscripciones_alertas";
        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    public SuscripcionAlerta registrarSuscripcion(String sistemaSuscriptor, Double latitud, Double longitud,
            int radioKm, String tiposAlertaJson, String urlCallback) throws SQLException {
        String idSuscripcion = "SUB-" + (System.currentTimeMillis() % 100000);

        String sql = "INSERT INTO suscripciones_alertas (id_suscripcion, sistema_suscriptor, latitud, longitud, "
                + "radio_km, tipos_alerta, url_callback, estado, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'activa', NOW()) "
                + "RETURNING id_suscripcion, estado, fecha_registro";

        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idSuscripcion);
            stmt.setString(2, sistemaSuscriptor);
            stmt.setDouble(3, latitud);
            stmt.setDouble(4, longitud);
            stmt.setInt(5, radioKm);
            stmt.setString(6, tiposAlertaJson);
            stmt.setString(7, urlCallback);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SuscripcionAlerta suscripcion = new SuscripcionAlerta();
                    suscripcion.setIdSuscripcion(rs.getString("id_suscripcion"));
                    suscripcion.setEstado(rs.getString("estado"));
                    suscripcion.setFechaRegistro(rs.getTimestamp("fecha_registro").toString());
                    return suscripcion;
                }
            }
        }
        return null;
    }

    public List<SuscripcionAlerta> listarSuscripcionesActivas() throws SQLException {
        String sql = "SELECT id_suscripcion, sistema_suscriptor, latitud, longitud, radio_km, tipos_alerta, "
                + "url_callback, estado, fecha_registro "
                + "FROM suscripciones_alertas WHERE estado = 'activa'";

        List<SuscripcionAlerta> lista = new ArrayList<>();
        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SuscripcionAlerta s = new SuscripcionAlerta();
                s.setIdSuscripcion(rs.getString("id_suscripcion"));
                s.setSistemaSuscriptor(rs.getString("sistema_suscriptor"));
                s.setLatitud(rs.getDouble("latitud"));
                s.setLongitud(rs.getDouble("longitud"));
                s.setRadioKm(rs.getInt("radio_km"));
                s.setTiposAlertaJson(rs.getString("tipos_alerta"));
                s.setUrlCallback(rs.getString("url_callback"));
                s.setEstado(rs.getString("estado"));
                s.setFechaRegistro(rs.getTimestamp("fecha_registro").toString());
                lista.add(s);
            }
        }
        return lista;
    }

    public double[] obtenerCoordenadasEstacion(String idEstacion) throws SQLException {
        String sql = "SELECT latitud, longitud FROM estaciones_meteorologicas WHERE id_estacion = ?";
        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idEstacion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new double[] { rs.getDouble("latitud"), rs.getDouble("longitud") };
                }
            }
        }
        return null;
    }

    public void guardarMedicion(String idEstacion, Double temperatura, String condicion, int probabilidadLluvia)
            throws SQLException {
        String sql = "INSERT INTO mediciones_climaticas (id_estacion, fecha_hora_medicion, temperatura, "
                + "sensacion_termica, condicion, probabilidad_lluvia) VALUES (?, NOW(), ?, ?, ?, ?)";
        try (Connection conn = Bd.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idEstacion);
            stmt.setDouble(2, temperatura);
            stmt.setDouble(3, temperatura);
            stmt.setString(4, condicion);
            stmt.setInt(5, probabilidadLluvia);
            stmt.executeUpdate();
        }
    }

}