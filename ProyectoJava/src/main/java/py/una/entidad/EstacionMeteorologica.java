package py.una.entidad;

public class EstacionMeteorologica {

    private String idEstacion;
    private Double latitud;
    private Double longitud;
    private String nombre;
    private Medicion ultimaMedicion;

    public EstacionMeteorologica() {
    }

    public EstacionMeteorologica(String idEstacion, Double latitud, Double longitud, String nombre) {
        this.idEstacion = idEstacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
    }

    public String getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(String idEstacion) {
        this.idEstacion = idEstacion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Medicion getUltimaMedicion() {
        return ultimaMedicion;
    }

    public void setUltimaMedicion(Medicion ultimaMedicion) {
        this.ultimaMedicion = ultimaMedicion;
    }
}