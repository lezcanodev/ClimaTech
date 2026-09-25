package py.una.entidad;

import java.util.List;

public class PronosticoExtendido {

    private String ubicacion;
    private List<PronosticoDia> pronostico;

    public PronosticoExtendido() {
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<PronosticoDia> getPronostico() {
        return pronostico;
    }

    public void setPronostico(List<PronosticoDia> pronostico) {
        this.pronostico = pronostico;
    }
}
