package jms.randomwalk.execs;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import jms.randomwalk.datahandling.Data;
import jms.randomwalk.plots.Execution;
import jms.randomwalk.plots.FXPlot;
import jms.randomwalk.scenes.SceneRealTimeSaw;

import java.io.File;
import java.util.*;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time SAW.
 */
public class ExecSAW extends Data {

    private String language;
    private boolean issaw;
    private boolean iseff;
    private boolean ismcsaw;
    private boolean first;
    private List<Double> sawExpd;
    private List<Double> sawRms;
    private List<Double> expdRuns;
    private List<Double> rmsRuns;
    private List<Double> effRuns;
    private List<Double> succRuns;
    private List<Double> sawLengths;
    private List<Integer> xAxis;
    private List<Integer> xhistAxis;
    private List<Double> yhistAxis;

    /**
     * Initiating class.
     * @param language which ui language: finnish or english
     */
    public ExecSAW(String language) {
        super();
        this.setLanguage(language);
    }

    /**
     * Method for setting run SAW mouseclicked.
     * @param folder data folder
     * @param executable file for execution
     * @param execSAW execute SAW button
     * @param execMCSAW execute MC SAW button
     * @param runEFF efficiency run button
     * @param sawScene scene for SAW
     * @param isovalikkoSaw HBox for components
     * @param sawPane Pane object
     * @param sawText help text
     * @param plotSAW plot button
     * @param closeNappiSAW close button
     * @param menuNappiSAW menu button
     * @param helpNappiSAW help button
     * @param aaSlider Slider object
     */
    public void setSawClick(File folder, String executable, Button execSAW, Button execMCSAW,
        Button runEFF, SceneRealTimeSaw sawScene, HBox isovalikkoSaw, Pane sawPane,
        TextArea sawText, Button plotSAW, Button closeNappiSAW, Button menuNappiSAW,
        Button helpNappiSAW, Slider aaSlider) {

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

                if (isovalikkoSaw.getChildren().contains(sawText)) {
                    sawText.clear();
                    isovalikkoSaw.getChildren().remove(sawText);
                    isovalikkoSaw.getChildren().add(sawPane);
                }

                if (!sawScene.isRunning()) {
                    return;
                }

                sawScene.refresh(folder, executable, getFirst(), getSawLengths(), getSawExpd(), getSawRms(),
                    getExpdRuns(), getRmsRuns(), null, null, getXAxis(), getXhistAxis(),
                    isSaw(), isEff(), aaSlider, 0);

                if (getFirst()) {
                    setFirst(false);
                }

                this.prevTime = currentNanoTime;
            }
        }.start();

        execSAW.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execSAW.setText(this.getLanguage().equals("fin") ? "AJA SAW" : "RUN SAW");
                runEFF.setDisable(false);
            } else {
                sawScene.setSawMc("E");
                sawScene.setSawPlot("-");
                sawScene.setSave("-");
                this.setVars(sawScene.getVars());
                boolean fail = false;

                int steps = Integer.parseInt(this.getVars()[3]);
                if (steps > 0) {
                    fail = true;
                }

                int dim = Integer.parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) {
                    fail = true;
                }

                if (fail) {
                    return;
                }

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable()) {
                        sawScene.getFxplot().getFrame().dispose();
                    }
                }

                this.setIsSaw(true);
                this.setIsEff(false);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawExpd(new ArrayList<>());
                this.setSawRms(new ArrayList<>());

                this.setExpdRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getExpdRuns().add(0.0);
                }

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getRmsRuns().add(0.0);
                }

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getSawLengths().add(0.0);
                }

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getXAxis().add(x);
                }

                this.setXhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) {
                    this.getXhistAxis().add(x);
                }
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) {
                    this.getYhistAxis().add(0.0);
                }

                this.setFirst(true);

                Map<Object, Object> labelMap = new HashMap<>();
                if (dim == 2) {
                    for (int i = 0; i < 20; i++) {
                        labelMap.put(i, String.valueOf((i + 1) * 3));
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        labelMap.put(i, String.valueOf((i + 1) * 20));
                    }
                }

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getExpdRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execMCSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
                runEFF.setDisable(true);
            }
        });

        execMCSAW.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execMCSAW.setText(this.getLanguage().equals("fin") ? "AJA MC" : "RUN MC");
                runEFF.setDisable(false);
            } else {
                sawScene.setSawMc("F");
                sawScene.setSawPlot("-");
                sawScene.setSave("-");
                this.setVars(sawScene.getVars());
                boolean fail = false;

                int steps = Integer.parseInt(this.getVars()[3]);
                if (this.getVars()[3].isEmpty() || steps == 0) {
                    fail = true;
                }

                int dim = Integer.parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) {
                    fail = true;
                }

                if (fail) {
                    return;
                }

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable()) {
                        sawScene.getFxplot().getFrame().dispose();
                    }
                }

                this.setIsSaw(false);
                this.setIsEff(false);

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(), "saw");

                this.setSawExpd(new ArrayList<>());
                this.setSawRms(new ArrayList<>());

                this.setExpdRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getExpdRuns().add(0.0);
                }

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getRmsRuns().add(0.0);
                }

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getSawLengths().add(0.0);
                }

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) {
                    this.getXAxis().add(x);
                }

                this.setXhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) {
                    this.getXhistAxis().add(x);
                }
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < 20; x++) {
                    this.getYhistAxis().add(0.0);
                }

                this.setFirst(true);

                Map<Object, Object> labelMap = new HashMap<>();
                if (steps < 30) {
                    for (int i = 0; i < 20; i++) {
                        labelMap.put(i, String.valueOf((i + 1)));
                    }
                } else if (dim == 2 || steps < 200) {
                    for (int i = 0; i < 20; i++) {
                        labelMap.put(i, String.valueOf((i + 1) * 3));
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        labelMap.put(i, String.valueOf((i + 1) * 10));
                    }
                }

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getExpdRuns(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getRmsRuns());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());

                sawScene.start();

                execMCSAW.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                execSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
                runEFF.setDisable(true);
            }
        });
    }

    /**
     * Method for checking if user input in GUI is an integer.
     * @param str GUI input string
     * @return true if input is an integer, false otherwise
     */
    private static boolean isNumInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Method for setting plot SAW mouseclicked.
     * @param plotNappi plot button
     * @param execSAW execute SAW button
     * @param sawScene scene for SAW
     * @param valikkoSAW VBox object
     * @param datapath data path
     * @param datafolder data folder
     * @param fexec fortran executable file
     * @param pyexecsaw2d python executable file
     * @param pyexecsaw3d python executable file
     * @param ex instance of Execution class (package plots)
     * @param setMax max iterations
     * @param result result text
     */
    public void setPlotClick(Button plotNappi, Button execSAW, SceneRealTimeSaw sawScene, VBox valikkoSAW,
        String datapath, File datafolder, String fexec, String pyexecsaw2d, String pyexecsaw3d,
        Execution ex, TextField setMax, TextFlow result) {

        Label redlabel = new Label(this.getLanguage().equals("fin") ? "Ei tulosta" : "No result");
        redlabel.setTextFill(Color.RED);
        redlabel.setStyle("-fx-font-size: 20px;");

        Label greenlabel = new Label();
        greenlabel.setTextFill(Color.GREEN);
        greenlabel.setStyle("-fx-font-size: 20px;");

        FadeTransition fadegreen = new FadeTransition(Duration.seconds(0.2), greenlabel);
        FadeTransition fadered = new FadeTransition(Duration.seconds(0.2), redlabel);
        fadegreen.setFromValue(0.0);
        fadegreen.setToValue(1.0);
        fadegreen.setCycleCount(2);
        fadered.setFromValue(0.0);
        fadered.setToValue(1.0);
        fadered.setCycleCount(2);
        String txt1 = this.getLanguage().equals("fin") ? " ajo" : " run";
        String txt2 = this.getLanguage().equals("fin") ? " ajoa" : " runs";
        result.setTextAlignment(TextAlignment.CENTER);

        plotNappi.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
            }
            result.getChildren().clear();
            if (execSAW.isDisabled()) {
                sawScene.setSawMc("F");
                this.setIsMcsaw(true);
            } else {
                sawScene.setSawMc("E");
                this.setIsMcsaw(false);
            }
            sawScene.setSawPlot("p");
            sawScene.setSave("s");
            String[] variables = sawScene.getVars();
            this.setVars(variables);
            Data data = new Data(variables);
            int dim = Integer.parseInt(getVars()[4]);
            boolean fail = false;

            if (dim < 2 || dim > 3) {
                fail = true;
            }
            if (fail) {
                return;
            }

            valikkoSAW.setDisable(true);
            this.setIsEff(false);

            int count = 0;
            int max = 100;
            if (isNumInteger(setMax.getText().trim())) {
                max = Integer.parseInt(setMax.getText());
            }

            boolean ok = false;
            while (!ok && count < max) {
                ok = ex.executeSAW(datafolder, datapath, fexec, pyexecsaw2d,
                    pyexecsaw3d, valikkoSAW, data, this.getVars(), this.isMcsaw());
                count += 1;
            }

            result.setVisible(true);
            if (!ok) {
                result.getChildren().add(redlabel);
                fadered.play();
            } else {
                greenlabel.setText(count + (count == 1 ? txt1 : txt2));
                result.getChildren().add(greenlabel);
                fadegreen.play();
            }
            valikkoSAW.setDisable(false);
        });
    }

    /**
     * Method for setting efficiency mouseclicked.
     * @param folder data folder
     * @param executable fortran executable file
     * @param runEFF run efficiency button
     * @param execSAW execute SAW button
     * @param execMCSAW execute MCSAW button
     * @param plotNappi plot button
     * @param sawScene scene for efficiency
     * @param valikkoSAW VBox object
     * @param setEff TextField object
     * @param resultEff result text
     * @param aaSlider Slider object
     */
    public void setEffClick(File folder, String executable, Button runEFF, Button execSAW,
        Button execMCSAW, Button plotNappi, SceneRealTimeSaw sawScene,
        VBox valikkoSAW, TextField setEff, TextFlow resultEff, Slider aaSlider) {

        setEff.setOnKeyReleased(e -> {
            if (isNumInteger(setEff.getText().trim())) {
                execSAW.setDisable(true);
                execMCSAW.setDisable(true);
                plotNappi.setDisable(true);
                this.setIsEff(true);
            } else {
                execSAW.setDisable(false);
                execMCSAW.setDisable(false);
                plotNappi.setDisable(false);
                this.setIsEff(false);
            }
        });

        runEFF.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
            }
            resultEff.getChildren().clear();
            sawScene.setSawMc("F");
            sawScene.setSawPlot("e");
            sawScene.setSave("-");
            String[] variables = sawScene.getVars();
            this.setVars(variables);
            int dim = Integer.parseInt(getVars()[4]);
            boolean fail = false;

            if (dim < 2 || dim > 3) {
                fail = true;
            }
            if (fail) {
                return;
            }

            if (sawScene.getFxplot() != null) {
                if (sawScene.getFxplot().getFrame().isShowing()
                    || sawScene.getFxplot().getFrame().isActive()
                    || sawScene.getFxplot().getFrame().isDisplayable()) {
                    sawScene.getFxplot().getFrame().dispose();
                }
            }
            sawScene.setFxplot(new FXPlot());
            sawScene.getFxplot().setFXPlot(this.getLanguage(), "eff");

            this.setIsEff(true);
            this.setIsSaw(false);
            valikkoSAW.setDisable(true);
            this.setFirst(true);

            int maxRuns;
            if (isNumInteger(setEff.getText().trim())) {
                maxRuns = Integer.parseInt(setEff.getText());

                this.setEffRuns(new ArrayList<>());
                for (int x = 0; x < maxRuns; x++) {
                    this.getEffRuns().add(null);
                }
                this.setSuccRuns(new ArrayList<>());
                for (int x = 0; x < maxRuns; x++) {
                    this.getSuccRuns().add(null);
                }
                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < maxRuns; x++) {
                    this.getXAxis().add(x);
                }

                sawScene.getFxplot().setFData(this.getXAxis(), this.getEffRuns(), maxRuns);

                for (int i = 1; i <= maxRuns; i++) {
                    sawScene.setSawMcEff(String.valueOf(i));

                    sawScene.refresh(folder, executable, this.getFirst(), null, null, null,
                        null, null, this.getEffRuns(), this.getSuccRuns(), this.getXAxis(),
                        null, this.isSaw(), this.isEff(), aaSlider, maxRuns);

                    if (this.getFirst()) {
                        this.setFirst(false);
                    }
                }
            }

            valikkoSAW.setDisable(false);
        });
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
     * @return the issaw
     */
    private boolean isSaw() {
        return this.issaw;
    }

    /**
     * @param issaw the issaw to set
     */
    private void setIsSaw(boolean issaw) {
        this.issaw = issaw;
    }

    /**
     * @return the ismcsaw
     */
    private boolean isMcsaw() {
        return this.ismcsaw;
    }

    /**
     * @param ismcsaw the ismcsaw to set
     */
    private void setIsMcsaw(boolean ismcsaw) {
        this.ismcsaw = ismcsaw;
    }

    /**
     * @return the iseff
     */
    private boolean isEff() {
        return this.iseff;
    }

    /**
     * @param iseff the iseff to set
     */
    private void setIsEff(boolean iseff) {
        this.iseff = iseff;
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
     * @return the saw_lengths
     */
    private List<Double> getSawLengths() {
        return this.sawLengths;
    }

    /**
     * @param sawLengths the length to set
     */
    private void setSawLengths(List<Double> sawLengths) {
        this.sawLengths = sawLengths;
    }

    /**
     * @return the sawExpd
     */
    private List<Double> getSawExpd() {
        return this.sawExpd;
    }

    /**
     * @param sawExpd the sawExpd to set
     */
    private void setSawExpd(List<Double> sawExpd) {
        this.sawExpd = sawExpd;
    }

    /**
     * @return the sawRms
     */
    private List<Double> getSawRms() {
        return this.sawRms;
    }

    /**
     * @param sawRms the sawRms to set
     */
    private void setSawRms(List<Double> sawRms) {
        this.sawRms = sawRms;
    }

    /**
     * @return the expd_runs
     */
    private List<Double> getExpdRuns() {
        return this.expdRuns;
    }

    /**
     * @param expdRuns the expd_runs to set
     */
    private void setExpdRuns(List<Double> expdRuns) {
        this.expdRuns = expdRuns;
    }

    /**
     * @return the rms_runs
     */
    private List<Double> getRmsRuns() {
        return this.rmsRuns;
    }

    /**
     * @param rmsRuns the rms_runs to set
     */
    private void setRmsRuns(List<Double> rmsRuns) {
        this.rmsRuns = rmsRuns;
    }

    /**
     * @return the eff_runs
     */
    private List<Double> getEffRuns() {
        return this.effRuns;
    }

    /**
     * @param effRuns the eff_runs to set
     */
    private void setEffRuns(List<Double> effRuns) {
        this.effRuns = effRuns;
    }

    /**
     * @return the succ_runs
     */
    private List<Double> getSuccRuns() {
        return this.succRuns;
    }

    /**
     * @param succRuns the succ_runs to set
     */
    private void setSuccRuns(List<Double> succRuns) {
        this.succRuns = succRuns;
    }

    /**
     * @return the xAxis
     */
    private List<Integer> getXAxis() {
        return this.xAxis;
    }

    /**
     * @param xAxis the xAxis to set
     */
    private void setXAxis(List<Integer> xAxis) {
        this.xAxis = xAxis;
    }

    /**
     * @return the yhistAxis
     */
    private List<Double> getYhistAxis() {
        return this.yhistAxis;
    }

    /**
     * @param yhistAxis the yhistAxis to set
     */
    private void setYhistAxis(List<Double> yhistAxis) {
        this.yhistAxis = yhistAxis;
    }

    /**
     * @param xhistAxis x-axis data array for walk plot to set
     */
    private void setXhistAxis(List<Integer> xhistAxis) {
        this.xhistAxis = xhistAxis;
    }

    /**
     * @return x-axis data array for walk plot
     */
    private List<Integer> getXhistAxis() {
        return this.xhistAxis;
    }

    /**
     * @return the first
     */
    private boolean getFirst() {
        return first;
    }

    /**
     *  the first to set
     */
    private void setFirst(boolean first) {
        this.first = first;
    }
}
