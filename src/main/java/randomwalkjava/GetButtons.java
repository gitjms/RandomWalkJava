package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.PrintWriter;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for creating buttons
 */
class GetButtons extends HelpText {

    private Stage stage;
    private JFrame frame;
    private SceneDiff scene;
    private DropShadow shadow;
    private String language;
    private String text;
    private Pane mempane;
    private TextArea newTextArea;
    private ButtonType buttonYES;
    private ButtonType buttonNO;

    /**
     * Initiating class
     */
    GetButtons(Stage stage) {
        super();
        this.setStage(stage);
        this.setShadow(new DropShadow());
    }

    /**
     * method for setting scene buttons
     * @param text button text
     * @return button
     */
    Button getSceneButton(String text) {
        this.setText(text);

        Button button = new Button(this.getText());
        button.setMinWidth(this.getButtonWidth());
        button.setMaxWidth(this.getButtonWidth());
        button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));
        button.setVisible(true);

        return button;
    }

    /**
     * method for setting scene execute buttons
     * @param language GUI language
     * @param text button text
     * @return button
     */
    Button getExecuteButton(String language, int width, @NotNull String text) {
        this.setLanguage(language);

        Button button;
        switch (text) {
            case "RUN":
                button = this.getLanguage().equals("fin") ? new Button("AJA") : new Button("RUN");
                button.setStyle("-fx-background-color: Red");
                button.setMinWidth(this.getButtonWidth());
                button.setMaxWidth(this.getButtonWidth());
                break;
            case "SAW":
                button = this.getLanguage().equals("fin") ? new Button("AJA SAW") : new Button("RUN SAW");
                button.setStyle("-fx-background-color: Red");
                button.setMinWidth(this.getCbmcButtonWidth());
                button.setMaxWidth(this.getCbmcButtonWidth());
                break;
            case "CBMC":
                button = this.getLanguage().equals("fin") ? new Button("AJA CBMC") : new Button("RUN CBMC");
                button.setStyle("-fx-background-color: Red");
                button.setMinWidth(this.getCbmcButtonWidth());
                button.setMaxWidth(this.getCbmcButtonWidth());
                break;
            case "ANIM":
                button = this.getLanguage().equals("fin") ? new Button("ANIMAATIO") : new Button("ANIMATION");
                button.setStyle("-fx-background-color: Red");
                button.setMinWidth(this.getButtonWidth());
                button.setMaxWidth(this.getButtonWidth());
                break;
            case "PLOT":
                button = this.getLanguage().equals("fin") ? new Button("KUVAAJA") : new Button("PLOT");
                button.setStyle("-fx-background-color: Blue");
                button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                break;
            case "SAWPLOT":
                button = this.getLanguage().equals("fin") ? new Button("SAW KUVAAJA") : new Button("SAW PLOT");
                button.setStyle("-fx-background-color: Blue");
                button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                break;
            default:
                button = this.getLanguage().equals("fin") ? new Button("SUORITA") : new Button("EXECUTE");
                button.setStyle("-fx-background-color: Red");
                button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
                break;
        }

        button.setDefaultButton(true);
        button.setTextFill(Color.WHITE);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));
        button.setVisible(true);

        return button;
    }

    /**
     * method for setting scene close buttons (excluding 'Diffusion')
     * @param getRealScene scene
     * @param getDiffScene scene
     * @param getSAWScene scene
     * @param ex Python execution class
     * @param language GUI language
     * @param frame JFrame for plots
     * @param buttonYES confirmation button
     * @param buttonNO confirmation button
     * @return button for closing stage
     */
    Button getCloseButton(SceneRealTimeRms getRealScene, SceneDiff getDiffScene, SceneRealTimeSaw getSAWScene, int width,
                          Execution ex, String language, JFrame frame, ButtonType buttonYES, ButtonType buttonNO) {
        this.setLanguage(language);
        this.setFrame(frame);
        this.setButtonYES(buttonYES);
        this.setButtonNO(buttonNO);

        GetDialogs getDialogs = new GetDialogs();

        Button button = this.getLanguage().equals("fin") ? new Button("SULJE") : new Button("CLOSE");
        button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.setTextFill(Color.RED);
        button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(button, HPos.LEFT);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));
        button.setOnAction(event -> {
            /*
            * CONFIRMATION DIALOG
            */
            Dialog conf = getDialogs.getConfirmation(getLanguage(), this.getButtonYES(), this.getButtonNO());
            conf.showAndWait();

            if ( conf.getResult().toString().equals(
                this.getLanguage().equals("fin") ? "ButtonType [text=KYLLÄ, buttonData=YES]" : "ButtonType [text=YES, buttonData=YES]") ) {
                if (this.getFrame() != null)
                    if (this.getFrame().isShowing() || this.getFrame().isActive() || this.getFrame().isDisplayable())
                        this.getFrame().dispose();
                if (getRealScene.getFxplot() != null)
                    if (getRealScene.getFxplot().getFrame().isShowing()
                        || getRealScene.getFxplot().getFrame().isActive()
                        || getRealScene.getFxplot().getFrame().isDisplayable())
                        getRealScene.getFxplot().getFrame().dispose();
                if (getDiffScene.getFxplot() != null)
                    if (getDiffScene.getFxplot().getFrame().isShowing()
                        || getDiffScene.getFxplot().getFrame().isActive()
                        || getDiffScene.getFxplot().getFrame().isDisplayable())
                        getDiffScene.getFxplot().getFrame().dispose();
                if (getSAWScene.getFxplot() != null)
                    if (getSAWScene.getFxplot().getFrame().isShowing()
                        || getSAWScene.getFxplot().getFrame().isActive()
                        || getSAWScene.getFxplot().getFrame().isDisplayable())
                        getSAWScene.getFxplot().getFrame().dispose();

                if (getDiffScene.runtimeIsRunning()) {
                    Runtime.getRuntime().gc();
                    getDiffScene.stopRuntime();
                }
                if (getRealScene.runtimeIsRunning()) {
                    Runtime.getRuntime().gc();
                    getRealScene.stopRuntime();
                }
                if (getSAWScene.runtimeIsRunning()) {
                    Runtime.getRuntime().gc();
                    getSAWScene.stopRuntime();
                }
                if (ex.runtimeIsRunning()) {
                    Runtime.getRuntime().gc();
                    ex.stopRuntime();
                }

                conf.close();
                this.getStage().close();
                Platform.exit();
                System.exit(0);
            }
        });
        button.setVisible(true);

        return button;
    }

    /**
     * method for setting scene close button for 'Diffusion' only
     * @param language GUI language
     * @param diffScene scene
     * @return button
     */
    Button getCloseDiffButton(String language, SceneDiff diffScene, JFrame frame) {
        this.setLanguage(language);
        this.setFrame(frame);
        this.setDiffScene(diffScene);

        GetDialogs getDialogs = new GetDialogs();

        Button button = this.getLanguage().equals("fin") ? new Button("SULJE") : new Button("CLOSE");
        button.setMinWidth(this.getButtonWidth());
        button.setMaxWidth(this.getButtonWidth());
        button.setTextFill(Color.RED);
        button.setBackground(new Background( new BackgroundFill( Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(button, HPos.LEFT);
        button.addEventHandler( MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler( MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));

        /*
         * CONFIRMATION DIALOG
         */
        Dialog conf = getDialogs.getConfirmation(getLanguage(), this.getButtonYES(), this.getButtonNO());

        button.setOnAction(event -> {
            if ( this.getDiffScene().timerIsRunning() && this.getDiffScene().barrierIsOn() ) {
                if ( this.getDiffScene().walkState() ) {
                    PrintWriter pw = null;
                    if (this.getDiffScene().getProcOut() != null)
                        pw = new PrintWriter(this.getDiffScene().getProcOut());
                    if (pw != null) {
                        pw.println("-");
                        pw.flush();
                        pw.close();
                    }
                }
            }
            conf.showAndWait();
            if ( conf.getResult().toString().equals(
                this.getLanguage().equals("fin") ? "ButtonType [text=KYLLÄ, buttonData=YES]" : "ButtonType [text=YES, buttonData=YES]") ) {
                if (this.getFrame() != null)
                    if (this.getFrame().isShowing() || this.getFrame().isActive() || this.getFrame().isDisplayable())
                        this.getFrame().dispose();
                    if (this.getDiffScene().getFxplot() != null)
                        if (this.getDiffScene().getFxplot().getFrame().isShowing()
                            || this.getDiffScene().getFxplot().getFrame().isActive()
                            || this.getDiffScene().getFxplot().getFrame().isDisplayable())
                                this.getDiffScene().getFxplot().getFrame().dispose();
                        this.getStage().close();
                        conf.close();

                    if (this.getDiffScene().runtimeIsRunning()) {
                        Runtime.getRuntime().gc();
                        this.getDiffScene().stopRuntime();
                    }
                    Platform.exit();
                    System.exit(0);
                }
        });
        button.setVisible(true);

        return button;
    }

    /**
     * method for setting scene help buttons
     * @param language GUI language
     * @param textArea textarea of the scene
     * @param isovalikko HBox of the scene
     * @param pane pane for javafx Canvas
     * @param which which scene
     * @return button
     */
    Button getHelpButton(String language, TextArea textArea, HBox isovalikko, Pane pane, @NotNull String which, int width) {
        this.setLanguage(language);

        GetComponents getComponents = new GetComponents();

        Button button = this.getLanguage().equals("fin") ? new Button("OHJE") : new Button("HELP");
        button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,CornerRadii.EMPTY,Insets.EMPTY)));
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));

        switch (which) {
            case "menu":
                button.setOnAction(event -> textArea.setText(this.getLanguage().equals("fin") ? super.menuFI() : super.menuEN()));
                break;
            case "path":
                button.setOnAction(event -> textArea.setText(this.getLanguage().equals("fin") ? super.pathtracingFI() : super.pathtracingEN()));
                break;
            case "1Ddist":
                button.setOnAction(event -> textArea.setText(this.getLanguage().equals("fin") ? super.distance1DFI() : super.distance1DEN()));
                break;
            case "calc":
                button.setOnAction(event -> {
                    assert isovalikko != null;
                    if (isovalikko.getChildren().contains(pane) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        this.mempane = new Pane();
                        this.mempane.getChildren().addAll(pane.getChildren());
                        isovalikko.getChildren().removeAll(pane);
                        this.newTextArea = getComponents.GetTextArea(this.getTextWidth(), this.getTextHeight());
                        this.newTextArea.setText(this.getLanguage().equals("fin") ? super.calculationFI() : super.calculationEN());
                        this.newTextArea.setVisible(true);
                        isovalikko.getChildren().add(this.newTextArea);
                        button.setText(this.getLanguage().equals("fin") ? "PALAA" : "BACK");
                    } else if (isovalikko.getChildren().contains(this.newTextArea) && (button.getText().equals("PALAA") || button.getText().equals("BACK"))) {
                        if (this.mempane != null) {
                            isovalikko.getChildren().remove(this.newTextArea);
                            pane.getChildren().addAll(this.mempane.getChildren());
                            isovalikko.getChildren().add(pane);
                            button.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
                        }
                    } else if (isovalikko.getChildren().contains(textArea) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        textArea.setText(this.getLanguage().equals("fin") ? super.calculationFI() : super.calculationEN());
                    }
                });
                break;
            case "real":
                button.setOnAction(event -> {
                    assert isovalikko != null;
                    if (isovalikko.getChildren().contains(pane) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        this.mempane = new Pane();
                        this.mempane.getChildren().addAll(pane.getChildren());
                        isovalikko.getChildren().removeAll(pane);
                        this.newTextArea = getComponents.GetTextArea(this.getAnimWidth(), this.getAnimHeight());
                        this.newTextArea.setText(this.getLanguage().equals("fin") ? super.realtimermsFI() : super.realtimermsEN());
                        this.newTextArea.setVisible(true);
                        isovalikko.getChildren().add(this.newTextArea);
                        button.setText(this.getLanguage().equals("fin") ? "PALAA" : "BACK");
                    } else if (isovalikko.getChildren().contains(this.newTextArea) && (button.getText().equals("PALAA") || button.getText().equals("BACK"))) {
                        if (this.mempane != null) {
                            isovalikko.getChildren().remove(this.newTextArea);
                            pane.getChildren().addAll(this.mempane.getChildren());
                            isovalikko.getChildren().add(pane);
                            button.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
                        }
                    } else if (isovalikko.getChildren().contains(textArea) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        textArea.setText(this.getLanguage().equals("fin") ? super.realtimermsFI() : super.realtimermsEN());
                    }
                });
                break;
            case "diff":
                button.setOnAction(event -> {
                    assert isovalikko != null;
                    if (isovalikko.getChildren().contains(pane) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        this.mempane = new Pane();
                        this.mempane.getChildren().addAll(pane.getChildren());
                        isovalikko.getChildren().removeAll(pane);
                        this.newTextArea = getComponents.GetTextArea(this.getTextWidth(), this.getTextHeight());
                        this.newTextArea.setText(this.getLanguage().equals("fin") ? super.diffFI() : super.diffEN());
                        this.newTextArea.setVisible(true);
                        isovalikko.getChildren().add(this.newTextArea);
                        button.setText(this.getLanguage().equals("fin") ? "PALAA" : "BACK");
                    } else if (isovalikko.getChildren().contains(this.newTextArea) && (button.getText().equals("PALAA") || button.getText().equals("BACK"))) {
                        if (this.mempane != null) {
                            isovalikko.getChildren().remove(this.newTextArea);
                            pane.getChildren().addAll(this.mempane.getChildren());
                            isovalikko.getChildren().add(pane);
                            button.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
                        }
                    } else if (isovalikko.getChildren().contains(textArea) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        textArea.setText(this.getLanguage().equals("fin") ? super.diffFI() : super.diffEN());
                    }
                });
                break;
            case "saw":
                button.setOnAction(event -> {
                    assert isovalikko != null;
                    if (isovalikko.getChildren().contains(pane) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        this.mempane = new Pane();
                        this.mempane.getChildren().addAll(pane.getChildren());
                        isovalikko.getChildren().removeAll(pane);
                        this.newTextArea = getComponents.GetTextArea(this.getSawTextWidth(), this.getSawTextHeight());
                        this.newTextArea.setText(this.getLanguage().equals("fin") ? super.realtimesawFI() : super.realtimesawEN());
                        this.newTextArea.setVisible(true);
                        isovalikko.getChildren().add(this.newTextArea);
                        button.setText(this.getLanguage().equals("fin") ? "PALAA" : "BACK");
                    } else if (isovalikko.getChildren().contains(this.newTextArea) && (button.getText().equals("PALAA") || button.getText().equals("BACK"))) {
                        if (this.mempane != null) {
                            isovalikko.getChildren().remove(this.newTextArea);
                            pane.getChildren().addAll(this.mempane.getChildren());
                            isovalikko.getChildren().add(pane);
                            button.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
                        }
                    } else if (isovalikko.getChildren().contains(textArea) && (button.getText().equals("OHJE") || button.getText().equals("HELP"))) {
                        textArea.setText(this.getLanguage().equals("fin") ? super.realtimesawFI() : super.realtimesawEN());
                    }
                });
                break;
        }

        button.setVisible(true);

        return button;
    }

    /**
     * method for setting return to menu button for scenes
     * @param language GUI language
     * @return button
     */
    Button getMenuButton(String language, int width) {
        this.setLanguage(language);

        Button button = this.getLanguage().equals("fin") ? new Button("PALAA MENUUN") : new Button("BACK TO MENU");
        button.setMinWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.setMaxWidth(width == 0 ? this.getButtonWidth() : this.getSawButtonWidth());
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));
        button.setVisible(true);

        return button;
    }

    /**
     * method for setting barrier button for scene 'Diffusion'
     * @param language GUI language
     * @return button
     */
    Button getDiffBarrierButton(String language) {
        this.setLanguage(language);

        Button button = this.getLanguage().equals("fin") ? new Button("JATKA") : new Button("CONTINUE");
        button.setMinWidth(this.getButtonWidth());
        button.setMaxWidth(this.getButtonWidth());
        button.setTextFill(Color.BLACK);
        button.setBackground(new Background(new BackgroundFill(Color.LIME,CornerRadii.EMPTY,Insets.EMPTY)));
        GridPane.setHalignment(button, HPos.LEFT);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> button.setEffect(this.getShadow()));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> button.setEffect(null));
        button.setVisible(false);

        return button;
    }

    /**
     * @return the shadow
     */
    @Contract(pure = true)
    private DropShadow getShadow() { return this.shadow; }

    /**
     * @param shadow the shadow to set
     */
    private void setShadow(DropShadow shadow) { this.shadow = shadow; }

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the text
     */
    @Contract(pure = true)
    private String getText() { return this.text; }

    /**
     * @param text the text to set
     */
    private void setText(String text) { this.text = text; }

    /**
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getButtonWidth() { return 150.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getSawButtonWidth() { return 205.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the buttonWidth
     */
    @Contract(pure = true)
    private double getCbmcButtonWidth() { return 100.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the stage
     */
    @Contract(pure = true)
    private Stage getStage() { return this.stage; }

    /**
     * @param stage the stage to set
     */
    private void setStage(Stage stage) { this.stage = stage; }

    /**
     * @return the frame
     */
    @Contract(pure = true)
    private JFrame getFrame() { return this.frame; }

    /**
     * @param frame the frame to set
     */
    private void setFrame(JFrame frame) { this.frame = frame; }

    /**
     * @return the scene
     */
    @Contract(pure = true)
    private SceneDiff getDiffScene() { return this.scene; }

    /**
     * @param scene the scene to set
     */
    private void setDiffScene(SceneDiff scene) { this.scene = scene; }

    /**
     * @return the buttonYES
     */
    @Contract(pure = true)
    private ButtonType getButtonYES() { return this.buttonYES; }

    /**
     * @param buttonYES the buttonYES to set
     */
    private void setButtonYES(ButtonType buttonYES) { this.buttonYES = buttonYES; }

    /**
     * @return the buttonNO
     */
    @Contract(pure = true)
    private ButtonType getButtonNO() { return this.buttonNO; }

    /**
     * @param buttonNO the buttonNO to set
     */
    private void setButtonNO(ButtonType buttonNO) { this.buttonNO = buttonNO; }

    /**
     * @return the textwidth
     */
    @Contract(pure = true)
    private double getTextWidth() { return 740.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textheight
     */
    @Contract(pure = true)
    private double getTextHeight() { return 600.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textwidth
     */
    @Contract(pure = true)
    private double getSawTextWidth() { return 690.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textheight
     */
    @Contract(pure = true)
    private double getSawTextHeight() { return 615.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the animwidth
     */
    @Contract(pure = true)
    private double getAnimWidth() { return 750.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the animheight
     */
    @Contract(pure = true)
    private double getAnimHeight() { return 750.0 / Screen.getMainScreen().getRenderScale(); }

}

