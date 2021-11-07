module com.example.tp_entrega2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tp_entrega2 to javafx.fxml;
    exports com.example.tp_entrega2;
}