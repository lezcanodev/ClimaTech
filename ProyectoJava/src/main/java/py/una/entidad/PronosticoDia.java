package py.una.entidad;

public class PronosticoDia {

    private String fecha;
    private Double tempMaxima;
    private Double tempMinima;
    private String condicion;
    private Integer probabilidadLluvia;

    public PronosticoDia() {
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getTempMaxima() {
        return tempMaxima;
    }

    public void setTempMaxima(Double tempMaxima) {
        this.tempMaxima = tempMaxima;
    }

    public Double getTempMinima() {
        return tempMinima;
    }

    public void setTempMinima(Double tempMinima) {
        this.tempMinima = tempMinima;
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
}
