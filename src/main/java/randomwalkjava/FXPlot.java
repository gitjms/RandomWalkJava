
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for ORG.KNOWM XCHART creation and handling
 */
class FXPlot extends SceneRealTimeRms {

    private int screenHeight;
    private final XYChart calcChartW;
    private final XYChart calcChartH;
    private final XYChart calcChartDNorm;
    private final XYChart calcChartDLog;
    private final XYChart calcChartE;
    private final XChartPanel<XYChart> chartPanelW;
    private final XChartPanel<XYChart> chartPanelH;
    private final XChartPanel<XYChart> chartPanelDNorm;
    private final XChartPanel<XYChart> chartPanelDLog;
    private final XChartPanel<XYChart> chartPanelE;
    private JFrame frame;
    private NumberFormat formatter;

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
        setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
        this.formatter = new DecimalFormat("#.#E0");

        switch (which) {
            case "Walks&norm":
                this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getHeight())/2.0) );
                this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
                this.getFrame().setTitle("Real Time R_rms");
                this.getFrame().getContentPane().setLayout(new GridLayout(2, 1));
                break;
            case "energy&diffusion":
                this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getEDGHeight())/2.0) );
                this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getEDGHeight()));
                this.getFrame().setTitle("Real Time MMC Diffusion");
                this.getFrame().getContentPane().setLayout(new GridLayout(2, 1));
                break;
        }
        ImageIcon icon = new ImageIcon(FXPlot.class.getResource("/icon64.png"));
        this.getFrame().setIconImage(icon.getImage());
        /*
        * XYCharts
        */
        this.calcChartW = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartH = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartDNorm = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartDLog = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartE = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        /*
        * XChartPanels
        */
        this.chartPanelW = new XChartPanel<>(this.getCalcChartW());
        this.chartPanelH = new XChartPanel<>(this.getCalcChartH());
        this.chartPanelDNorm = new XChartPanel<>(this.getCalcChartDNorm());
        this.chartPanelDLog = new XChartPanel<>(this.getCalcChartDLog());
        this.chartPanelE = new XChartPanel<>(this.getCalcChartE());

        switch (which) {
            case "Walks&norm":
                /*
                 * Upper XYChart: ChartPanelW & calcChartW
                 */
                this.getChartPanelW().setBounds(getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/2);
                this.getChartPanelW().setVisible(true);

                this.getCalcChartW().getStyler().setXAxisTitleVisible(true);
                this.getCalcChartW().getStyler().setYAxisTitleVisible(true);
                this.getCalcChartW().setXAxisTitle("walks");
                this.getCalcChartW().setYAxisTitle("<R_rms>");
                this.getCalcChartW().getStyler().setLegendVisible(true);
                this.getCalcChartW().getStyler().setMarkerSize(0);
                this.getCalcChartW().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartW().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartW().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartW().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartW().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartW().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartW().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartW().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
                this.getCalcChartW().getStyler().setChartTitlePadding(15);
                this.getCalcChartW().getStyler().setToolTipsEnabled(false);

                /*
                 * Lower XYChart: ChartPanelH & calcChartH
                 */
                this.getChartPanelH().setBounds(getMarginSmall(), this.getHeight()/2, this.getWidth(), this.getHeight()/2);
                this.getChartPanelH().setVisible(true);

                this.getCalcChartH().getStyler().setLegendVisible(false);
                this.getCalcChartH().getStyler().setMarkerSize(0);
                this.getCalcChartH().getStyler().setXAxisDecimalPattern("0.0");
                this.getCalcChartH().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartH().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartH().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartH().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartH().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartH().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartH().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartH().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartH().getStyler().setChartTitlePadding(15);
                this.getCalcChartH().getStyler().setYAxisMin(0.0);
                this.getCalcChartH().getStyler().setYAxisMax(10.0);
                this.getCalcChartH().getStyler().setToolTipsEnabled(true);
                break;
            case "energy&diffusion":
                /*
                 * Upper XYChart: ChartPanelE &  calcChartE
                 */
                this.getChartPanelE().setBounds(getMarginSmall(), getMarginTiny(), this.getWidth(), this.getEDGHeight()/2);
                this.getChartPanelE().setVisible(true);

                this.getCalcChartE().getStyler().setLegendVisible(false);
                this.getCalcChartE().setXAxisTitle("steps");
                this.getCalcChartE().setYAxisTitle("Energy [eV]");
                this.getCalcChartE().getStyler().setMarkerSize(0);
                this.getCalcChartE().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartE().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartE().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartE().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartE().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartE().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartE().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartE().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartE().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartE().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartE().getStyler().setChartTitlePadding(15);
                this.getCalcChartE().getStyler().setXAxisMin(0.0);
                this.getCalcChartE().getStyler().setYAxisMin(0.0);
                this.getCalcChartE().getStyler().setToolTipsEnabled(true);
                /*
                 * Lower XYChart: ChartPanelDNorm & calcChartDNorm / ChartPanelDLog & calcChartDLog
                 */
                this.getChartPanelDNorm().setBounds(getMarginSmall(), this.getEDGHeight()/2, this.getWidth(), this.getEDGHeight()/2);
                this.getChartPanelDNorm().setVisible(true);
                this.getChartPanelDLog().setBounds(getMarginSmall(), this.getEDGHeight()/2, this.getWidth(), this.getEDGHeight()/2);
                this.getChartPanelDLog().setVisible(true);

                this.getCalcChartDNorm().getStyler().setLegendVisible(false);
                this.getCalcChartDNorm().setXAxisTitle("Time, t [ns]");
                this.getCalcChartDNorm().setYAxisTitle("Diffusion Coefficient, D [cm^2/s]");
                this.getCalcChartDNorm().getStyler().setMarkerSize(0);
                this.getCalcChartDNorm().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartDNorm().getStyler().setYAxisDecimalPattern("#.#E00");
                this.getCalcChartDNorm().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartDNorm().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartDNorm().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartDNorm().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartDNorm().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartDNorm().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartDNorm().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartDNorm().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartDNorm().getStyler().setChartTitlePadding(15);
                this.getCalcChartDNorm().getStyler().setToolTipsEnabled(true);

                this.getCalcChartDLog().getStyler().setLegendVisible(false);
                this.getCalcChartDLog().setXAxisTitle("Time, t [ns]");
                this.getCalcChartDLog().setYAxisTitle("Diffusion Coefficient, D [cm^2/s]");
                this.getCalcChartDLog().getStyler().setMarkerSize(0);
                this.getCalcChartDLog().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartDLog().getStyler().setYAxisDecimalPattern("#.#E00");
                this.getCalcChartDLog().getStyler().setYAxisLogarithmic(true);
                this.getCalcChartDLog().getStyler().setPlotGridHorizontalLinesVisible(false);
                this.getCalcChartDLog().getStyler().setYAxisLogarithmicDecadeOnly(true);
                this.getCalcChartDLog().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartDLog().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartDLog().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartDLog().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartDLog().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartDLog().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartDLog().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartDLog().getStyler().setChartTitlePadding(15);
                this.getCalcChartDLog().getStyler().setToolTipsEnabled(true);
                break;
        }
    }

    /**
     * method for plotting R_rms and sqrt(steps) vs. walks in Real Time Rms
     * plot has two data lines: "R_rms" and "sqrt(steps)"
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
        this.getCalcChartW().addSeries("sqrt(steps)", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.blue);

        this.getCalcChartW().setTitle("R_rms and sqrt(steps) vs. walks, "+"sqrt(steps) = "+String.format("%.2f",expected));
        this.getCalcChartW().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelW(),0);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting normal distribution in Real Time Rms
     * @param x x-axis data (rms_norm)
     * @param y y-axis data (rms_norm)
     * @param minx x-axis min for normal distribution plot
     * @param maxx x-axis max for normal distribution plot
     * @param standdiff "stand" if standard normal distribution, "diff" if diffusion normal distribution
     */
    void setHData(double[] x, double[] y, double minx, double maxx, @NotNull String standdiff) {
        this.getCalcChartH().getSeriesMap().clear();
        this.getChartPanelH().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelH());
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f ),
            new BasicStroke( 1.0f, CAP_ROUND,
                JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartH().addSeries("norm", x, y).setLineStyle(BasicStroke[0]).setLineColor(Color.orange);
        this.getCalcChartH().addSeries("diff", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.RED);
        this.getCalcChartH().addSeries("1", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(150.0f / 256.0f, 1f, 1f));
        this.getCalcChartH().addSeries("2", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(170.0f / 256.0f, 1f, 1f));
        this.getCalcChartH().addSeries("3", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(190.0f / 256.0f, 1f, 1f));
        this.getCalcChartH().addSeries("4", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(210.0f / 256.0f, 1f, 1f));
        this.getCalcChartH().addSeries("5", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(230.0f / 256.0f, 1f, 1f));

        switch (standdiff) {
            case "stand":
                this.getCalcChartH().setTitle("R_rms standard normal distribution");
                this.getCalcChartH().setXAxisTitle("<R_rms>");
                this.getCalcChartH().getStyler().setYAxisMax(1.0);
                break;
            case "diff":
                this.getCalcChartH().setTitle("Diffusion normal distribution");
                this.getCalcChartH().setXAxisTitle("<r>");
                this.getCalcChartH().getStyler().setYAxisMax(1.0);
                break;
        }
        this.getCalcChartH().getStyler().setXAxisMin(minx);
        this.getCalcChartH().getStyler().setXAxisMax(maxx);
        this.getCalcChartH().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelH(),1);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting energy minimization in MMC
     * @param x x-axis data (energy_x)
     * @param y y-axis data (energy_y)
     */
    void setEData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartE().addSeries("energy", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.MAGENTA);
        this.getCalcChartE().setTitle("MMC Energy Minimizing");
        this.getCalcChartE().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelE(),0);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting diffusion in MMC Diffusion (normal y-axis)
     * @param x x-axis data
     * @param y y-axis data
     */
    void setDNormData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartDNorm().addSeries("diffusion", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartDNorm().setTitle("MMC Diffusion Calculation");
        this.getCalcChartDNorm().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelDNorm(),1);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for plotting diffusion in MMC Diffusion (logarithmic y-axis)
     * @param x x-axis data
     * @param y y-axis data
     */
    void setDLogData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke(1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f)
        };
        this.getCalcChartDLog().addSeries("diffusion", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartDLog().setTitle("MMC Diffusion Calculation (logarithmic)");
        this.getCalcChartDLog().getStyler().setAntiAlias(true);
        this.getFrame().getContentPane().add(this.getChartPanelDLog(), 1);
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating R_rms and sqrt(steps) vs. walks in Real Time Rms
     * @param name name for "R_rms" or "sqrt(steps)"
     * @param x x-axis data (xAxis)
     * @param y y-axis data (yAxis or y2Axis)
     */
    void updateWData(String name, double[] x, double[] y) {
        this.getCalcChartW().updateXYSeries(name, x, y, null);
        this.getFrame().revalidate();
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
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating normal distribution in Real Time Rms
     * @param name name for plot series
     * @param x x-axis data (xnormAxis)
     * @param y y-axis data (ynormAxis)
     */
    void updateHDiffData(String name, double[] x, double[] y) {
        this.getCalcChartH().updateXYSeries(name, x, y, null);
        this.getFrame().revalidate();
        this.getFrame().pack();
    }

    /**
     * method for updating energy minimization in MMC
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateEData(List<Double> x, List<Double> y) {
        this.getCalcChartE().updateXYSeries("energy", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating diffusion in Real Time Rms (normal y-axis)
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateDNormData(List<Double> x, List<Double> y) {
        this.getCalcChartDNorm().updateXYSeries("diffusion", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating diffusion in Real Time Rms (logarithmic)
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateDLogData(List<Double> x, List<Double> y) {
        this.getCalcChartDLog().updateXYSeries("diffusion", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    void setInitT(double evalue) {
        this.getCalcChartE().setTitle("MMC Diffusion Calculation, T_init="+this.formatter.format(evalue)+" K");
    }

    void setFinT(double evalue) {
        this.getCalcChartE().setTitle(this.getCalcChartE().getTitle()+", T_fin="+this.formatter.format(evalue)+" K");
    }

    /**
     * @param minY the y-axis min value to set
     */
    void setWMinY(double minY) {
        this.getCalcChartW().getStyler().setYAxisMin(minY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setWMaxY(double maxY) {
        this.getCalcChartW().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setHDiffMaxY(double maxY) {
        this.getCalcChartH().getStyler().setYAxisMax(maxY);
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
     * @param minY the y-axis min value to set
     */
    void setDNormMinY(double minY) {
        this.getCalcChartDNorm().getStyler().setYAxisMin(minY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setDNormMaxY(double maxY) {
        this.getCalcChartDNorm().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param minY the y-axis min value to set
     */
    void setDLogMinY(double minY) {
        this.getCalcChartDLog().getStyler().setYAxisMin(minY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setDLogMaxY(double maxY) {
        this.getCalcChartDLog().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param coeff the max diffusion coefficient value to set to chart title
     */
    void setDNormTitle(double coeff) {
        this.getCalcChartDNorm().setTitle("Diffusion Coefficient, D_max = "+this.formatter.format(coeff)+" cm^2/s"); }

    /**
     * @param coeff the max diffusion coefficient value to set to chart title
     */
    void setDLogTitle(double coeff) {
        this.getCalcChartDLog().setTitle("Diffusion Coefficient, D_max = "+this.formatter.format(coeff)+" cm^2/s"); }

    /**
     */
    void setFrameVis() { this.getFrame().setVisible(true); }

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
    JFrame getFrame() { return frame; }

    /**
     * @return the Width
     */
    @Contract(pure = true)
    private int getWidth() { return 700 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the Height
     */
    @Contract(pure = true)
    private int getHeight() { return 800 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the Height
     */
    @Contract(pure = true)
    private int getEDGHeight() { return 1000 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginTiny
     */
    @Contract(pure = true)
    private int getMarginTiny() { return 10 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginSmall
     */
    @Contract(pure = true)
    private int getMarginSmall() { return 50 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the screenHeight
     */
    @Contract(pure = true)
    private int getScreenHeight() { return screenHeight; }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(int screenHeight) { this.screenHeight = screenHeight; }

    /**
     * @return the calcChartW
     */
    @Contract(pure = true)
    private XYChart getCalcChartW() { return calcChartW; }

    /**
     * @return the calcChartH
     */
    @Contract(pure = true)
    private XYChart getCalcChartH() { return calcChartH; }

    /**
     * @return the calcChartDNorm
     */
    @Contract(pure = true)
    private XYChart getCalcChartDNorm() { return calcChartDNorm; }

    /**
     * @return the calcChartDLog
     */
    @Contract(pure = true)
    private XYChart getCalcChartDLog() { return calcChartDLog; }

    /**
     * @return the calcChartE
     */
    @Contract(pure = true)
    private XYChart getCalcChartE() { return calcChartE; }

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
     * @return the chartPanelDNorm
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelDNorm() { return chartPanelDNorm; }

    /**
     * @return the chartPanelDLog
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelDLog() { return chartPanelDLog; }

    /**
     * @return the chartPanelE
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelE() { return chartPanelE; }


}
