module ed.masanz.ut9.pruebachat {
    requires javafx.controls;
    requires javafx.fxml;


    opens ed.masanz.ut9.pruebachat to javafx.fxml;
    exports ed.masanz.ut9.pruebachat;
    exports ed.masanz.ut9.pruebachat.p1;
    opens ed.masanz.ut9.pruebachat.p1 to javafx.fxml;
}