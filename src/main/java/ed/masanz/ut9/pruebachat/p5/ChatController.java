package ed.masanz.ut9.pruebachat.p5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatController {

    @FXML
    private TextArea areaMensajes;

    @FXML
    private Button bttnConectar;

    @FXML
    private Button bttnEnviar;

    @FXML
    private TextField textIP;

    @FXML
    private TextField textMensajes;

    @FXML
    private TextField textPort;

    @FXML
    private TextField textNombre;

    private boolean isConnected;
    private  int port;
    private String ip;
    private String userName;
    private Socket socket;
    private PrintWriter out;
    private BufferedInputStream in;


    @FXML
    void controladorBotonConectar(ActionEvent event) {
        if(isConnected){
            disconnect();
        } else {
            connect();
        }
    }

    private void disconnect() {
        System.out.println("Desconectando...");
        setConnected(false);
    }

    private void connect() {
//        System.out.println("Conectando a " + textIP.getText() + ":" + textPort.getText());
        ip = textIP.getText();
       if(ip.trim().isEmpty()){
           textIP.requestFocus();
           return;
       }
       userName = textNombre.getText();
       if(userName.trim().isEmpty()){
           textNombre.requestFocus();
           return;
       }
       try{
           port = Integer.parseInt(textPort.getText());
       }catch (NumberFormatException e) {
           textPort.requestFocus();
           return;
       }
//        setConnected(true);
       new Thread(() -> connectInBackground()).start();
    }

    private void connectInBackground() {

    }

    @FXML
    private void initialize() {
        areaMensajes.setEditable(false);
        setConnected(false);
    }

    private void setConnected(boolean b) {
        isConnected = b;

        bttnConectar.setText(b ? "Desconectar" : "Conectar");
        textIP.setDisable(b);
        textPort.setDisable(b);
        bttnConectar.setDisable(b);

        areaMensajes.setDisable(!b);
        textMensajes.setDisable(!b);
        bttnEnviar.setDisable(!b);
    }

    @FXML
    void controladorBotonEnviar(ActionEvent event) {

    }

}
