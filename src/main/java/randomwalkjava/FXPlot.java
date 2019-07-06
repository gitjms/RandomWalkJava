
package randomwalkjava;

import java.awt.Color;
import java.awt.BasicStroke; 
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_MITER;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;

public class FXPlot extends SceneAnim {

    final int width = 800;
    final int height = 600;
    final XYChart calcChart;
    final XChartPanel chartPanel;
    final JFrame frame;

    public void setBounds(int screenHeight) {
        this.frame.setBounds(
            10, (screenHeight-height)/2,
            width, height);
    }

    public void setMinX(double minX) {
        this.calcChart.getStyler().setXAxisMin(minX);
    }

    public void setMaxX(double maxX) {
       this.calcChart.getStyler().setXAxisMax(maxX);
    }

    public void setMinY(double minY) {
        this.calcChart.getStyler().setYAxisMin(minY);
    }

    public void setMaxY(double maxY) {
       this.calcChart.getStyler().setYAxisMax(maxY);
    }

    private void setXaxtitle(boolean xtitvis) {
        this.calcChart.getStyler().setXAxisTitleVisible(xtitvis);
    }

    private void setYaxtitle(boolean ytitvis) {
        this.calcChart.getStyler().setYAxisTitleVisible(ytitvis);
    }

    private void setLegendVis(boolean legvis) {
        this.calcChart.getStyler().setLegendVisible(legvis);
    }

    private void setMarkSize(int msize) {
        this.calcChart.getStyler().setMarkerSize(msize);
    }

    private void setxDec(String xdec) {
        this.calcChart.getStyler().setXAxisDecimalPattern(xdec);
    }

    private void setyDec(String ydec) {
        this.calcChart.getStyler().setYAxisDecimalPattern(ydec);
    }

    private void setStyle(XYSeriesRenderStyle sty) {
        this.calcChart.getStyler().setDefaultSeriesRenderStyle(sty);
    }

    private void setTitle(String title) {
        this.calcChart.setTitle(title);
    }

    private void setFrameTitle(String ftitle) {
        this.frame.setTitle(ftitle);
    }

    private void setChartVis(boolean chvis) {
        this.frame.setVisible(chvis);
    }

    public FXPlot(int screenheight) {
        this.frame = new JFrame();
        this.frame.setBackground(Color.white);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.calcChart = new XYChartBuilder()
            .theme(ChartTheme.Matlab).build();
        this.setBounds(screenheight);
        this.chartPanel = new XChartPanel(calcChart);
        this.setXaxtitle(true);
        this.setYaxtitle(true);
        this.calcChart.setXAxisTitle("walks");
        this.calcChart.setYAxisTitle("R_rms");
        this.setLegendVis(true);
        this.setMarkSize(0);
        this.setxDec("0");
        this.setyDec("0.0");
        this.calcChart.getStyler().setAxisTickLabelsFont(new java.awt.Font(null,0,15));
        this.setStyle(XYSeriesRenderStyle.Line);
        this.setTitle("R_rms and sqrt(N) vs. walks");
        this.calcChart.getStyler().setChartTitleFont(new java.awt.Font(null,0,20));
        this.calcChart.getStyler().setAxisTitleFont(new java.awt.Font(null,0,18));
        this.calcChart.getStyler().setLegendFont(new java.awt.Font(null,0,18));
        this.calcChart.getStyler().setChartTitlePadding(15);
        this.setFrameTitle("Real Time R_rms");
        this.setChartVis(false);
    }

    public void setData(String name1, String name2, double[] x, double[] y) {
        this.calcChart.getSeriesMap().clear();
        this.chartPanel.removeAll();
        this.frame.getContentPane().removeAll();
        BasicStroke[] BasicStroke = new BasicStroke[]{
                new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, new float[]{5, 5}, 2.0f ),
                new BasicStroke( 1.5f, CAP_SQUARE,
				JOIN_MITER, 10.0f, null, 0.0f )
        };
        
        this.calcChart.addSeries(String.valueOf(name1), x, y)
            .setLineStyle(BasicStroke[0]).setLineColor(Color.blue);
        this.calcChart.addSeries(String.valueOf(name2), x, y)
            .setLineStyle(BasicStroke[1]).setLineColor(Color.red);

        this.calcChart.getStyler().setAntiAlias(true);
        this.chartPanel.getChart();
        this.frame.add(chartPanel);
        this.frame.repaint();
        this.frame.pack();
    }

    public void updateData(String name, double[] x, double[] y) {
        this.calcChart.updateXYSeries(name, x, y, null);
        this.chartPanel.getChart();
        this.frame.add(chartPanel);
        this.frame.repaint();
        this.frame.pack();
    }

    public XYChart getCalcChart() {
        return calcChart;
    }

    public JFrame getFrame() {
        return frame;
    }
}
