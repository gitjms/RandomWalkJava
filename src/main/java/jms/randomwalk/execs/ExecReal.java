package jms.randomwalk.execs;

import enums.DblSizes;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.ui.GetComponents;
import jms.randomwalk.scenes.SceneRealTimeRms;

import java.io.File;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time RMS.
 */
public class ExecReal extends Data {

    private String language;
    private double realscalefactor;
    private double linewidth;
    private boolean isrealscaled;
    private boolean newdata;
    private Pane pane;
    private TextArea textarea;
    private Canvas alusta;
    private HBox isovalikko;
    private GraphicsContext piirturi;

    /**
     * Initiating class.
     * @param language which ui language: finnish or english
     */
    public ExecReal(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * Method for setting execute RMS mouseclicked.
     * @param folder data folder
     * @param executable file for execution
     * @param execNappi execute button
     * @param rmsScene scene for RMS
     * @param closeNappiReal close button
     * @param menuNappiReal menu button
     * @param helpNappiReal help button
     * @param isovalikko HBox for components
     * @param textAreaReal help text
     * @param realPane Pane object
     * @param rtrmsAlusta Canvas object
     * @param piirturi GraphicsContext object
     */
    public void setRmsClick(File folder, String executable, Button execNappi, SceneRealTimeRms rmsScene,
        Button closeNappiReal, Button menuNappiReal, Button helpNappiReal,
        HBox isovalikko, TextArea textAreaReal, Pane realPane, Canvas rtrmsAlusta,
        GraphicsContext piirturi) {

        this.setIsoValikko(isovalikko);
        this.setTextArea(textAreaReal);
        this.setPane(realPane);
        this.setAlusta(rtrmsAlusta);
        this.setPiirturi(piirturi);

        /*
         * ANIMATION TIMER FOR REAL TIME RMS ANIMATION
         */
        new AnimationTimer() {
            private long prevTime = 0;

            @Override
            public void handle(long currentNanoTime) {

                /*
                 * REFESH ANIMATION IN ABOUT 100 MILLISECOND STEPS
                 */
                long sleepNanoseconds = 100 * 1000000;
                if ((currentNanoTime - this.prevTime) < sleepNanoseconds) {
                    return;
                }

                if (!rmsScene.isRunning()) {
                    return;
                }

                if (getIsoValikko().getChildren().contains(getTextArea())) {
                    getTextArea().clear();
                    getIsoValikko().getChildren().remove(getTextArea());
                }

                String[] vars = rmsScene.getVars();
                /*
                 * FROM SCENEREALTIMERMS
                 * vars from user:
                 * vars[0] = which simulation,  USER
                 * vars[1] = particles,         USER
                 * vars[2] = diameter,          n/a
                 * vars[3] = steps,             USER
                 * vars[4] = dimension,         USER
                 * vars[5] = calcfix or sawplot,n/a
                 * vars[6] = fixed,             n/a
                 * vars[7] = lattice,           n/a
                 * vars[8] = save               n/a
                 */

                int dim = Integer.parseInt(vars[4]);

                getPiirturi().setGlobalAlpha(1.0);
                getPiirturi().setFill(Color.BLACK);
                if (dim == 1) {
                    getPiirturi().fillRect(0, 0, 1.0 / getRealScalefactor() * DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize());
                } else {
                    getPiirturi().fillRect(0, 0, 1.0 / getRealScalefactor() * DblSizes.ANIMSIZE.getDblSize(), 1.0 / getRealScalefactor() * DblSizes.ANIMSIZE.getDblSize());
                }
                getPiirturi().fill();

                /*
                 * DRAW ANIMATION
                 */
                rmsScene.refresh(folder, executable, getPiirturi(), getRealScalefactor(), getLinewidth(),
                    isNewdata(), DblSizes.ANIMSIZE.getDblSize());
                setNewdata(false);

                this.prevTime = currentNanoTime;
            }
        }.start();

        execNappi.setOnMouseClicked((MouseEvent event) -> {
            if (rmsScene.isRunning()) {
                rmsScene.stop();
                if (this.isRealScaled()) {
                    if (this.getVars()[4].equals("1")) {
                        this.getPiirturi().scale(1.0 / this.getRealScalefactor(), 1.0);
                    } else {
                        this.getPiirturi().scale(1.0 / this.getRealScalefactor(), 1.0 / this.getRealScalefactor());
                    }
                }
                rmsScene.getPlotChoice().setDisable(false);
                rmsScene.getDimension().setDisable(false);
                menuNappiReal.setDisable(false);
                helpNappiReal.setDisable(false);
                closeNappiReal.setDisable(false);
                execNappi.setText(this.getLanguage().equals("fin") ? "UUSI AJO" : "NEW RUN");

            } else {
                this.setVars(rmsScene.getVars());
                rmsScene.setSave("-");
                boolean fail = false;

                int particles = Integer.parseInt(this.getVars()[1]);
                int steps = Integer.parseInt(this.getVars()[3]);
                int dim = Integer.parseInt(this.getVars()[4]);

                if (particles < 0) {
                    fail = true;
                }
                if (steps < 1) {
                    fail = true;
                }
                if (dim < 1 || dim > 3) {
                    fail = true;
                }

                if (fail) {
                    return;
                }

                if (rmsScene.getFxplot() != null) {
                    if (rmsScene.getFxplot().getFrame().isShowing()
                        || rmsScene.getFxplot().getFrame().isActive()
                        || rmsScene.getFxplot().getFrame().isDisplayable()) {
                        rmsScene.getFxplot().getFrame().dispose();
                    }
                }

                GetComponents getComponents = new GetComponents();

                if (this.getIsoValikko().getChildren().size() > 1) {
                    this.getIsoValikko().getChildren().remove(1);
                    this.setPane(getComponents.getPane(this.getAlusta(), DblSizes.ANIMSIZE.getDblSize(), DblSizes.ANIMSIZE.getDblSize()));
                    this.getIsoValikko().getChildren().add(this.getPane());
                    helpNappiReal.setText(this.getLanguage().equals("fin") ? "OHJE" : "HELP");
                }

                rmsScene.setFxplot(new FXPlot());
                rmsScene.getFxplot().setFXPlot(this.getLanguage(), "Walks&norm");

                double expected = Math.sqrt(steps);
                this.setRealScalefactor(
                    10.0 * Math.sqrt(DblSizes.ANIMSIZE.getDblSize() * expected) / (Math.pow(Math.log(steps), 3.0)));

                if (dim == 1) {
                    this.setLinewidth(1.0 / Math.log10(steps));
                    this.getPiirturi().scale(this.getRealScalefactor(), 1.0);
                } else {
                    this.setLinewidth(1.2 / (this.getRealScalefactor() * Math.sqrt(Math.log10(steps))));
                    this.getPiirturi().scale(this.getRealScalefactor(), this.getRealScalefactor());
                }
                this.setRealScaled();
                this.getPiirturi().setGlobalAlpha(1.0 / this.getRealScalefactor() * Math.pow(Math.log10(steps), 2.0));

                this.setNewdata(true);

                rmsScene.start();
                execNappi.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiReal.setDisable(true);
                helpNappiReal.setDisable(true);
                closeNappiReal.setDisable(true);
                rmsScene.getDimension().setDisable(true);
                rmsScene.getPlotChoice().setDisable(true);
            }
        });
    }

    /**
     * @return the textarea
     */
    private TextArea getTextArea() {
        return this.textarea;
    }

    /**
     * @param textarea the textarea to set
     */
    private void setTextArea(TextArea textarea) {
        this.textarea = textarea;
    }

    /**
     * @return the alusta
     */
    private Canvas getAlusta() {
        return this.alusta;
    }

    /**
     * @param alusta the alusta to set
     */
    private void setAlusta(Canvas alusta) {
        this.alusta = alusta;
    }

    /**
     * @return the isovalikko
     */
    private HBox getIsoValikko() {
        return this.isovalikko;
    }

    /**
     * @param isovalikko the isovalikko to set
     */
    private void setIsoValikko(HBox isovalikko) {
        this.isovalikko = isovalikko;
    }

    /**
     * @return the pane
     */
    private Pane getPane() {
        return this.pane;
    }

    /**
     * @param pane the pane to set
     */
    private void setPane(Pane pane) {
        this.pane = pane;
    }

    /**
     * @return the piirturi
     */
    private GraphicsContext getPiirturi() {
        return this.piirturi;
    }

    /**
     * @param piirturi the piirturi to set
     */
    private void setPiirturi(GraphicsContext piirturi) {
        this.piirturi = piirturi;
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
     * @return the vars
     */
    private String[] getVars() {
        return vars.clone();
    }

    /**
     * @param vars the vars to set
     */
    private void setVars(String[] vars) {
        this.vars = vars.clone();
    }

    /**
     * @return the realscalefactor
     */
    private double getRealScalefactor() {
        return realscalefactor;
    }

    /**
     * @param realscalefactor the realscalefactor to set
     */
    private void setRealScalefactor(double realscalefactor) {
        this.realscalefactor = realscalefactor;
    }

    /**
     * @return the linewidth
     */
    private double getLinewidth() {
        return linewidth;
    }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) {
        this.linewidth = linewidth;
    }

    /**
     * @return the isrealscaled
     */
    private boolean isRealScaled() {
        return isrealscaled;
    }

    /**
     */
    private void setRealScaled() {
        this.isrealscaled = true;
    }

    /**
     * @return the newdata
     */
    private boolean isNewdata() {
        return newdata;
    }

    /**
     * @param newdata the newdata to set
     */
    private void setNewdata(boolean newdata) {
        this.newdata = newdata;
    }

}
