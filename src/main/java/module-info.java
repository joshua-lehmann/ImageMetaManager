module ch.hftm {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.slf4j;
    opens ch.hftm to javafx.fxml;
    exports ch.hftm;
}
