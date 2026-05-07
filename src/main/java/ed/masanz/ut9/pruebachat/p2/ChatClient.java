package ed.masanz.ut9.pruebachat.p2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient {

    private static final int PORT = 12345;

    public static void main(String[] args) {
       Scanner userInput = new Scanner(System.in);
       System.out.println("¿Que IP te quieres conectar?");
       String ip = userInput.nextLine().trim();
       if(ip.isEmpty()){
           ip = "localhost";
       }
       start(ip);
    }
    private static void start(String ip) {
        try (Socket socket = new Socket(ip, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner in = new Scanner(socket.getInputStream());
             Scanner userInput = new Scanner(System.in)) {

            System.out.println("Conectado al servidor " + ip + " Escribe tu nombre:");
            String name = userInput.nextLine();

            // Hilo para escuchar mensajes del servidor sin bloquear la escritura
            new Thread(() -> {
                while (in.hasNextLine()) {
                    System.out.println("\n" + in.nextLine());
                }
            }).start();

            System.out.println("Ya puedes escribir mensajes:");
            while (userInput.hasNextLine()) {
                out.println(name + ": " + userInput.nextLine());
            }

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor. ¿Está encendido?");
        }
    }
}
