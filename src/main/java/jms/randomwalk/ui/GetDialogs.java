package jms.randomwalk.ui;

import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for creating alerts and dialogs.
 */
public class GetDialogs {

    /**
     * Method for creating a dialog for language choice.
     * @param buttonFI button for finnish
     * @param buttonEX button for exit
     * @param buttonEN button for english
     * @return dialog
     */
    public Dialog<?> getLangChoice(ButtonType buttonFI, ButtonType buttonEX, ButtonType buttonEN) {

        GetComponents getComponents = new GetComponents();

        /*
         *   DIALOG WITH BUTTONS
         */
        Dialog<String> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getStylesheets().add("/styles.css");
        dialog.setTitle("Kielivalinta - Language Choice");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(buttonFI, buttonEN, buttonEX);

        /*
         *   LABEL TEXTS
         */
        Label labTextFI = new Label("Käyttöliittymä ja\nkuvat suomeksi");
        Label labTextEN = new Label("User interface and\nimages in english");
        labTextFI.setFont(Font.font(18));
        labTextEN.setFont(Font.font(18));
        Label labFI = new Label("FI");
        Label labEN = new Label("EN");
        labFI.setFont(Font.font(30));
        labEN.setFont(Font.font(30));

        /*
         *   COUNTRY FLAG IMAGES
         */
        Image imageFI = new Image("fin.png");
        Image imageEN = new Image("eng.png");
        ImageView ivFI = new ImageView(imageFI);
        ImageView ivEN = new ImageView(imageEN);
        ivFI.setFitHeight(50);
        ivFI.setPreserveRatio(true);
        ivEN.setFitHeight(50);
        ivEN.setPreserveRatio(true);

        /*
         *   BORDERS FOR IMAGES
         */
        HBox borderboxFI = getComponents.getHBox(0, 0);
        String borderstyle = "-fx-border-color: black;" + "-fx-border-width: 0.5;";
        borderboxFI.setStyle(borderstyle);
        borderboxFI.getChildren().add(ivFI);
        HBox borderboxEN = getComponents.getHBox(0, 0);
        borderboxEN.setStyle(borderstyle);
        borderboxEN.getChildren().add(ivEN);

        /*
         *   HORIZONTAL BOXES FOR FLAGS AND TEXTS 'FI'/'EN'
         */
        HBox hboxFI = getComponents.getHBox(0, 10);
        HBox hboxEN = getComponents.getHBox(0, 10);
        hboxFI.getChildren().addAll(borderboxFI, labFI);
        hboxEN.getChildren().addAll(borderboxEN, labEN);

        /*
         *   VERTICAL BOXES FOR TEXTS AND HBOXES, LEFT FOR FI AND RIGHT FOR EN
         */
        VBox vboxLeft = getComponents.getVBox(10);
        vboxLeft.getChildren().addAll(labTextFI, hboxFI);
        VBox vboxRight = getComponents.getVBox(10);
        vboxRight.getChildren().addAll(labTextEN, hboxEN);

        /*
         *   SEPARATOR LINE BETWEEN LEFT AND RIGHT
         */
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setValignment(VPos.CENTER);

        /*
         *   HORIZONTAL BOX WITH ALL COMPONENTS
         */
        HBox hboxBig = getComponents.getHBox(5, 10);
        hboxBig.getChildren().addAll(vboxLeft, separator, vboxRight);

        dialog.getDialogPane().setContent(hboxBig);

        return dialog;
    }

    /**
     * Method for creating an alert dialog.
     * @param language which language selected
     * @param buttonYES button for rejecting warning
     * @param buttonNO button for accepting warning
     * @param text warning text
     * @return alert dialog
     */
    public Alert getAlert(String language, ButtonType buttonYES, ButtonType buttonNO, String text) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().getStylesheets().add("/styles.css");
        alert.setTitle(language.equals("fin") ? "Varoitus" : "Warning");
        alert.setHeaderText(text);
        alert.setContentText(null);
        alert.getDialogPane().getButtonTypes().addAll(buttonYES, buttonNO);

        return alert;
    }

    /**
     * Method for creating an info dialog.
     * @param text info text
     * @return info dialog
     */
    public Alert getInfo(String text) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().getStylesheets().add("/styles.css");
        alert.setTitle("Info");
        alert.setHeaderText(text);
        alert.setContentText(null);

        return alert;
    }

    /**
     * Method for creating a confirmation dialog.
     * @param language which language selected
     * @param buttonYES button for accepting confirmation
     * @param buttonNO button for rejecting confirmation
     * @return confirmation dialog
     */
    Dialog<?> getConfirmation(String language, ButtonType buttonYES, ButtonType buttonNO) {

        Dialog<String> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getStylesheets().add("/styles.css");
        dialog.setTitle(language.equals("fin") ? "Vahvistus" : "Confirmation");
        dialog.setHeaderText(language.equals("fin") ? "Suljetaanko sovellus?" : "Close application?");
        dialog.setContentText(null);
        dialog.getDialogPane().getButtonTypes().addAll(buttonYES, buttonNO);

        return dialog;
    }
}
