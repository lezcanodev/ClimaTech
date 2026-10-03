# ClimaTech

## Requisitos

- JDK 8, Maven, PostgreSQL
- Crear BD `climatech`, ejecutar `sql.sql`
- Ajustar `ProyectoJava/src/main/java/py/una/bd/Bd.java` (url, user, password)

## Descargar

```bash
git clone <url-del-repositorio>
cd climaTech/ProyectoJava
```

## Servidor (todos los servicios)

Un solo proceso: TCP `:5002`, UDP clima `:5001`, receptor mediciones `:5003`, simulador de estación cada 5 s.

```bash
mvn -q compile exec:java -Dexec.mainClass=py.una.server.ClimaTech
```

## Probar clientes (otra consola, misma carpeta `ProyectoJava`)

Comando base:

```bash
mvn -q compile exec:java -Dexec.mainClass=<clase>
```

| Servicio | Protocolo | Clase | Datos de ejemplo |
|----------|-----------|--------|------------------|
| Clima actual | UDP 5001 | `py.una.server.udp.UDPClient` | lat `-25.2637`, lon `-57.5759` |
| Pronóstico extendido | TCP 5002 | `py.una.server.tcp.TCPClient` | lat/lon Asunción, días `5` |
| Alertas (suscripción + callback TCP) | TCP 5002 | `py.una.server.tcp.ClienteFlujoAlertasTCP` | puerto callback `9100`, lat/lon, radio `10` (entero, 1–32767) |

**Ejemplos**

```bash
mvn -q compile exec:java -Dexec.mainClass=py.una.server.udp.UDPClient
```

```bash
mvn -q compile exec:java -Dexec.mainClass=py.una.server.tcp.TCPClient
```

```bash
mvn -q compile exec:java -Dexec.mainClass=py.una.server.tcp.ClienteFlujoAlertasTCP
```

Solo suscripción (callback HTTP aparte): `py.una.server.tcp.SuscripcionAlertasTCPClient`

Medición manual a receptor (opcional; ClimaTech ya simula): `py.una.server.udp.SimuladorEstacionUDP`
