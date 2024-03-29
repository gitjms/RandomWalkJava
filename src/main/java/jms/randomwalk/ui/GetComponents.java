package jms.randomwalk.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for creating HBoxes, VBoxes, GridPanes, Panes, and TextAreas.
 */
public class GetComponents {

    private final boolean isWin = SystemUtils.IS_OS_WINDOWS;
    
    /**
     * method for setting HBoxes
     * @param ins insets values
     * @param spc spacing value
     * @return hbox
     */
    public HBox getHBox(int ins, int spc) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(ins, ins, ins, ins));
        hbox.setSpacing(spc);
        return hbox;
    }

    /**
     * Method for setting VBoxes.
     * @param spc spacing value
     * @return vbox
     */
    public VBox getVBox(int spc) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(spc);
        return vbox;
    }

    /**
     * method for setting Panes for Real Time Rms and Diffusion
     * @param alusta javafx Canvas
     * @param width pane width
     * @param height pane height
     * @return pane
     */
    public Pane getPane(Canvas alusta, double width, double height) {
        Pane pane = new Pane();
        pane.setPrefSize(width, height);
        if (alusta != null) {
            pane.getChildren().add(alusta);
        }
        pane.setVisible(true);
        return pane;
    }

    /**
     * method for setting Panes for Rms Calculation and Real Time Saw
     * @param image javafx ImageView
     * @param width pane width
     * @param height pane height
     * @return pane
     */
    public Pane getPane2(ImageView image, double width, double height) {
        Pane pane = new Pane();
        pane.setMaxSize(width, height);
        image.setFitWidth(width);
        image.setFitHeight(height);
        pane.getChildren().add(image);
        pane.setVisible(true);
        return pane;
    }

    /**
     * method for setting ImageView to Pane for Real Time Saw
     * @param pane pane object
     * @param image javafx ImageView
     * @param width pane width
     * @param height pane height
     */
    public void getPaneView(Pane pane, ImageView image, double width, double height) {
        pane.getChildren().remove(0);
        pane.setMaxSize(width, height);
        image.setFitWidth(width);
        image.setFitHeight(height);
        pane.getChildren().add(image);
    }

    /**
     * Method for setting buttons to GridPane.
     * @param nappi1 button for 'Path Tracing'
     * @param nappi2 button for '1D Distance'
     * @param nappi3 button for 'RMS vs SQRT(S)'
     * @param nappi4 button for 'Real Time RMS'
     * @param nappi5 button for 'Diffusion'
     * @param nappi6 button for 'Real Time SAW'
     * @return asettelu
     */
    public static GridPane getAsettelu(Button nappi1, Button nappi2, Button nappi3, Button nappi4, Button nappi5, Button nappi6) {
        GridPane asettelu = new GridPane();
        asettelu.setMaxWidth(200.0);
        asettelu.setVgap(5);
        asettelu.setHgap(10);
        asettelu.setPadding(new Insets(0, 0, 0, 0));

        GridPane.setHalignment(nappi1, HPos.LEFT);
        asettelu.add(nappi1, 0, 0, 2, 1);
        final Pane empty1 = new Pane();
        GridPane.setHalignment(empty1, HPos.LEFT);
        asettelu.add(empty1, 0, 1, 2, 1);
        GridPane.setHalignment(nappi2, HPos.LEFT);
        asettelu.add(nappi2, 0, 2, 2, 1);
        final Pane empty2 = new Pane();
        GridPane.setHalignment(empty2, HPos.LEFT);
        asettelu.add(empty2, 0, 3, 2, 1);
        GridPane.setHalignment(nappi3, HPos.LEFT);
        asettelu.add(nappi3, 0, 4, 2, 1);
        final Pane empty3 = new Pane();
        GridPane.setHalignment(empty3, HPos.LEFT);
        asettelu.add(empty3, 0, 5, 2, 1);
        GridPane.setHalignment(nappi4, HPos.LEFT);
        asettelu.add(nappi4, 0, 6, 2, 1);
        final Pane empty4 = new Pane();
        GridPane.setHalignment(empty4, HPos.LEFT);
        asettelu.add(empty4, 0, 7, 2, 1);
        GridPane.setHalignment(nappi5, HPos.LEFT);
        asettelu.add(nappi5, 0, 8, 2, 1);
        final Pane empty5 = new Pane();
        GridPane.setHalignment(empty5, HPos.LEFT);
        asettelu.add(empty5, 0, 9, 2, 1);
        GridPane.setHalignment(nappi6, HPos.LEFT);
        asettelu.add(nappi6, 0, 10, 2, 1);
        final Pane empty6 = new Pane();
        GridPane.setHalignment(empty6, HPos.LEFT);
        asettelu.add(empty6, 0, 11, 2, 1);

        return asettelu;
    }

    /**
     * Method for creating textareas.
     * @param width textarea width
     * @param height textarea height
     * @return textArea
     */
    public TextArea getTextArea(double width, double height) {
        TextArea textArea = new TextArea();
        textArea.setMinWidth(width);
        textArea.setMaxWidth(width);
        textArea.setMinHeight(height);
        textArea.setMaxHeight(height);
        textArea.setFont(this.isWin == true ? Font.font("Consolas", FontWeight.EXTRA_BOLD, 15) : Font.font("System Regular", FontWeight.EXTRA_BOLD, 15));
        textArea.setEditable(false);
        textArea.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        textArea.setBlendMode(BlendMode.DIFFERENCE);

        return textArea;
    }
}
