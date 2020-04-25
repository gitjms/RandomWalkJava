
package jms.randomwalk.datahandling;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for thread failure message.
 */
class Message extends Thread {
    @Override
    public void run() {
        Alert alert;
        alert = new Alert(
            Alert.AlertType.ERROR,
            "Sovellus sulkeutuu virheen vuoksi.\nProgram is closing due to an error.",
            ButtonType.OK);
        alert.showAndWait();
    }
}