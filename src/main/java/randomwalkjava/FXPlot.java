
package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.Color;
import java.awt.BasicStroke; 
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for ORG.KNOWM XCHART creation and handling
 */
public class FXPlot extends SceneRealTimeRms {

    private final int width = 800;
    private final int height = 1000;
    private final int a = 400
        / (int) Screen.getMainScreen().getRenderScale();
    private final int b = 100
        / (int) Screen.getMainScreen().getRenderScale();
    private final int c = 10
        / (int) Screen.getMainScreen().getRenderScale();
    private final XYChart calcChartW;
    private final XYChart calcChartH;
    private final XYChart calcChartE;
    private final XChartPanel chartPanelW;
    private final XChartPanel chartPanelH;
    private final XChartPanel chartPanelE;
    private final JFrame frame;

    public void setMinX(double minX) {
        this.calcChartW.getStyler().setXAxisMin(minX);
    }

    public void setMaxX(double maxX) {
       this.calcChartW.getStyler().setXAxisMax(maxX);
    }

    public void setMinY(double minY) {
        this.calcChartW.getStyler().setYAxisMin(minY);
    }

    public void setMaxY(double maxY) {
       this.calcChartW.getStyler().setYAxisMax(maxY);
    }

    public double getEMaxX() {
        return this.calcChartE.getStyler().getXAxisMax();
    }

    public void setEMaxX(double maxX) {
        this.calcChartE.getStyler().setXAxisMax(maxX);
    }

    public void setEMaxY(double maxY) {
       this.calcChartE.getStyler().setYAxisMax(maxY);
    }

    public void setFrameVis(boolean fvis) {
       this.frame.setVisible(fvis);
    }

    public void setHMinX(double minX) {
        this.calcChartH.getStyler().setXAxisMin(minX);
    }

    public void setHMaxX(double maxX) {
        this.calcChartH.getStyler().setXAxisMax(maxX);
    }

    public void setHMaxY(double maxY) {
        this.calcChartH.getStyler().setYAxisMax(maxY);
    }

    public FXPlot(String which, Integer screenHeight) {
        /**
        * JFrame
        */
        this.frame = new JFrame();
        this.frame.setBackground(Color.white);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        if (which.equals("W&H")) {
            this.frame.setLocation(0, 0);
            this.frame.setPreferredSize(new Dimension(this.width,this.height));
            this.frame.setTitle("Real Time R_rms");
            this.frame.getContentPane().setLayout(new GridLayout(2,1));
        } else if (which.equals("E")) {
            this.frame.setLocation(0, (screenHeight-this.height/2)/2);
            this.frame.setPreferredSize(new Dimension(this.width,this.height-this.a));
            this.frame.setTitle("Real Time MMC");
        }
        ImageIcon icon = new ImageIcon("src/main/resources/images/icon.png");
        this.frame.setIconImage(icon.getImage());
        /**
        * XYCharts
        */
        this.calcChartW = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();
        this.calcChartH = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();
        this.calcChartE = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();
        /**
        * XChartPanels
        */
        this.chartPanelW = new XChartPanel(this.calcChartW);
        this.chartPanelH = new XChartPanel(this.calcChartH);
        this.chartPanelE = new XChartPanel(this.calcChartE);
        
        if (which.equals("W&H")) {
            this.chartPanelW.setBounds(0, this.c, this.width, this.height/2-this.b);
            this.chartPanelW.setVisible(true);

            this.chartPanelH.setBounds(0, this.height/2, this.width, this.height/2-this.b);
            this.chartPanelH.setVisible(true);
            /**
            * Upper XYChart calcChartW
            */
            this.calcChartW.getStyler().setXAxisTitleVisible(true);
            this.calcChartW.getStyler().setYAxisTitleVisible(true);
            this.calcChartW.setXAxisTitle("walks");
            this.calcChartW.setYAxisTitle("<R_rms>");
            this.calcChartW.getStyler().setLegendVisible(true);
            this.calcChartW.getStyler().setMarkerSize(0);
            this.calcChartW.getStyler().setXAxisDecimalPattern("0");
            this.calcChartW.getStyler().setYAxisDecimalPattern("0.0");
            this.calcChartW.getStyler().setAxisTickLabelsFont(new java.awt.Font(null,0,15));
            this.calcChartW.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.calcChartW.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
            this.calcChartW.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
            this.calcChartW.getStyler().setLegendFont(new java.awt.Font(null,0,18));
            this.calcChartW.getStyler().setChartTitlePadding(15);
            this.calcChartW.getStyler().setToolTipsEnabled(false);
            /**
            * Lower XYChart calcChartH
            */
            this.calcChartH.getStyler().setLegendVisible(false);
            this.calcChartH.setXAxisTitle("<R_rms>");
            this.calcChartH.getStyler().setMarkerSize(0);
            this.calcChartH.getStyler().setXAxisDecimalPattern("0.0");
            this.calcChartH.getStyler().setYAxisDecimalPattern("0.0");
            this.calcChartH.getStyler().setAxisTickLabelsFont(new java.awt.Font(null,0,15));
            this.calcChartH.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.calcChartH.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
            this.calcChartH.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
            this.calcChartH.getStyler().setLegendFont(new java.awt.Font(null,0,18));
            this.calcChartH.getStyler().setChartTitlePadding(15);
            this.calcChartH.getStyler().setYAxisMin(0.0);
            this.calcChartH.getStyler().setYAxisMax(10.0);
            this.calcChartH.getStyler().setToolTipsEnabled(true);

        } else if (which.equals("E")) {
            this.chartPanelE.setBounds(0, 0, this.width, this.height-this.a);
            this.chartPanelE.setVisible(true);
            /**
            * XYChart calcChartE
            */
            this.calcChartE.getStyler().setLegendVisible(false);
            this.calcChartE.setXAxisTitle("steps");
            this.calcChartE.setYAxisTitle("Arbitrary Energy Unit");
            this.calcChartE.getStyler().setMarkerSize(0);
            this.calcChartE.getStyler().setXAxisDecimalPattern("0");
            this.calcChartE.getStyler().setYAxisDecimalPattern("0.0");
            this.calcChartE.getStyler().setAxisTickLabelsFont(new java.awt.Font(null,0,15));
            this.calcChartE.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
            this.calcChartE.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
            this.calcChartE.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
            this.calcChartE.getStyler().setLegendFont(new java.awt.Font(null,0,18));
            this.calcChartE.getStyler().setChartTitlePadding(15);
            this.calcChartE.getStyler().setXAxisMin(0.0);
            this.calcChartE.getStyler().setYAxisMin(0.0);
            this.calcChartE.getStyler().setToolTipsEnabled(true);
        }
    }

    public void setWData(String name1, String name2, double[] x, double[] y, double expected) {
        this.calcChartW.getSeriesMap().clear();
        this.chartPanelW.removeAll();
        this.frame.getContentPane().remove(this.chartPanelW);
        BasicStroke[] BasicStroke = new BasicStroke[]{
                new BasicStroke( 1.5f, CAP_SQUARE,
            		JOIN_MITER, 10.0f, null, 0.0f ),
                new BasicStroke( 1.5f, CAP_SQUARE,
        			JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f )
        };
        this.calcChartW.addSeries(String.valueOf(name1), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.red);
        this.calcChartW.addSeries(String.valueOf(name2), x, y)
            .setLineStyle(BasicStroke[1]).setLineColor(Color.blue);

        this.calcChartW.setTitle("R_rms and sqrt(N) vs. walks, "+"sqrt(N) = "+String.format("%.2f",expected));
        this.calcChartW.getStyler().setAntiAlias(true);
        this.frame.getContentPane().add(this.chartPanelW);
        this.frame.repaint();
        this.frame.pack();
    }

    public void setHData(String name, double[] x, double[] y, double minx,
        double maxx, boolean standnorm) {
        this.calcChartH.getSeriesMap().clear();
        this.chartPanelH.removeAll();
        this.frame.getContentPane().remove(this.chartPanelH);
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f ),
        };
        this.calcChartH.addSeries(String.valueOf(name), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.orange);

        if ( standnorm == true ) {
            this.calcChartH.setTitle("R_rms standard normal distribution");
        } else {
            this.calcChartH.setTitle("R_rms normal distribution");
        }
        this.calcChartH.getStyler().setXAxisMin(minx);
        this.calcChartH.getStyler().setXAxisMax(maxx);
        this.calcChartH.getStyler().setYAxisMax( 1.05 );
        this.calcChartH.getStyler().setAntiAlias(true);
        this.frame.getContentPane().add(this.chartPanelH);
        this.frame.repaint();
        this.frame.pack();
    }

    public void setEData(String name, List<Double> x, List<Double> y) {
        this.calcChartE.getSeriesMap().clear();
        this.chartPanelE.removeAll();
        this.frame.getContentPane().remove(this.chartPanelE);
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f ),
        };
        this.calcChartE.addSeries(String.valueOf(name), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.MAGENTA);
        this.calcChartE.setTitle("MMC Diffusion Random Walk");
        this.calcChartE.getStyler().setAntiAlias(true);
        this.frame.getContentPane().add(this.chartPanelE);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateWData(String name, double[] x, double[] y) {
        this.calcChartW.updateXYSeries(name, x, y, null);
        this.frame.add(this.chartPanelW,0);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateHData(String name, double[] x, double[] y, double expected) {
        this.calcChartH.updateXYSeries(name, x, y, null);
        this.calcChartH.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        this.calcChartH.getStyler().setToolTipFont(new java.awt.Font(null,0,18));
        this.frame.add(this.chartPanelH,1);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateEData(String name, List<Double> x, List<Double> y) {
        this.calcChartE.updateXYSeries(name, x, y, null);
        this.calcChartE.getStyler().setToolTipType(Styler.ToolTipType.yLabels);
        this.calcChartE.getStyler().setToolTipFont(new java.awt.Font(null,0,18));
        this.frame.add(this.chartPanelE);
        this.frame.repaint();
        this.frame.pack();
    }

    public XYChart getWChart() {
        return calcChartW;
    }

    public XYChart getHChart() {
        return calcChartH;
    }

    public XYChart getEChart() {
        return calcChartE;
    }

    public JFrame getFrame() {
        return frame;
    }
}
