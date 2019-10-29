package randomwalkjava;

import com.sun.glass.ui.Screen;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time RMS
 */
class ExecReal extends Data {

    private String language;
    private double realscalefactor;
    private double linewidth;
    private boolean isrealscaled;
    private boolean newdata;

    /**
     * Initiating class
     */
    ExecReal(String language) {
        super();
        this.setLanguage(language);
    }

    void setRmsClick(File folder, String executable, @NotNull Button execNappi, SceneRealTimeRms rmsScene,
                     Button closeNappiReal, Button menuNappiReal, Button helpNappiReal,
                     HBox isovalikkoReal, TextArea textAreaReal, Pane realPane, GraphicsContext piirturi) {

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

                if (!rmsScene.isRunning())
                    return;

                if (isovalikkoReal.getChildren().contains(textAreaReal)) {
                    textAreaReal.clear();
                    isovalikkoReal.getChildren().remove(textAreaReal);
                    isovalikkoReal.getChildren().add(realPane);
                }

                String[] vars = rmsScene.getVars();
                /*
                 * FROM SCENEREALTIMERMS
                 * vars from user:
                 * vars[0] = particles,     USER
                 * vars[1] = diameter,      n/a
                 * vars[2] = charge,        n/a
                 * vars[3] = steps,         USER
                 * vars[4] = dimension,     USER
                 * vars[5] = mmc,           n/a
                 * vars[6] = fixed,         n/a
                 * vars[7] = lattice,       n/a
                 * vars[8] = save           n/a
                 */

                int dim = parseInt(vars[4]);

                piirturi.setGlobalAlpha(1.0);
                piirturi.setFill(Color.BLACK);
                if (dim == 1) {
                    piirturi.fillRect(0, 0, 1.0 / getRealScalefactor() * getAnimWidth(), getAnimHeight());
                } else {
                    piirturi.fillRect(0, 0, 1.0 / getRealScalefactor() * getAnimWidth(), 1.0 / getRealScalefactor() * getAnimHeight());
                }
                piirturi.fill();

                /*
                 * DRAW ANIMATION
                 */
                rmsScene.refresh(folder, executable, piirturi, getRealScalefactor(), getLinewidth(),
                    isNewdata(), getAnimWidth());
                setNewdata(false);

                this.prevTime = currentNanoTime;
            }
        }.start();

        execNappi.setOnMouseClicked((MouseEvent event) -> {
            if (rmsScene.isRunning()) {
                rmsScene.stop();
                if (this.isRealScaled()) {
                    if (this.getVars()[4].equals("1"))
                        piirturi.scale(1.0 / this.getRealScalefactor(), 1.0);
                    else
                        piirturi.scale(1.0 / this.getRealScalefactor(), 1.0 / this.getRealScalefactor());
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

                int particles = parseInt(getVars()[0]);
                int steps = parseInt(getVars()[3]);
                int dim = parseInt(this.getVars()[4]);

                if (particles < 0) fail = true;
                if (steps < 1) fail = true;
                if (dim < 1 || dim > 3) fail = true;

                if (fail) return;

                if (rmsScene.getFxplot() != null) {
                    if (rmsScene.getFxplot().getFrame().isShowing()
                        || rmsScene.getFxplot().getFrame().isActive()
                        || rmsScene.getFxplot().getFrame().isDisplayable())
                        rmsScene.getFxplot().getFrame().dispose();
                }

                rmsScene.setFxplot(new FXPlot());
                rmsScene.getFxplot().setFXPlot(this.getLanguage(), "Walks&norm");

                double expected = Math.sqrt(steps);
                this.setRealScalefactor(
                    10.0 * Math.sqrt(this.getAnimWidth() * expected) / (Math.pow(Math.log(steps), 3.0)));

                if (dim == 1) {
                    this.setLinewidth(1.0 / Math.log10(steps));
                    piirturi.scale(this.getRealScalefactor(), 1.0);
                } else {
                    this.setLinewidth(1.2 / (this.getRealScalefactor() * Math.sqrt(Math.log10(steps))));
                    piirturi.scale(this.getRealScalefactor(), this.getRealScalefactor());
                }
                this.setRealScaled();
                piirturi.setGlobalAlpha(1.0 / this.getRealScalefactor() * Math.pow(Math.log10(steps), 2.0));

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
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the vars
     */
    private String[] getVars() { return vars.clone(); }

    /**
     * @param vars the vars to set
     */
    private void setVars(@NotNull String[] vars) { this.vars = vars.clone(); }

    /**
     * @return the realscalefactor
     */
    @Contract(pure = true)
    private double getRealScalefactor() { return realscalefactor; }

    /**
     * @param realscalefactor the realscalefactor to set
     */
    private void setRealScalefactor(double realscalefactor) { this.realscalefactor = realscalefactor; }

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
     * @return the linewidth
     */
    @Contract(pure = true)
    private double getLinewidth() { return linewidth; }

    /**
     * @param linewidth the linewidth to set
     */
    private void setLinewidth(double linewidth) { this.linewidth = linewidth; }

    /**
     * @return the isrealscaled
     */
    @Contract(pure = true)
    private boolean isRealScaled() { return isrealscaled; }

    /**
     */
    private void setRealScaled() { this.isrealscaled = true; }

    /**
     * @return the newdata
     */
    @Contract(pure = true)
    private boolean isNewdata() { return newdata; }

    /**
     * @param newdata the newdata to set
     */
    private void setNewdata(boolean newdata) { this.newdata = newdata; }

}
