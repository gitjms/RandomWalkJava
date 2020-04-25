package jms.randomwalk.scenes;

import enums.DblSizes;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import jms.randomwalk.ui.HelpText;

import java.awt.*;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for setting scene properties via button scene choices.
 * (Stage titles, stage measures and positions, scene textareas)
 */
public class SetSceneChoices {

    private Stage stage;
    private double screenWidth;
    private double screenHeight;
    private String language;

    /**
     * Initiating class.
     * @param stage main stage
     */
    public SetSceneChoices(Stage stage) {
        super();
        this.setStage(stage);
        this.setScreenWidth(Toolkit.getDefaultToolkit().getScreenSize().width);
        this.setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    /**
     * Method for setting scene button effects for scenes '1D Distance' and 'Rms calculation'.
     * @param language GUI language
     * @param button button of the scene
     * @param scene scene
     * @param textFI GUI language in Finnish
     * @param textEN GUI language in English
     */
    public void setSceneEffects(String language, Button button, Scene scene, String textFI, String textEN) {
        this.setLanguage(language);

        button.setOnMouseClicked(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? textFI : textEN);
            this.getStage().setScene(scene);
        });
    }

    /**
     * Method for setting scene button effects for the rest of the scenes.
     * @param language GUI language
     * @param button scene button
     * @param scene scene
     * @param textFI GUI language in Finnish
     * @param textEN GUI language in English
     */
    public void setBigSceneEffects(String language, Button button, Scene scene, String textFI, String textEN) {
        this.setLanguage(language);

        button.setOnMouseClicked(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? textFI : textEN);
            if (this.getStage().getWidth() == DblSizes.STGW.getDblSize()) {
                this.getStage().setWidth(DblSizes.STGW.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTW.getDblSize()));
                this.getStage().setHeight(DblSizes.STGH.getDblSize() + (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTH.getDblSize()));
                this.getStage().setX(this.getScreenWidth() - (DblSizes.ANIMSIZE.getDblSize() + DblSizes.PANEW.getDblSize()));
                this.getStage().setY((this.getScreenHeight() - DblSizes.STGH.getDblSize()) / 2.0 - (DblSizes.ANIMSIZE.getDblSize() - DblSizes.TXTH.getDblSize()) / 2.0 - 10.0);
            }
            this.getStage().setScene(scene);
        });
    }

    /**
     * Method for setting scene button effects in returning to menu from other scenes.
     * @param language GUI language
     * @param button scene button
     * @param which which scene
     * @param textArea textarea of the scene
     * @param textAreaMenu textarea of menu
     * @param scene scene
     * @param num different scenes get different measures accordin to the num
     */
    public void setMenuEffects(String language, Button button, String which,
        TextArea textArea, TextArea textAreaMenu, Scene scene, int num) {

        this.setLanguage(language);
        HelpText helpText = new HelpText();

        button.setOnAction(event -> {
            this.getStage().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku" : "Random Walk");
            switch (which) {
                case "path":
                    if (textArea.getText().startsWith(this.getLanguage().equals("fin") ? "\n Liikeradat" : "\n Path Tracing")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
                case "1Ddist":
                    if (textArea.getText().startsWith(this.getLanguage().equals("fin") ? "\n 1D etäisyys" : "\n 1D Distance")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
                case "calc":
                    if (textArea.getText().startsWith("\n Rms vs. sqrt(S)")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
                case "real":
                    if (textArea.getText().startsWith(this.getLanguage().equals("fin") ? "\n Reaaliaika-rms" : "\n Real Time Rms")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
                case "diff":
                    if (textArea.getText().startsWith(this.getLanguage().equals("fin") ? "\n Diffuusio" : "\n Diffusion")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
                case "saw":
                    if (textArea.getText().startsWith(this.getLanguage().equals("fin") ? "\n Reaaliaika-saw" : "\n Real Time Saw")
                        || textArea.getText().isEmpty()) {
                        textAreaMenu.setText(this.getLanguage().equals("fin") ? helpText.welcomeFI() : helpText.welcomeEN());
                    } else {
                        textAreaMenu.setText(textArea.getText());
                    }
                    break;
            }

            if (num == 1) {
                this.getStage().setY((this.getScreenHeight() - DblSizes.STGH.getDblSize()) / 2.0);
                this.getStage().setHeight(DblSizes.STGH.getDblSize());
            } else if (num == 2) {
                this.getStage().setX(this.getScreenWidth() - DblSizes.STGW.getDblSize());
                this.getStage().setY((this.getScreenHeight() - DblSizes.STGH.getDblSize()) / 2.0);
                this.getStage().setWidth(DblSizes.STGW.getDblSize());
                this.getStage().setHeight(DblSizes.STGH.getDblSize());
            }
            this.getStage().setScene(scene);
        });
    }

    /**
     * @return the stage
     */
    private Stage getStage() {
        return this.stage;
    }

    /**
     * @param stage the stage to set
     */
    private void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return the language
     */
    private String getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the screenWidth
     */
    private double getScreenWidth() {
        return screenWidth;
    }

    /**
     * @param screenWidth the screenWidth to set
     */
    private void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * @return the screenHeight
     */
    private double getScreenHeight() {
        return screenHeight;
    }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
    }
}
