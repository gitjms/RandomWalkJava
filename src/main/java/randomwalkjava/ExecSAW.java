package randomwalkjava;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for executing and plotting Real Time SAW
 */
class ExecSAW extends Data {

    private String language;
    private boolean first;
    private List <Double> saw_rms;
    private List <Double> saw_rms2;
    private List <Double> saw_lengths;
    private List <Double> rms_runs;
    private List <Double> rms_runs2;
    private List <Integer> xAxis;
    private List <Double> yrmsAxis1;
    private List <Double> yrmsAxis2;
    private List <Double> yexpdAxis;
    private List <Double> ylenAxis;
    private List <Integer> xhistAxis;
    private List <Double> yhistAxis;

    /**
     * Initiating class
     */
    ExecSAW(String language) {
        super();
        this.setLanguage(language);
    }

    void setSawClick(File folder, String executable, @NotNull Button execNappi, SceneRealTimeSaw sawScene,
                     Button plotSAW, Button closeNappiSAW, Button menuNappiSAW, Button helpNappiSAW) {

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

                if ( !sawScene.isRunning())
                    return;

                sawScene.refresh(folder, executable, getFirst(), getSawLengths(),
                    getSawRms(), getSawRms2(), getRmsRuns(), getRmsRuns2(), getXAxis(), getYrmsAxis1(),
                    getYrmsAxis2(), getYexpdAxis(), getXhistAxis(), getYlenAxis());

                if (getFirst()) setFirst(false);

                this.prevTime = currentNanoTime;
            }
        }.start();

        execNappi.setOnMouseClicked((MouseEvent event) -> {
            if (sawScene.isRunning()) {
                sawScene.stop();
                sawScene.getDimension().setDisable(false);
                menuNappiSAW.setDisable(false);
                helpNappiSAW.setDisable(false);
                closeNappiSAW.setDisable(false);
                plotSAW.setDisable(false);
                execNappi.setText(this.getLanguage().equals("fin") ? "UUSI AJO" : "NEW RUN");
            } else {
                this.setVars(sawScene.getVars());
                sawScene.setSave("-");
                boolean fail = false;

                int dim = parseInt(this.getVars()[4]);

                if (dim < 2 || dim > 3) fail = true;

                if (fail) return;

                if (sawScene.getFxplot() != null) {
                    if (sawScene.getFxplot().getFrame().isShowing()
                        || sawScene.getFxplot().getFrame().isActive()
                        || sawScene.getFxplot().getFrame().isDisplayable())
                        sawScene.getFxplot().getFrame().dispose();
                }

                sawScene.setFxplot(new FXPlot());
                sawScene.getFxplot().setFXPlot(this.getLanguage(),"saw");

                this.setSawRms(new ArrayList<>());
                this.setSawRms2(new ArrayList<>());

                this.setRmsRuns(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns().add(0.0);
                this.setRmsRuns2(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getRmsRuns2().add(0.0);

                this.setSawLengths(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getSawLengths().add(0.0);

                this.setXAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getXAxis().add(x);

                this.setYrmsAxis1(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getYrmsAxis1().add(0.0);
                this.setYrmsAxis2(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getYrmsAxis2().add(0.0);
                this.setYexpdAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getYexpdAxis().add(0.0);
                this.setXhistAxis(new ArrayList<>());
                int histsize;
                histsize = dim == 2 ? 10 : 20;
                for (int x = 0; x < histsize; x++) this.getXhistAxis().add(x);
                this.setYhistAxis(new ArrayList<>());
                for (int x = 0; x < histsize; x++) this.getYhistAxis().add(0.0);
                this.setYlenAxis(new ArrayList<>());
                for (int x = 0; x < 10; x++) this.getYlenAxis().add(0.0);

                this.setFirst(true);

                Map<Object,Object> labelMap = new HashMap<>();
                labelMap.put(0,"10");
                labelMap.put(1,"20");
                labelMap.put(2,"30");
                labelMap.put(3,"40");
                labelMap.put(4,"50");
                labelMap.put(5,"60");
                labelMap.put(6,"70");
                labelMap.put(7,"80");
                labelMap.put(8,"90");
                labelMap.put(9,"100");
                if (dim == 3) {
                    labelMap.put(10, "110");
                    labelMap.put(11, "120");
                    labelMap.put(12, "130");
                    labelMap.put(13, "140");
                    labelMap.put(14, "150");
                    labelMap.put(15, "160");
                    labelMap.put(16, "170");
                    labelMap.put(17, "180");
                    labelMap.put(18, "190");
                    labelMap.put(19, "200");
                }

                sawScene.getFxplot().setS1Data(this.getXAxis(), this.getYrmsAxis1(), dim);
                sawScene.getFxplot().setS2Data(this.getXAxis(), this.getYrmsAxis1());
                sawScene.getFxplot().setS3Data(labelMap, this.getXhistAxis(), this.getYhistAxis());


                sawScene.start();

                execNappi.setText(this.getLanguage().equals("fin") ? "SEIS" : "STOP");
                menuNappiSAW.setDisable(true);
                helpNappiSAW.setDisable(true);
                closeNappiSAW.setDisable(true);
                plotSAW.setDisable(true);
                sawScene.getDimension().setDisable(true);
            }
        });
    }

        void setPlotClick (@NotNull Button plotNappi, SceneRealTimeSaw sawScene, VBox valikkoSAW, String datapath,
            File datafolder, String fexec, String pyexecsaw2d, String pyexecsaw3d, Execution ex){

            plotNappi.setOnMouseClicked((MouseEvent event) -> {
                valikkoSAW.setDisable(true);
                sawScene.setSave("s");
                String[] vars = sawScene.getVars();
                this.setVars(vars);
                Data data = new Data(vars);
                int dim = parseInt(getVars()[4]);
                boolean fail = false;

                if ( dim < 2 || dim > 3 ) fail = true;
                if ( fail ) return;

                ex.executeSAW(datafolder, datapath, fexec, pyexecsaw2d,
                    pyexecsaw3d, valikkoSAW, data, this.getVars());
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
     * @return the saw_lengths
     */
    @Contract(pure = true)
    private List<Double> getSawLengths() { return this.saw_lengths; }

    /**
     * @param saw_lengths the saw_lengths to set
     */
    private void setSawLengths(List<Double> saw_lengths) { this.saw_lengths = saw_lengths; }

    /**
     * @return the saw_rms
     */
    @Contract(pure = true)
    private List <Double> getSawRms() { return this.saw_rms; }

    /**
     * @param saw_rms the saw_rms to set
     */
    private void setSawRms(List<Double> saw_rms) { this.saw_rms = saw_rms; }

    /**
     * @return the saw_rms2
     */
    @Contract(pure = true)
    private List <Double> getSawRms2() { return this.saw_rms2; }

    /**
     * @param saw_rms2 the saw_rms to set
     */
    private void setSawRms2(List<Double> saw_rms2) { this.saw_rms2 = saw_rms2; }

    /**
     * @return the rms_runs
     */
    @Contract(pure = true)
    private List <Double> getRmsRuns() { return this.rms_runs; }

    /**
     * @param rms_runs the rms_runs to set
     */
    private void setRmsRuns(List<Double> rms_runs) { this.rms_runs = rms_runs; }

    /**
     * @return the rms_runs2
     */
    @Contract(pure = true)
    private List <Double> getRmsRuns2() { return this.rms_runs2; }

    /**
     * @param rms_runs2 the rms_runs2 to set
     */
    private void setRmsRuns2(List<Double> rms_runs2) { this.rms_runs2 = rms_runs2; }

    /**
     * @return the xAxis
     */
    @Contract(pure = true)
    private List <Integer> getXAxis() { return this.xAxis; }

    /**
     * @param xAxis the xAxis to set
     */
    private void setXAxis(List<Integer> xAxis) { this.xAxis = xAxis; }

    /**
     * @return the yrmsAxis1
     */
    @Contract(pure = true)
    private List <Double> getYrmsAxis1() { return this.yrmsAxis1; }

    /**
     * @param yrmsAxis1 yrmsAxis1 yAxis to set
     */
    private void setYrmsAxis1(List<Double> yrmsAxis1) { this.yrmsAxis1 = yrmsAxis1; }

    /**
     * @return the yrmsAxis2
     */
    @Contract(pure = true)
    private List <Double> getYrmsAxis2() { return this.yrmsAxis2; }

    /**
     * @param yrmsAxis2 the yrmsAxis2 to set
     */
    private void setYrmsAxis2(List<Double> yrmsAxis2) { this.yrmsAxis2 = yrmsAxis2; }

    /**
     * @return the yexpdAxis
     */
    @Contract(pure = true)
    private List <Double> getYexpdAxis() { return this.yexpdAxis; }

    /**
     * @param yexpdAxis the yexpdAxis to set
     */
    private void setYexpdAxis(List<Double> yexpdAxis) { this.yexpdAxis = yexpdAxis; }

    /**
     * @return the yhistAxis
     */
    @Contract(pure = true)
    private List <Double> getYhistAxis() { return this.yhistAxis; }

    /**
     * @param yhistAxis the yhistAxis to set
     */
    private void setYhistAxis(List<Double> yhistAxis) { this.yhistAxis = yhistAxis; }

    /**
     * @return the ylenAxis
     */
    @Contract(pure = true)
    private List <Double> getYlenAxis() { return this.ylenAxis; }

    /**
     * @param ylenAxis the ylenAxis to set
     */
    private void setYlenAxis(List<Double> ylenAxis) { this.ylenAxis = ylenAxis; }

    /**
     * @param xhistAxis x-axis data array for walk plot to set
     */
    private void setXhistAxis(List<Integer> xhistAxis) { this.xhistAxis = xhistAxis; }

    /**
     * @return x-axis data array for walk plot
     */
    @Contract(pure = true)
    private List<Integer> getXhistAxis() { return this.xhistAxis; }

    /**
     * @return the first
     */
    @Contract(pure = true)
    private boolean getFirst() { return first; }

    /**
     *  the first to set
     */
    private void setFirst(boolean first) { this.first = first; }
}
