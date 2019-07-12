
package randomwalkjava;

import java.awt.Color;
import java.awt.BasicStroke; 
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;

public class FXPlot extends SceneAnimation {

    private final int width = 800;
    private final int height = 1000;
    private final XYChart calcChartW;
    private final XYChart calcChartH;
    private final XChartPanel chartPanelW;
    private final XChartPanel chartPanelH;
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

    public void setFrameVis(boolean fvis) {
       this.frame.setVisible(fvis);
    }

    public void setHMinX(double minX) {
        this.calcChartH.getStyler().setXAxisMin(minX);
    }

    public void setHMaxX(double maxX) {
        this.calcChartH.getStyler().setXAxisMax(maxX);
    }

    public FXPlot() {
        this.frame = new JFrame();
        this.frame.setBackground(Color.white);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setLocation(0, 0);
        this.frame.setPreferredSize(new Dimension(width,height));
        this.frame.setTitle("Real Time R_rms");
        ImageIcon icon = new ImageIcon("src/main/resources/images/icon.png");
        this.frame.setIconImage(icon.getImage());
        this.frame.getContentPane().setLayout(new GridLayout(2,1));

        this.calcChartW = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();
        this.calcChartH = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();

        this.chartPanelW = new XChartPanel(calcChartW);
        this.chartPanelW.setBounds(0, 10, width, height/2-100);
        this.chartPanelW.setVisible(true);

        this.chartPanelH = new XChartPanel(calcChartH);
        this.chartPanelH.setBounds(0, height/2, width, height/2-100);
        this.chartPanelH.setVisible(true);

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
        this.calcChartW.setTitle("R_rms and sqrt(N) vs. walks");
        this.calcChartW.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
        this.calcChartW.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
        this.calcChartW.getStyler().setLegendFont(new java.awt.Font(null,0,18));
        this.calcChartW.getStyler().setChartTitlePadding(15);

        this.calcChartH.getStyler().setLegendVisible(false);
        this.calcChartH.setXAxisTitle("<R_rms>");
        this.calcChartH.getStyler().setMarkerSize(0);
        this.calcChartH.getStyler().setXAxisDecimalPattern("0.0");
        this.calcChartH.getStyler().setYAxisDecimalPattern("0.0");
        this.calcChartH.getStyler().setAxisTickLabelsFont(new java.awt.Font(null,0,15));
        this.calcChartH.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        this.calcChartH.setTitle("R_rms normal distribution");
        this.calcChartH.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
        this.calcChartH.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
        this.calcChartH.getStyler().setLegendFont(new java.awt.Font(null,0,18));
        this.calcChartH.getStyler().setChartTitlePadding(15);
        this.calcChartH.getStyler().setYAxisMin(0.0);
        this.calcChartH.getStyler().setYAxisMax(10.0);
    }

    public void setWData(String name1, String name2, double[] x, double[] y) {
        this.calcChartW.getSeriesMap().clear();
        this.chartPanelW.removeAll();
        this.frame.getContentPane().remove(this.chartPanelW);
        BasicStroke[] BasicStroke = new BasicStroke[]{
                new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f ),
                new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.calcChartW.addSeries(String.valueOf(name1), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.blue);
        this.calcChartW.addSeries(String.valueOf(name2), x, y)
            .setLineStyle(BasicStroke[1]).setLineColor(Color.red);

        this.calcChartW.getStyler().setAntiAlias(true);
        this.frame.getContentPane().add(this.chartPanelW);
        this.frame.repaint();
        this.frame.pack();
    }

    public void setHData(String name1, String name2, double[] x, double[] y, double minx, double maxx, double maxy) {
        this.calcChartH.getSeriesMap().clear();
        this.chartPanelH.removeAll();
        this.frame.getContentPane().remove(this.chartPanelH);
        BasicStroke[] BasicStroke = new BasicStroke[]{
            new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f )
        };
        this.calcChartH.addSeries(String.valueOf(name2), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.orange);

        this.calcChartH.getStyler().setXAxisMin(minx);
        this.calcChartH.getStyler().setXAxisMax(maxx);
        this.calcChartH.getStyler().setYAxisMax(maxy);
        this.calcChartH.getStyler().setAntiAlias(true);
        this.frame.getContentPane().add(this.chartPanelH);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateWData(String name, double[] x, double[] y) {
        this.calcChartW.updateXYSeries(name, x, y, null);
        this.frame.add(this.chartPanelW,0);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateHData(String name, double[] x, double[] y, double spike) {
        this.calcChartH.updateXYSeries(name, x, y, null);
        this.calcChartH.getStyler().setYAxisMax( spike );
        this.frame.add(this.chartPanelH,1);
        this.frame.repaint();
        this.frame.pack();
    }

    public XYChart getWChart() {
        return calcChartW;
    }

    public XYChart getHChart() {
        return calcChartH;
    }

    public JFrame getFrame() {
        return frame;
    }
}
