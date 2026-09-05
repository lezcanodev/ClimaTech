package py.una.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

/**
 * Carga datos de prueba para poder simular los servicios de clima
 */
public class ClimaTechSeed {

    public static void generarDatos() {
        try {
            Connection connection = Bd.connect();
            Random rnd = new Random();

            // Creamos 3 estaciones
            Object[][] estaciones = {
                    // id_estacion, latitud, longitud, nombre
                    { "EST-001", -25.2637, -57.5759, "Estación Asunción Centro" },
                    { "EST-002", -25.3500, -57.6300, "Estación Lambaré" },
                    { "EST-003", -25.2900, -57.5100, "Estación San Lorenzo" }
            };

            // Insertamos las estaciones solo si no existen en la bd
            String sqlEstacion = "INSERT INTO estaciones_meteorologicas (id_estacion, latitud, longitud, nombre) " +
                    "VALUES (?, ?, ?, ?) ON CONFLICT (id_estacion) DO NOTHING";
            try (PreparedStatement stmt = connection.prepareStatement(sqlEstacion)) {
                for (Object[] e : estaciones) {
                    stmt.setString(1, (String) e[0]);
                    stmt.setDouble(2, (Double) e[1]);
                    stmt.setDouble(3, (Double) e[2]);
                    stmt.setString(4, (String) e[3]);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // Generamos de forma aleatoria las mediciones
            String[] condiciones = { "Soleado", "Parcialmente nublado", "Nublado", "Tormentas aisladas", "Lluvia" };
            String sqlMedicion = "INSERT INTO mediciones_climaticas (id_estacion, fecha_hora_medicion, temperatura, sensacion_termica, condicion, probabilidad_lluvia) VALUES (?, NOW(), ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sqlMedicion)) {
                for (Object[] e : estaciones) {
                    // Generamos una medicion
                    String idEstacion = (String) e[0];
                    double temperatura = 15 + rnd.nextDouble() * 20; // 15-35°C
                    double sensacion = temperatura + (rnd.nextDouble() * 4 - 2); // ±2°C
                    String condicion = condiciones[rnd.nextInt(condiciones.length)];
                    int probLluvia = rnd.nextInt(101);

                    stmt.setString(1, idEstacion);
                    stmt.setDouble(2, temperatura);
                    stmt.setDouble(3, sensacion);
                    stmt.setString(4, condicion);
                    stmt.setInt(5, probLluvia);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}