package py.una.entidad;

public class SuscripcionAlerta {

    private String idSuscripcion;
    private String estado;
    private String fechaRegistro;
    private String sistemaSuscriptor;
    private Double latitud;
    private Double longitud;
    private Integer radioKm;
    private String tiposAlertaJson;
    private String urlCallback;

    public String getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(String idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getSistemaSuscriptor() {
        return sistemaSuscriptor;
    }

    public void setSistemaSuscriptor(String sistemaSuscriptor) {
        this.sistemaSuscriptor = sistemaSuscriptor;
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

    public Integer getRadioKm() {
        return radioKm;
    }

    public void setRadioKm(Integer radioKm) {
        this.radioKm = radioKm;
    }

    public String getTiposAlertaJson() {
        return tiposAlertaJson;
    }

    public void setTiposAlertaJson(String tiposAlertaJson) {
        this.tiposAlertaJson = tiposAlertaJson;
    }

    public String getUrlCallback() {
        return urlCallback;
    }

    public void setUrlCallback(String urlCallback) {
        this.urlCallback = urlCallback;
    }
}
