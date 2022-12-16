module ch.hftm {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens ch.hftm to javafx.fxml;
    exports ch.hftm;
}
