package py.una.server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPServerHilo extends Thread {

    private Socket socket = null;

    TCPMultiServer servidor;
    
    public TCPServerHilo(Socket socket, TCPMultiServer servidor ) {
        super("TCPServerHilo");
        this.socket = socket;
        this.servidor = servidor;
    }

    public void run() {

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    socket.getInputStream()));
            out.println("Bienvenido!");
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido: " + inputLine);

                
                //to-do: utilizar json
                if (inputLine.equals("Bye")) {
                    outputLine = "Usted apago el hilo";
                    break;
                    
                }else if (inputLine.equals("Terminar todo")) {
                    servidor.listening = false;
                    outputLine = "Usted apago todo";
                    break;
                    
                }else if (inputLine.split(":").length > 1) {
                	String usuario = inputLine.split(":")[1]; 
                	outputLine = "Usuario/a "+usuario+"agregado";
                }else if (inputLine.equals("Listar usuarios")) {
                	outputLine = "Lista de usuarios: " ;
                }else {
                	outputLine = "No introdujo orden a ejecutar." ;
                }
                
                out.println(outputLine);
            }
            out.close();
            in.close();
            socket.close();
            System.out.println("Finalizando Hilo");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
