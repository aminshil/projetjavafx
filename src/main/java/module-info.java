module com.example.user {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jakarta.mail;
    requires org.jfree.jfreechart;
    requires org.apache.pdfbox;
    requires jbcrypt;
    requires javafx.swing;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires okhttp3;

    opens sample to javafx.fxml;
    exports sample;

    // Open the 'entite' package to the javafx.base module
    opens entite to javafx.base;
}
