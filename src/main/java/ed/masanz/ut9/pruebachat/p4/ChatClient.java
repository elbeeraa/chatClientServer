package ed.masanz.ut9.pruebachat.p4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient {

    private static final int PORT = 12345;

    private static String name;

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

            requestName(out, in, userInput);

//            System.out.println("Conectado al servidor " + ip + " Escribe tu nombre:");
//            name = userInput.nextLine();

            // Hilo para escuchar mensajes del servidor sin bloquear la escritura
            new Thread(() -> {
                while (in.hasNextLine()) {
                    System.out.println("\n" + in.nextLine());
                }
            }).start();

            System.out.println("Ya puedes escribir mensajes:");
            while (userInput.hasNextLine()) {
                String message = userInput.nextLine().trim();
                if(message.equalsIgnoreCase("bye")){
                    System.out.println("Desconectando del servidor...");
                    break;
                }
                out.println(message);
            }

        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor. ¿Está encendido?");
        }
    }

    private static void requestName(PrintWriter out, Scanner in, Scanner userInput) {
        while (true) {
            System.out.println("Conectado al servidor. Escribe tu nombre:");
            name = userInput.nextLine().trim();
            out.println(name);
            if (in.hasNextLine()) {
                String response = in.nextLine();
                if (response.equals("OK")) {
                    System.out.println("Nombre aceptado. Ya puedes escribir mensajes:");
                    break;
                } else {
                    System.out.println("El nombre ya está en uso. Inténtalo de nuevo.");
                }
            } else {
                System.out.println("No se recibió respuesta del servidor. Inténtalo de nuevo.");
            }
        }
    }
}
