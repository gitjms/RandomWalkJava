
package randomwalkjava;

import com.sun.glass.ui.Screen;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for ORG.KNOWM XCHART creation and handling
 */
class FXPlot extends SceneRealTimeRms {

    private final XYChart calcChartW;
    private final XYChart calcChartH;
    private final XYChart calcChartE;
    private final XChartPanel<XYChart> chartPanelW;
    private final XChartPanel<XYChart> chartPanelH;
    private final XChartPanel<XYChart> chartPanelE;
    private JFrame frame;

    /**
     * method for creating a plotting element
     * @param which Real Time plots and MMC plot
     */
    FXPlot(@NotNull String which) {
        /*
        * JFrame
        */
        this.setFrame(new JFrame());
        this.getFrame().setBackground(Color.white);
        this.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        if (which.equals("Walks&norm")) {
            this.getFrame().setLocation(0, this.getYMarginTiny());
            this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getWalkNormHeight()));
            this.getFrame().setTitle("Real Time R_rms");
            this.getFrame().getContentPane().setLayout(new GridLayout(2,1));
        } else if (which.equals("E")) {
            this.getFrame().setLocation(0, this.getYMarginSmall());
            this.getFrame().setPreferredSize(new Dimension(this.getWidth(),this.getHeight()));
            this.getFrame().setTitle("Real Time MMC");
        }
        ImageIcon icon = new ImageIcon(FXPlot.class.getResource("/icon64.png"));
        this.getFrame().setIconImage(icon.getImage());
        /*
        * XYCharts
        */
        this.calcChartW = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartH = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartE = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        /*
        * XChartPanels
        */
        this.chartPanelW = new XChartPanel<>(this.getCalcChartW());
        this.chartPanelH = new XChartPanel<>(this.getCalcChartH());
        this.chartPanelE = new XChartPanel<>(this.getCalcChartE());

        if (which.equals("Walks&norm")) {
            this.getChartPanelW().setBounds(0, this.getYMarginTiny(), this.getWidth(), this.getHeight()/2);
            this.getChartPanelW().setVisible(true);

            this.getChartPanelH().setBounds(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);
            this.getChartPanelH().setVisible(true);
            /*
            * Upper XYChart calcChartW
            */
            this.getCalcChartW().getStyler().setXAxisTitleVisible(true);
            this.getCalcChartW().getStyler().setYAxisTitleVisible(true);
            this.getCalcChartW().setXAxisTitle("walks");
            this.getCalcChartW().setYAxisTitle("<R_rms>");
            this.getCalcChartW().getStyler().setLegendVisible(true);
            this.getCalcChartW().getStyler().setMarkerSize(0);
            this.getCalcChartW().getStyler().setXAxisDecimalPattern("0");
            this.getCalcChartW().getStyler().setYAxisDecimalPattern("0.0");
            this.getCalcChartW().getStyler().setAxisTickLabelsFont(new java.awt.Font(null, Font.PLAIN,15));
            this.getCalcChartW().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.getCalcChartW().getStyler().setChartTitleFont(new java.awt.Font(null, Font.PLAIN,20));
            this.getCalcChartW().getStyler().setAxisTitleFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartW().getStyler().setLegendFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartW().getStyler().setChartTitlePadding(15);
            this.getCalcChartW().getStyler().setToolTipsEnabled(false);
            /*
            * Lower XYChart calcChartH
            */
            this.getCalcChartH().getStyler().setLegendVisible(false);
            this.getCalcChartH().setXAxisTitle("<R_rms>");
            this.getCalcChartH().getStyler().setMarkerSize(0);
            this.getCalcChartH().getStyler().setXAxisDecimalPattern("0.0");
            this.getCalcChartH().getStyler().setYAxisDecimalPattern("0.0");
            this.getCalcChartH().getStyler().setAxisTickLabelsFont(new java.awt.Font(null, Font.PLAIN,15));
            this.getCalcChartH().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.getCalcChartH().getStyler().setChartTitleFont(new java.awt.Font(null, Font.PLAIN,20));
            this.getCalcChartH().getStyler().setAxisTitleFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartH().getStyler().setLegendFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartH().getStyler().setChartTitlePadding(15);
            this.getCalcChartH().getStyler().setYAxisMin(0.0);
            this.getCalcChartH().getStyler().setYAxisMax(10.0);
            this.getCalcChartH().getStyler().setToolTipsEnabled(true);

        } else if (which.equals("E")) {
            this.getChartPanelE().setBounds(0, 0, this.getWidth(), this.getHeight()-this.getYMarginBig());
            this.getChartPanelE().setVisible(true);
            /*
            * XYChart calcChartE
            */
            this.getCalcChartE().getStyler().setLegendVisible(false);
            this.getCalcChartE().setXAxisTitle("steps");
            this.getCalcChartE().setYAxisTitle("Arbitrary Energy Unit");
            this.getCalcChartE().getStyler().setMarkerSize(0);
            this.getCalcChartE().getStyler().setXAxisDecimalPattern("0");
            this.getCalcChartE().getStyler().setYAxisDecimalPattern("0.0");
            this.getCalcChartE().getStyler().setAxisTickLabelsFont(new java.awt.Font(null, Font.PLAIN,15));
            this.getCalcChartE().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.getCalcChartE().getStyler().setChartTitleFont(new java.awt.Font(null, Font.PLAIN,20));
            this.getCalcChartE().getStyler().setAxisTitleFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartE().getStyler().setLegendFont(new java.awt.Font(null, Font.PLAIN,18));
            this.getCalcChartE().getStyler().setChartTitlePadding(15);
            this.getCalcChartE().getStyler().setXAxisMin(0.0);
            this.getCalcChartE().getStyler().setYAxisMin(0.0);
            this.getCalcChartE().getStyler().setToolTipsEnabled(true);
        }
    }

    /**
     * method for plotting R_rms and sqrt(N) vs. walks in Real Time Rms
     * plot has two data lines: "R_rms" and "sqrt(N)"
     * @param x x-axis data (rms_runs)
     * @param y y-axis data (rms_runs)
     * @param expected value of Math.sqrt((double) steps)
     */
    void setWData(double[] x, double[] y, double expected) {
        this.getCalcChartW().getSeriesMap().clear();
        this.getChartPanelW().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelW());
        BasicStroke[] BasicStroke = new BasicStroke[]{
                new BasicStroke( 1.5f, CAP_SQUARE,
            		JOIN_MITER, 10.0f, null, 0.0f ),
                new BasicStroke( 1.5f, CAP_SQUARE,
        			JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f )
        };
        this.getCalcChartW().addSeries("R_rms", x, y).setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartW().addSeries("sqrt(N)", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.blue);

        this.getCalcChartW().setTitle("R_rms and sqrt(N) vs. walks, "+"sqrt(N) = "+String.format("%.2f",expected));
        this.getCalcChartW().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelW());
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting normal distribution in Real Time Rms
     * @param x x-axis data (rms_norm)
     * @param y y-axis data (rms_norm)
     * @param minx x-axis min for normal distribution plot
     * @param maxx x-axis max for normal distribution plot
     * @param standnorm true if standard normal distribution, false otherwise
     */
    void setHData(double[] x, double[] y, double minx, double maxx, boolean standnorm) {
        this.getCalcChartH().getSeriesMap().clear();
        this.getChartPanelH().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelH());
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f ),
        };
        this.getCalcChartH().addSeries("norm", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.orange);

        if (standnorm) {
            this.getCalcChartH().setTitle("R_rms standard normal distribution");
        } else {
            this.getCalcChartH().setTitle("R_rms normal distribution");
        }
        this.getCalcChartH().getStyler().setXAxisMin(minx);
        this.getCalcChartH().getStyler().setXAxisMax(maxx);
        this.getCalcChartH().getStyler().setYAxisMax( 1.05 );
        this.getCalcChartH().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelH());
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting energy minimization in MMC
     * @param x x-axis data (energy_x)
     * @param y y-axis data (energy_y)
     */
    void setEData(List<Double> x, List<Double> y) {
        this.getCalcChartE().getSeriesMap().clear();
        this.getChartPanelE().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelE());
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f ),
        };
        this.getCalcChartE().addSeries("energy", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.MAGENTA);
        this.getCalcChartE().setTitle("MMC Diffusion Random Walk");
        this.getCalcChartE().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelE());
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating R_rms and sqrt(N) vs. walks in Real Time Rms
     * @param name name for "R_rms" or "sqrt(N)"
     * @param x x-axis data (xAxis)
     * @param y y-axis data (yAxis or y2Axis)
     */
    void updateWData(String name, double[] x, double[] y) {
        this.getCalcChartW().updateXYSeries(name, x, y, null);
        this.getFrame().add(this.getChartPanelW(),0);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating normal distribution in Real Time Rms
     * @param x x-axis data (xnormAxis)
     * @param y y-axis data (ynormAxis)
     */
    void updateHData(double[] x, double[] y) {
        this.getCalcChartH().updateXYSeries("norm", x, y, null);
        this.getCalcChartH().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        this.getCalcChartH().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
        this.getFrame().add(this.getChartPanelH(),1);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating energy minimization in MMC
     * @param x x-axis data (energy_x)
     * @param y y-axis data (energy_y)
     */
    void updateEData(List<Double> x, List<Double> y) {
        this.getCalcChartE().updateXYSeries("energy", x, y, null);
        this.getCalcChartE().getStyler().setToolTipType(Styler.ToolTipType.yLabels);
        this.getCalcChartE().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
        this.getFrame().add(this.getChartPanelE());
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * @param minY the y-axis min value to set
     */
    void setMinY(double minY) {
        this.getCalcChartW().getStyler().setYAxisMin(minY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setMaxY(double maxY) {
        this.getCalcChartW().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param minY the y-axis min value to set
     */
    void setEMinY(double minY) {
        this.getCalcChartE().getStyler().setYAxisMin(minY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setEMaxY(double maxY) {
        this.getCalcChartE().getStyler().setYAxisMax(maxY);
    }

    /**
     */
    void setFrameVis() {
       this.getFrame().setVisible(true);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setHMaxY(double maxY) {
        this.getCalcChartH().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param frame the frame to set
     */
    private void setFrame(JFrame frame) {
        this.frame = frame;
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * @return the frame
     */
    JFrame getFrame() {
        return frame;
    }

    /**
     * @return the width
     */
    @Contract(pure = true)
    private int getWidth() { return 600 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the height
     */
    @Contract(pure = true)
    private int getHeight() { return 600 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the height
     */
    @Contract(pure = true)
    private int getWalkNormHeight() { return 800 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginTiny
     */
    @Contract(pure = true)
    private int getYMarginTiny() { return 10 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginSmall
     */
    @Contract(pure = true)
    private int getYMarginSmall() { return 50 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginBig
     */
    @Contract(pure = true)
    private int getYMarginBig() { return 100 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the calcChartW
     */
    @Contract(pure = true)
    private XYChart getCalcChartW() {
        return calcChartW;
    }

    /**
     * @return the calcChartH
     */
    @Contract(pure = true)
    private XYChart getCalcChartH() {
        return calcChartH;
    }

    /**
     * @return the calcChartE
     */
    @Contract(pure = true)
    private XYChart getCalcChartE() {
        return calcChartE;
    }

    /**
     * @return the chartPanelW
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelW() { return chartPanelW; }

    /**
     * @return the chartPanelH
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelH() { return chartPanelH; }

    /**
     * @return the chartPanelE
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelE() { return chartPanelE; }


}
