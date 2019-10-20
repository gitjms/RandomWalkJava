package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for setting scene properties via button scene choices
 * (Stage titles, stage measures and positions, scene textareas)
 */
class SetSceneChoices extends HelpText {

    private Stage stage;
    private double screenWidth;
    private double screenHeight;
    private String language;

    /**
     * Initiating class
     */
    SetSceneChoices(Stage stage) {
        super();
        this.setStage(stage);
        this.setScreenWidth(Toolkit.getDefaultToolkit().getScreenSize().width / Screen.getMainScreen().getRenderScale());
        this.setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height / Screen.getMainScreen().getRenderScale());
    }

    /**
     * method for setting scene button effects for scene 'Path Tracing'
     * @param language GUI language
     * @param button button of the scene
     * @param scene scene
     */
    void setPathSceneEffects(String language, @NotNull Button button, Scene scene) {
        this.setLanguage(language);

        button.setOnMouseClicked(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? "Liikeradat" : "Path Tracing");
            if ( this.getStage().getHeight() == this.getStageHeight() ){
                this.getStage().setHeight(this.getStageHeight()+(this.getPathHeight()-this.getTextHeight()));
                this.getStage().setY((this.getScreenHeight()-this.getStageHeight())/2.0 - (this.getPathHeight()-this.getTextHeight())/2.0);
            }
            this.getStage().setScene(scene);
        });
    }

    /**
     * method for setting scene button effects for scenes '1D Distance' and 'Rms calculation'
     * @param language GUI language
     * @param button button of the scene
     * @param scene scene
     * @param textFI GUI language in Finnish
     * @param textEN GUI language in English
     */
    void setSceneEffects(String language, @NotNull Button button, Scene scene, String textFI, String textEN) {
        this.setLanguage(language);

        button.setOnMouseClicked(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? textFI : textEN);
            this.getStage().setScene(scene);
        });
    }

    /**
     * method for setting scene button effects for the rest of the scenes
     * @param language GUI language
     * @param button scene button
     * @param scene scene
     * @param textFI GUI language in Finnish
     * @param textEN GUI language in English
     */
    void setOtherSceneEffects(String language, @NotNull Button button, Scene scene, String textFI, String textEN) {
        this.setLanguage(language);

        button.setOnMouseClicked(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? textFI : textEN);
            if ( this.getStage().getWidth() == this.getStageWidth() ){
                this.getStage().setWidth(this.getStageWidth()+(this.getAnimWidth()-this.getTextWidth()));
                this.getStage().setHeight(this.getStageHeight()+(this.getAnimHeight()-this.getTextHeight()));
                this.getStage().setX(this.getScreenWidth()-(this.getAnimWidth()+this.getPaneWidth()));
                this.getStage().setY((this.getScreenHeight() -this.getStageHeight())/2.0 -(this.getAnimHeight() -this.getTextHeight())/2.0-10.0);
            }
            this.getStage().setScene(scene);
        });
    }

    /**
     * method for setting scene button effects in returning to menu from other scenes
     * @param language GUI language
     * @param button scene button
     * @param which which scene
     * @param textArea textarea of the scene
     * @param textAreaMenu textarea of menu
     * @param scene scene
     * @param num different scenes get different measures accordin to the num
     */
    void setMenuEffects(String language, @NotNull Button button, String which,
        TextArea textArea, TextArea textAreaMenu, Scene scene, int num) {

        this.setLanguage(language);

        button.setOnAction(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku" : "Random Walk");
            switch (which) {
                case "path":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.pathtracingFI() : super.pathtracingEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
                case "1Ddist":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.distance1DFI() : super.distance1DEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
                case "calc":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.calculationFI() : super.calculationEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
                case "real":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.realtimermsFI() : super.realtimermsEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
                case "mmc":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.mmcFI() : super.mmcEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
                case "saw":
                    if (textArea.getText().equals(this.getLanguage().equals("fin") ? super.realtimesawFI() : super.realtimesawEN()))
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? super.welcomeFI() : super.welcomeEN());
                    else
                        textAreaMenu.setText(textArea.getText());
                    break;
            }

            if (num == 1) {
                this.getStage().setY((this.getScreenHeight() - this.getStageHeight()) / 2.0);
                this.getStage().setHeight(this.getStageHeight());
            } if (num == 2) {
                this.getStage().setX(this.getScreenWidth()-this.getStageWidth());
                this.getStage().setY((this.getScreenHeight()-this.getStageHeight())/2.0);
                this.getStage().setWidth(this.getStageWidth());
                this.getStage().setHeight(this.getStageHeight());
            }
            this.getStage().setScene(scene);
        });
    }

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
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the stageWidth
     */
    @Contract(pure = true)
    private double getStageWidth() { return 940.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the stageHeight
     */
    @Contract(pure = true)
    private double getStageHeight() { return 660.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the pathheight
     */
    @Contract(pure = true)
    private double getPathHeight() { return 660.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textheight
     */
    @Contract(pure = true)
    private double getTextHeight() { return 600.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the screenWidth
     */
    @Contract(pure = true)
    private double getScreenWidth() { return screenWidth; }

    /**
     * @param screenWidth the screenWidth to set
     */
    private void setScreenWidth(double screenWidth) { this.screenWidth = screenWidth; }

    /**
     * @return the screenHeight
     */
    @Contract(pure = true)
    private double getScreenHeight() { return screenHeight; }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(double screenHeight) { this.screenHeight = screenHeight; }

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

    /**
     * @return the paneWidth
     */
    @Contract(pure = true)
    private double getPaneWidth() { return 200.0 / Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the textwidth
     */
    @Contract(pure = true)
    private double getTextWidth() { return 740.0 / Screen.getMainScreen().getRenderScale(); }

}