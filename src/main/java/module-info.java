open module es.cifpcarlos.pimandragora {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires static lombok;
    requires java.net.http;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires javafx.graphics;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.slf4j;
    // Expose only what you want other modules to compile against (usually just your app entry package)
    exports es.cifpcarlos3.pimandragora.presentation.app;

    // If you really want to keep jackson reflective access restricted, you can keep these,
    // but with open module they’re not needed.
    // opens es.cifpcarlos3.pimandragora.dto to com.fasterxml.jackson.databind;
}
