package py.una.entidad;

public class Medicion {

    private String fechaHoraMedicion;
    private Double temperatura;
    private Double sensacionTermica;
    private String condicion;
    private Integer probabilidadLluvia;
    private String ubicacion;

    public Medicion() {
    }

    public Medicion(String ubicacion, String fechaHoraMedicion, Double temperatura,
            Double sensacionTermica, String condicion, Integer probabilidadLluvia) {

        this.ubicacion = ubicacion;
        this.fechaHoraMedicion = fechaHoraMedicion;
        this.temperatura = temperatura;
        this.sensacionTermica = sensacionTermica;
        this.condicion = condicion;
        this.probabilidadLluvia = probabilidadLluvia;
    }

    public String getFechaHoraMedicion() {
        return fechaHoraMedicion;
    }

    public void setFechaHoraMedicion(String fechaHoraMedicion) {
        this.fechaHoraMedicion = fechaHoraMedicion;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getSensacionTermica() {
        return sensacionTermica;
    }

    public void setSensacionTermica(Double sensacionTermica) {
        this.sensacionTermica = sensacionTermica;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Integer getProbabilidadLluvia() {
        return probabilidadLluvia;
    }

    public void setProbabilidadLluvia(Integer probabilidadLluvia) {
        this.probabilidadLluvia = probabilidadLluvia;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}