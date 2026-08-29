    package py.una.server.udp;

    import java.io.*;
    import java.net.*;


    public class UDPServer {
        
        
        public void ejecutar() {
            
            // Variables
            int puertoServidor = 9876;
            
            try {
                //1) Creamos el socket Servidor de Datagramas (UDP)
                DatagramSocket serverSocket = new DatagramSocket(puertoServidor);
                System.out.println("Servidor Sistemas Distribuidos - UDP ");
                
                //2) buffer de datos a enviar y recibir
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];

                
                //3) Servidor siempre esperando
                while (true) {

                    receiveData = new byte[1024];

                    DatagramPacket receivePacket =
                            new DatagramPacket(receiveData, receiveData.length);


                    System.out.println("Esperando a algun cliente... ");

                    // 4) Receive LLAMADA BLOQUEANTE
                    serverSocket.receive(receivePacket);
                    
                    System.out.println("________________________________________________");
                    System.out.println("Aceptamos un paquete");

                    // Datos recibidos e Identificamos quien nos envio
                    String datoRecibido = new String(receivePacket.getData());
                    datoRecibido = datoRecibido.trim();
                    System.out.println("DatoRecibido: " + datoRecibido );

                    // Ip
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    System.out.println("De : " + IPAddress + ":" + port);
                    System.out.println("Consulta Recibida : ");
                    
            
                    // Enviamos la respuesta inmediatamente a ese mismo cliente
                    // Es no bloqueante
                    sendData = "respuesta".getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData, sendData.length, IPAddress,port);

                    serverSocket.send(sendPacket);

                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }

        }
    }  

