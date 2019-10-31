
package randomwalkjava;

import com.sun.glass.ui.Screen;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.*;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for ORG.KNOWM XCHART creation and handling
 */
class FXPlot {

    private String language;
    private int screenHeight;
    private XYChart calcChartW;
    private XYChart calcChartN;
    private XYChart calcChartH;
    private XYChart calcChartE;
    private XYChart calcChartD;
    private XYChart calcChartV;
    private XYChart calcChartS1;
    private XYChart calcChartS2;
    private CategoryChart calcChartS3;
    private XChartPanel<XYChart> chartPanelW;
    private XChartPanel<XYChart> chartPanelN;
    private XChartPanel<XYChart> chartPanelH;
    private XChartPanel<XYChart> chartPanelE;
    private XChartPanel<XYChart> chartPanelD;
    private XChartPanel<XYChart> chartPanelV;
    private XChartPanel<XYChart> chartPanelS1;
    private XChartPanel<XYChart> chartPanelS2;
    private XChartPanel<CategoryChart> chartPanelS3;
    private JFrame frame;
    private NumberFormat formatter;
    private NumberFormat diffformatter;
    private NumberFormat muformatter;

    /**
     * method for creating a plotting element
     * @param language GUI language
     * @param which which graph
     */
    void setFXPlot(String language, @NotNull String which) {
        this.setLanguage(language);
        this.setFrame(new JFrame());
        this.getFrame().setBackground(Color.white);
        this.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);

        this.formatter = new DecimalFormat("#.#E0");
        this.diffformatter = new DecimalFormat("0.0");
        this.muformatter = new DecimalFormat("0.0000");

        switch (which) {
            case "Walks&norm":
                this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getHeight())/2.0) );
                this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
                this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Reaaliaika-RMS" : "Real Time RMS");
                this.getFrame().getContentPane().setLayout(new GridLayout(3, 1));
                break;
            case "energy&diffusion":
                this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getHeight())/2.0) );
                this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
                this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Reaaliaika-diffuusio" : "Real Time Diffusion");
                this.getFrame().getContentPane().setLayout(new GridLayout(3, 1));
                break;
            case "saw":
                this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getHeight())/2.0) );
                this.getFrame().setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
                this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Reaaliaika-SAW" : "Real Time SAW");
                this.getFrame().getContentPane().setLayout(new GridLayout(3, 1));
                break;
        }
        ImageIcon icon = new ImageIcon(FXPlot.class.getResource("/icon64.png"));
        this.getFrame().setIconImage(icon.getImage());
        /*
        * XYCharts
        */
        this.calcChartW = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartN = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartH = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartE = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartD = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartV = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartS1 = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartS2 = new XYChartBuilder().theme(ChartTheme.Matlab).build();
        this.calcChartS3 = new CategoryChartBuilder().theme(ChartTheme.Matlab).build();
        /*
        * XChartPanels
        */
        this.chartPanelW = new XChartPanel<>(this.getCalcChartW());
        this.chartPanelN = new XChartPanel<>(this.getCalcChartN());
        this.chartPanelH = new XChartPanel<>(this.getCalcChartH());
        this.chartPanelE = new XChartPanel<>(this.getCalcChartE());
        this.chartPanelD = new XChartPanel<>(this.getCalcChartD());
        this.chartPanelV = new XChartPanel<>(this.getCalcChartV());
        this.chartPanelS1 = new XChartPanel<>(this.getCalcChartS1());
        this.chartPanelS2 = new XChartPanel<>(this.getCalcChartS2());
        this.chartPanelS3 = new XChartPanel<>(this.getCalcChartS3());
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f )
        };

        switch (which) {
            case "Walks&norm":
                /*
                 * XYCharst: ChartPanel(W,N,H) & calcChart(W,N,H)
                 */
                this.getChartPanelW().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelW().setVisible(true);
                this.getChartPanelN().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelN().setVisible(true);
                this.getChartPanelH().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelH().setVisible(true);

                this.getCalcChartW().getStyler().setXAxisTitleVisible(true);
                this.getCalcChartW().getStyler().setYAxisTitleVisible(true);
                this.getCalcChartW().setXAxisTitle(this.getLanguage().equals("fin") ? "Ajot" : "Walks");
                this.getCalcChartW().setYAxisTitle("Rrms");
                this.getCalcChartW().getStyler().setLegendVisible(true);
                this.getCalcChartW().getStyler().setMarkerSize(0);
                this.getCalcChartW().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartW().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartW().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartW().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartW().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartW().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartW().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartW().getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
                this.getCalcChartW().getStyler().setChartTitlePadding(15);
                this.getCalcChartW().getStyler().setAntiAlias(true);
                this.getCalcChartW().getStyler().setToolTipsEnabled(false);

                this.getCalcChartN().getStyler().setLegendVisible(true);
                this.getCalcChartN().getStyler().setMarkerSize(0);
                this.getCalcChartN().getStyler().setXAxisDecimalPattern("0.0");
                this.getCalcChartN().getStyler().setYAxisDecimalPattern("0.00");
                this.getCalcChartN().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartN().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartN().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartN().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartN().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartN().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartN().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartN().getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
                this.getCalcChartN().getStyler().setChartTitlePadding(15);
                this.getCalcChartN().getStyler().setAntiAlias(true);
                this.getCalcChartN().getStyler().setToolTipsEnabled(true);

                this.getCalcChartH().getStyler().setMarkerSize(0);
                this.getCalcChartH().setXAxisTitle(this.getLanguage().equals("fin") ? "Etäisyys" : "Distance");
                this.getCalcChartH().setYAxisTitle(this.getLanguage().equals("fin") ? "Lukumäärä" : "Frequency");
                this.getCalcChartH().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartH().getStyler().setYAxisDecimalPattern("0");
                this.getCalcChartH().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartH().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartH().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartH().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartH().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartH().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartH().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.StepArea);
                this.getCalcChartH().getStyler().setSeriesLines(BasicStroke);
                this.getCalcChartH().getStyler().setChartTitlePadding(15);
                this.getCalcChartH().getStyler().setYAxisMin(0.0);
                this.getCalcChartH().getStyler().setLegendVisible(false);
                this.getCalcChartH().getStyler().setAntiAlias(true);
                this.getCalcChartH().getStyler().setToolTipsEnabled(true);
                break;
            case "energy&diffusion":
                /*
                 * XYCharts: ChartPanel(E,D,V) &  calcChart(E,D,V)
                 */
                this.getChartPanelE().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelE().setVisible(true);
                this.getChartPanelD().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelD().setVisible(true);
                this.getChartPanelV().setBounds(this.getMarginSmall(), this.getMarginTiny(), this.getWidth(), this.getHeight()/3);
                this.getChartPanelV().setVisible(true);

                this.getCalcChartE().getStyler().setLegendVisible(false);
                this.getCalcChartE().setXAxisTitle(this.getLanguage().equals("fin") ? "Askeleet" : "Steps");
                this.getCalcChartE().setYAxisTitle(this.getLanguage().equals("fin") ? "Energia [eV]" : "Energy [eV]");
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
                this.getCalcChartE().getStyler().setAntiAlias(true);
                this.getCalcChartE().getStyler().setXAxisMin(0.0);
                this.getCalcChartE().getStyler().setYAxisMin(0.0);
                this.getCalcChartE().getStyler().setToolTipsEnabled(true);

                this.getCalcChartD().getStyler().setLegendVisible(false);
                this.getCalcChartD().setXAxisTitle(this.getLanguage().equals("fin") ? "Aika, t [ps]" : "Time, t [ps]");
                this.getCalcChartD().setYAxisTitle(this.getLanguage().equals("fin")
                    ? "Diffuusiokerroin, D [1e-7 cm^2/s]" : "Diffusion Coefficient, D [1e-7 cm^2/s]");
                this.getCalcChartD().getStyler().setMarkerSize(0);
                this.getCalcChartD().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartD().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartD().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartD().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartD().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartD().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartD().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartD().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartD().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartD().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartD().getStyler().setChartTitlePadding(15);
                this.getCalcChartD().getStyler().setAntiAlias(true);
                this.getCalcChartD().getStyler().setYAxisMin(0.0);
                this.getCalcChartD().getStyler().setToolTipsEnabled(true);

                this.getCalcChartV().getStyler().setLegendVisible(false); // \u03BD=nu
                this.getCalcChartV().setXAxisTitle(this.getLanguage().equals("fin") ? "Aika, t [ps]" : "Time, t [ps]");
                this.getCalcChartV().setYAxisTitle(this.getLanguage().equals("fin") ? "Viskositeetti, \u03BD [\u00B5Pa s]" : "Viscosity, \u03BD [\u00B5Pa s]");
                this.getCalcChartV().getStyler().setMarkerSize(0);
                this.getCalcChartV().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartV().getStyler().setYAxisDecimalPattern("0.0");
                this.getCalcChartV().getStyler().setPlotGridHorizontalLinesVisible(false);
                this.getCalcChartV().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartV().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartV().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartV().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartV().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartV().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartV().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartV().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartV().getStyler().setChartTitlePadding(15);
                this.getCalcChartV().getStyler().setAntiAlias(true);
                this.getCalcChartV().getStyler().setYAxisMin(0.0);
                this.getCalcChartV().getStyler().setToolTipsEnabled(true);
                break;
            case "saw":
                /*
                 * XYCharts: ChartPanelS(1,2,2) & calcChartS(1,2,3)
                 */
                this.getChartPanelS1().setBounds(this.getMarginSmall(), 0, this.getWidth(), this.getHeight()/3);
                this.getChartPanelS1().setVisible(true);
                this.getChartPanelS2().setBounds(this.getMarginSmall(), 0, this.getWidth(), this.getHeight()/3);
                this.getChartPanelS2().setVisible(true);
                this.getChartPanelS3().setBounds(this.getMarginSmall(), 0, this.getWidth(), this.getHeight()/3);
                this.getChartPanelS3().setVisible(true);

                this.getCalcChartS1().getStyler().setLegendVisible(true);
                this.getCalcChartS1().setXAxisTitle(this.getLanguage().equals("fin") ? "Ajot, Cn" : "Walks, Cn");
                this.getCalcChartS1().setYAxisTitle(this.getLanguage().equals("fin") ? "Etäisyys, R" : "Distance, R");
                this.getCalcChartS1().getStyler().setMarkerSize(0);
                this.getCalcChartS1().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartS1().getStyler().setYAxisDecimalPattern("0");
                this.getCalcChartS1().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartS1().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartS1().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartS1().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartS1().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartS1().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartS1().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS1().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS1().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS1().getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
                this.getCalcChartS1().getStyler().setLegendLayout(Styler.LegendLayout.Horizontal);
                this.getCalcChartS1().getStyler().setLegendPadding(10);
                this.getCalcChartS1().getStyler().setLegendSeriesLineLength(30);
                this.getCalcChartS1().getStyler().setChartTitlePadding(15);
                this.getCalcChartS1().getStyler().setAntiAlias(true);
                this.getCalcChartS1().getStyler().setXAxisMin(0.0);
                this.getCalcChartS1().getStyler().setXAxisMax(9.0);
                this.getCalcChartS1().getStyler().setYAxisMin(0.0);
                this.getCalcChartS1().getStyler().setYAxisMax(10.0);
                this.getCalcChartS1().getStyler().setToolTipsEnabled(true);

                this.getCalcChartS2().getStyler().setLegendVisible(false);
                this.getCalcChartS2().setXAxisTitle(this.getLanguage().equals("fin") ? "Ajot, Cn" : "Walks, Cn");
                this.getCalcChartS2().setYAxisTitle(this.getLanguage().equals("fin") ? "Etäisyys, R" : "Distance, R");
                this.getCalcChartS2().getStyler().setMarkerSize(0);
                this.getCalcChartS2().getStyler().setXAxisDecimalPattern("0");
                this.getCalcChartS2().getStyler().setYAxisDecimalPattern("0");
                this.getCalcChartS2().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartS2().getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
                this.getCalcChartS2().getStyler().setToolTipFont(new java.awt.Font(null, Font.PLAIN,18));
                this.getCalcChartS2().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartS2().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
                this.getCalcChartS2().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartS2().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS2().getStyler().setLegendFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS2().getStyler().setChartTitlePadding(15);
                this.getCalcChartS2().getStyler().setAntiAlias(true);
                this.getCalcChartS2().getStyler().setXAxisMin(0.0);
                this.getCalcChartS2().getStyler().setXAxisMax(9.0);
                this.getCalcChartS2().getStyler().setYAxisMin(0.0);
                this.getCalcChartS2().getStyler().setYAxisMax(10.0);
                this.getCalcChartS2().getStyler().setToolTipsEnabled(true);

                this.getCalcChartS3().getStyler().setMarkerSize(0);
                this.getCalcChartS3().setXAxisTitle(this.getLanguage().equals("fin") ? "Etäisyys" : "Distance");
                this.getCalcChartS3().setYAxisTitle(this.getLanguage().equals("fin") ? "Lukumäärä" : "Frequency");
                this.getCalcChartS3().getStyler().setXAxisDecimalPattern("10");
                this.getCalcChartS3().getStyler().setYAxisDecimalPattern("0");
                this.getCalcChartS3().getStyler().setYAxisLogarithmic(false);
                this.getCalcChartS3().getStyler().setAxisTickLabelsFont(new Font(null, Font.PLAIN, 15));
                this.getCalcChartS3().getStyler().setChartTitleFont(new Font(null, Font.PLAIN, 20));
                this.getCalcChartS3().getStyler().setAxisTitleFont(new Font(null, Font.PLAIN, 18));
                this.getCalcChartS3().getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Bar);
                this.getCalcChartS3().getStyler().setSeriesLines(BasicStroke);
                this.getCalcChartS3().getStyler().setPlotGridHorizontalLinesVisible(true);
                this.getCalcChartS3().getStyler().setPlotGridVerticalLinesVisible(false);
                this.getCalcChartS3().getStyler().setChartTitlePadding(15);
                this.getCalcChartS3().getStyler().setYAxisMin(0.0);
                this.getCalcChartS3().getStyler().setLegendVisible(false);
                this.getCalcChartS3().getStyler().setAntiAlias(true);
                this.getCalcChartS3().getStyler().setToolTipsEnabled(false);
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
                new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f ),
                new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f )
        };
        this.getCalcChartW().addSeries("Rrms", x, y).setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartW().addSeries(this.getLanguage().equals("fin")
            ? "\u221Aaskeleet" : "\u221Asteps", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.blue);

        this.getCalcChartW().setTitle(this.getLanguage().equals("fin")
            ? "R_rms ja odotusarvo (" + String.format("%.2f",expected) + ") ajojen funktiona"
            : "R_rms and Expected Value (" + String.format("%.2f",expected) + ") as Functions of Walks");
        this.getFrame().getContentPane().add(this.getChartPanelW(),0);
    }

    /**
     * method for plotting normal distribution in Real Time Rms
     * @param x x-axis data (rms_norm)
     * @param y y-axis data (rms_norm)
     * @param minx x-axis min for normal distribution plot
     * @param maxx x-axis max for normal distribution plot
     * @param standdiff "stand" if standard normal distribution, "diff" if diffusion normal distribution
     */
    void setNData(double[] x, double[] y, double minx, double maxx, @NotNull String standdiff) {
        this.getCalcChartN().getSeriesMap().clear();
        this.getChartPanelN().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelN());
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.0f, CAP_ROUND, JOIN_MITER, 10.0f, null, 0.0f ),
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartN().addSeries("\u03C1(r,t\u2081)", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.getHSBColor(150.0f / 256.0f, 1f, 1f));
        this.getCalcChartN().addSeries("\u03C1(r,t\u2082)", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.getHSBColor(170.0f / 256.0f, 1f, 1f));
        this.getCalcChartN().addSeries("\u03C1(r,t\u2083)", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.getHSBColor(190.0f / 256.0f, 1f, 1f));
        this.getCalcChartN().addSeries("\u03C1(r,t\u2084)", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.getHSBColor(210.0f / 256.0f, 1f, 1f));
        this.getCalcChartN().addSeries("\u03C1(r,t\u2085)", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.getHSBColor(230.0f / 256.0f, 1f, 1f));
        this.getCalcChartN().addSeries("\u03C1(r,t)", x, y)
            .setLineStyle(BasicStroke[1]).setLineColor(Color.orange);
        switch (standdiff) {
            case "stand":
                this.getCalcChartN().setTitle(this.getLanguage().equals("fin")
                    ? "Rrms-normaalijakauma" : "Rrms Normal Distribution");
                //this.getCalcChartN().setXAxisTitle("r");
                this.getCalcChartN().setYAxisTitle("\u03C1(r)");
                this.getCalcChartN().getStyler().setYAxisMin(0.0);
                this.getCalcChartN().getStyler().setYAxisMax(1.0);
                break;
            case "diff":
                this.getCalcChartN().setTitle(this.getLanguage().equals("fin")
                    ? "Diffuusio-normaalijakauma" : "Diffusion Normal distribution");
                //this.getCalcChartN().setXAxisTitle("r");
                this.getCalcChartN().getStyler().setYAxisMin(0.0);
                this.getCalcChartN().setYAxisTitle("\u03C1(r)");
                break;
        }
        this.getCalcChartN().getStyler().setXAxisMin(minx);
        this.getCalcChartN().getStyler().setXAxisMax(maxx);
        this.getFrame().getContentPane().add(this.getChartPanelN(),1);
    }

    /**
     * method for plotting SAW3
     * @param x x-axis data
     * @param y y-axis data
     */
    void setHData(double[] x, double[] y) {
        this.getCalcChartH().getSeriesMap().clear();
        this.getChartPanelH().removeAll();
        this.getFrame().getContentPane().remove(this.getChartPanelH());
        this.getCalcChartH().addSeries("hist", x, y).setFillColor(Color.orange);
        this.getCalcChartH().setTitle(this.getLanguage().equals("fin")
            ? "Etäisyys-histogrammi" : "Distance Histogram");
        this.getFrame().getContentPane().add(this.getChartPanelH(),2);
    }

    /**
     * method for plotting energy minimization in Diffusion
     * @param x x-axis data (energy_x)
     * @param y y-axis data (energy_y)
     */
    void setEData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartE().addSeries(this.getLanguage().equals("fin") ? "energia" :"energy", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.MAGENTA);
        this.getCalcChartE().setTitle(this.getLanguage().equals("fin")
            ? "Energian minimointi" : "Energy Minimizing");
        this.getFrame().getContentPane().add(this.getChartPanelE());
    }

    /**
     * method for plotting diffusion in Diffusion (normal y-axis)
     * @param x x-axis data
     * @param y y-axis data
     */
    void setDData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartD().addSeries(this.getLanguage().equals("fin") ? "diffuusio" : "diffusion", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartD().setTitle(this.getLanguage().equals("fin")
            ? "Diffuusiokerroin" : "Diffusion Coefficient");
        this.getFrame().getContentPane().add(this.getChartPanelD());
    }

    /**
     * method for plotting viscosity calculation in Diffusion
     * @param x x-axis data (visc_x)
     * @param y y-axis data (visc_y)
     */
    void setVData(List<Double> x, List<Double> y) {
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
                JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartV().addSeries(this.getLanguage().equals("fin") ? "viskositeetti" : "viscosity", x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.orange);
        this.getCalcChartV().setTitle(this.getLanguage().equals("fin")
            ? "Dynaaminen viskositeetti" : "Dynamic Viscosity");
        this.getFrame().getContentPane().add(this.getChartPanelV());
    }

    /**
     * method for plotting SAW1
     * @param x x-axis data
     * @param y y-axis data
     * @param dim dimension
     */
    void setS1Data(List<Integer> x, List<Double> y, int dim) { //u208# subscript
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 2.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f ),
            new BasicStroke( 2.0f, CAP_SQUARE, JOIN_MITER, 10.0f,null, 0.0f ),
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f ),
            new BasicStroke( 2.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartS1().addSeries("Rrms", x, y).setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartS1().addSeries("<Rrms>", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(100.0f / 256.0f, 1f, 1f));
        this.getCalcChartS1().addSeries(this.getLanguage().equals("fin") ? "odotusarvo" : "expected value", x, y)
            .setLineStyle(BasicStroke[2]).setLineColor(Color.blue);
        this.getCalcChartS1().addSeries(this.getLanguage().equals("fin") ? "etäisyys" : "distance", x, y)
            .setLineStyle(BasicStroke[3]).setLineColor(Color.orange);
        this.getCalcChartS1().setTitle(this.getLanguage().equals("fin")
            ? "Itseäänvälttelevä kulku, "+dim+"D" : "Self-avoiding Walk, "+dim+"D");
        this.getFrame().getContentPane().add(this.getChartPanelS1(),0);
    }

    /**
     * method for plotting SAW2
     * @param x x-axis data
     * @param y y-axis data
     */
    void setS2Data(List<Integer> x, List<Double> y) { // u208#=subscript
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 2.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f ),
            new BasicStroke( 2.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f ),
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f ),
            new BasicStroke( 1.5f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.getCalcChartS2().addSeries("Rrms", x, y).setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.getCalcChartS2().addSeries("<Rrms>", x, y).setLineStyle(BasicStroke[1]).setLineColor(Color.getHSBColor(100.0f / 256.0f, 1f, 1f));
        this.getCalcChartS2().addSeries(this.getLanguage().equals("fin") ? "odotusarvo" : "expected value", x, y)
            .setLineStyle(BasicStroke[2]).setLineColor(Color.blue);
        this.getCalcChartS2().addSeries(this.getLanguage().equals("fin") ? "etäisyys" :"distance", x, y)
            .setLineStyle(BasicStroke[3]).setLineColor(Color.orange);
        this.getFrame().getContentPane().add(this.getChartPanelS2(),1);
    }

    /**
     * method for plotting SAW3
     * @param labelMap x-axis labels
     * @param x x-axis data
     * @param y y-axis data
     */
    void setS3Data(Map<Object,Object> labelMap, List<Integer> x, List<Double> y) {
        this.getCalcChartS3().addSeries("hist", x, y).setFillColor(Color.orange);
        this.getCalcChartS3().setTitle(this.getLanguage().equals("fin") ? "Etäisyys-histogrammi" : "Distance Histogram");
        this.getCalcChartS3().setCustomCategoryLabels(labelMap);
        this.getFrame().getContentPane().add(this.getChartPanelS3(),2);
    }

    /**
     * method for updating R_rms and sqrt(steps) vs. walks in Real Time Rms
     * @param name name for "R_rms" or "sqrt(steps)"
     * @param x x-axis data (xAxis)
     * @param y y-axis data (yAxis or y2Axis)
     * @param expected real time expected value
     * @param peak real time rms value
     */
    void updateWData(String name, double[] x, double[] y, double expected, double peak) {
        this.getCalcChartW().updateXYSeries(name, x, y, null);
        this.getCalcChartW().setTitle(this.getLanguage().equals("fin")
            ? "Rrms (" + String.format("%.2f",peak) + ") ja odotusarvo (" + String.format("%.2f",expected) + ") ajojen funktiona"
            : "Rrms (" + String.format("%.2f",peak) + ") and Expected Value (" + String.format("%.2f",expected) + ") as Functions of Walks");
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
    void updateNData(String name, double[] x, double[] y) {
        this.getCalcChartN().updateXYSeries(name, x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating histogram plot in Real Time Rms
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateHData(double[] x, double[] y) {
        this.getCalcChartH().updateXYSeries("hist", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating energy minimization in Diffusion
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateEData(List<Double> x, List<Double> y) {
        this.getCalcChartE().updateXYSeries(this.getLanguage().equals("fin") ? "energia" :"energy", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating diffusion in Real Time Rms (normal y-axis)
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateDData(List<Double> x, List<Double> y) {
        this.getCalcChartD().updateXYSeries(this.getLanguage().equals("fin") ? "diffuusio" :"diffusion", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating viscosity calculation in Diffusion
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateVData(List<Double> x, List<Double> y) {
        this.getCalcChartV().updateXYSeries(this.getLanguage().equals("fin") ? "viskositeetti" :"viscosity", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating comoving x-axis plot in SAW
     * @param name name for plot series
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateS1Data(String name, List<Integer> x, List<Double> y) {
        this.getCalcChartS1().updateXYSeries(name, x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating increasing x-axis plot in SAW
     * @param name name for plot series
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateS2Data(String name, List<Integer> x, List<Double> y) {
        this.getCalcChartS2().updateXYSeries(name, x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for updating histogram plot in SAW
     * @param x x-axis data
     * @param y y-axis data
     */
    void updateS3Data(List<Integer> x, List<Double> y) {
        this.getCalcChartS3().updateCategorySeries("hist", x, y, null);
        this.getFrame().revalidate();
        this.getFrame().repaint();
        this.getFrame().pack();
    }

    /**
     * method for printing final energy difference
     * @param evalue value
     */
    void setDeltaT(double evalue) { // \u2146=differential d
        this.getCalcChartE().setTitle(this.getLanguage().equals("fin")
            ? this.getCalcChartE().getTitle()+", \u2146T = "+this.formatter.format(evalue)+" K"
            : this.getCalcChartE().getTitle()+", \u2146T = "+this.formatter.format(evalue)+" K");
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
    void setNMaxY(double maxY) {
        this.getCalcChartN().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param minY the y-axis min value to set
     */
    void setEMinY(double minY) { this.getCalcChartE().getStyler().setYAxisMin(minY); }

    /**
     * @param maxY the y-axis max value to set
     */
    void setEMaxY(double maxY) { this.getCalcChartE().getStyler().setYAxisMax(maxY); }

    /**
     * @param maxY the y-axis max value to set
     */
    void setVMaxY(double maxY) {
        this.getCalcChartV().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setDMaxY(double maxY) {
        this.getCalcChartD().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param minX the x-axis min value to set
     */
    void setS1MinX(double minX) {
        this.getCalcChartS1().getStyler().setXAxisMin(minX);
    }

    /**
     * @param maxX the x-axis max value to set
     */
    void setS1MaxX(double maxX) {
        this.getCalcChartS1().getStyler().setXAxisMax(maxX);
    }

    /**
     * @param maxX the x-axis max value to set
     */
    void setS2MaxX(double maxX) {
        this.getCalcChartS2().getStyler().setXAxisMax(maxX);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setS1MaxY(double maxY) {
        this.getCalcChartS1().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param maxY the y-axis max value to set
     */
    void setS2MaxY(double maxY) {
        this.getCalcChartS2().getStyler().setYAxisMax(maxY);
    }

    /**
     * @param coeff the max viscosity value to set to chart title
     */
    void setVTitle(int time, double coeff) { // u03BC=μ, u00B5=micro
        this.getCalcChartV().setTitle(this.getLanguage().equals("fin")
            ? "Dynaaminen viskositeetti, \u03BC(" + time + "ps) = "+this.diffformatter.format(coeff)+" \u00B5Pa s"
            : "Dynamic viscosity, \u03BC(" + time + "ps) = "+this.diffformatter.format(coeff)+" \u00B5Pa s");
    }

    /**
     * @param coeff the max diffusion coefficient value to set to chart title
     */
    void setDTitle(int time, double coeff) {
        this.getCalcChartD().setTitle(this.getLanguage().equals("fin")
            ? "Diffuusiokerroin, D(" + time + "ps) = "+this.diffformatter.format(coeff)+"E-7 cm^2/s"
            : "Diffusion Coefficient, D(" + time + "ps) = "+this.diffformatter.format(coeff)+"E-7 cm^2/s");
    }

    /**
     * @param mu1 the connective constant
     * @param mu2 the connective constant
     */
    void setS1SawTitle(int dim, double mu1, double mu2) { // u03BC=mu, u208#=subscript
        this.getCalcChartS1().setTitle(this.getLanguage().equals("fin")
            ? "Itseäänvälttelevä kulku, "+dim+"D, \u03BC\u2081="+this.muformatter.format(mu1)+", \u03BC\u2082="+this.muformatter.format(mu2)
            : "Self-avoiding Walk, "+dim+"D, \u03BC\u2081="+this.muformatter.format(mu1)+", \u03BC\u2082="+this.muformatter.format(mu2));
    }

    /**
     * @param pros the percent of failed
     */
    void setS1CbmcTitle(int dim, double pros) { // u03BC=mu, u208#=subscript
        this.getCalcChartS1().setTitle(this.getLanguage().equals("fin")
            ? "Itseäänvälttelevä kulku, "+dim+"D, epäonnistuneita: "+this.diffformatter.format(pros)+"%"
            : "Self-avoiding Walk, "+dim+"D, failed: "+this.diffformatter.format(pros)+"%");
    }

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
    private int getHeight() { return 1000 / (int) Screen.getMainScreen().getRenderScale(); }

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
     * @return the calcChartN
     */
    @Contract(pure = true)
    private XYChart getCalcChartN() { return calcChartN; }

    /**
     * @return the calcChartH
     */
    @Contract(pure = true)
    private XYChart getCalcChartH() { return calcChartH; }

    /**
     * @return the calcChartD
     */
    @Contract(pure = true)
    private XYChart getCalcChartD() { return calcChartD; }

    /**
     * @return the calcChartE
     */
    @Contract(pure = true)
    private XYChart getCalcChartE() { return calcChartE; }

    /**
     * @return the calcChartV
     */
    @Contract(pure = true)
    private XYChart getCalcChartV() { return calcChartV; }

    /**
     * @return the calcChartS1
     */
    @Contract(pure = true)
    private XYChart getCalcChartS1() { return calcChartS1; }

    /**
     * @return the calcChartS2
     */
    @Contract(pure = true)
    private XYChart getCalcChartS2() { return calcChartS2; }

    /**
     * @return the calcChartS3
     */
    @Contract(pure = true)
    private CategoryChart getCalcChartS3() { return calcChartS3; }

    /**
     * @return the chartPanelW
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelW() { return chartPanelW; }

    /**
     * @return the chartPanelN
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelN() { return chartPanelN; }

    /**
     * @return the chartPanelH
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelH() { return chartPanelH; }

    /**
     * @return the chartPanelD
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelD() { return chartPanelD; }

    /**
     * @return the chartPanelV
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelV() { return chartPanelE; }

    /**
     * @return the chartPanelE
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelE() { return chartPanelV; }

    /**
     * @return the chartPanelS1
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelS1() { return chartPanelS1; }

    /**
     * @return the chartPanelS2
     */
    @Contract(pure = true)
    private XChartPanel<XYChart> getChartPanelS2() { return chartPanelS2; }

    /**
     * @return the chartPanelS3
     */
    @Contract(pure = true)
    private XChartPanel<CategoryChart> getChartPanelS3() { return chartPanelS3; }

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

}
