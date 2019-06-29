
package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.scene.control.TextArea;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class Execution {

    final int chartWidth = 860;
    final int chartHeight = 605;
    final int screenHeight = Screen.getMainScreen().getHeight();
    final String path = "C:\\DATA";
    final String fexec = "walk.exe";
    final String pyexecrms = "python plotrms.py";
    final String pyexec1d = "python plot1d.py";
    final String pyexec2d = "python plot2d.py";
    final String pyexec3d = "python plot3d.py";
    
    public Execution() {
    }

    public void executeTrace(File folder, TextArea textArea, JFrame frame, Data data, String[] vars ) {
        //this.vars[0] = amount, from user
        //this.vars[1] = size, from user
        //this.vars[2] = steps, from user
        //this.vars[3] = skip, from user
        //this.vars[4] = dimension, from user
        //this.vars[5] = avoid, from user
        //this.vars[6] = save or real time, from user
        //this.vars[7] = xgraph or normal save, from user
        String xDataPath = "";
        String yDataPath = "";
        String zDataPath = "";
        BufferedImage image1d = null;
        BufferedImage image2d = null;
        BufferedImage image3d = null;

        textArea.setText(data.createData(folder, fexec, true));
        int particles = Integer.valueOf(vars[0]);
        int dimension = Integer.valueOf(vars[4]);
        int steps = Integer.valueOf(vars[2]);

        xDataPath = path + "\\" + "x_path"
            + vars[4] + "D_"
            + vars[0] + ".xy";
                
        // 1D DATA
        if ( dimension == 1 ) {
            Pyplot pyplot1d = new Pyplot();
            String[] files1d = new String[]{xDataPath};
            textArea.setText(pyplot1d.createPlot(folder, files1d, dimension, pyexec1d, false));
            String imgFile1d = "jpyplot1D_N" + particles + ".png";
            // GET IMAGE FROM PYDPLOT.READPYPLOT()
            image1d = pyplot1d.readPyPlot(new File(path + "\\" + imgFile1d));
        }

        // 2D DATA
        if ( dimension == 2 || dimension == 3 ) {
            yDataPath = path + "\\" + "y_path"
                + vars[4] + "D_"
                + vars[0] + ".xy";
            if ( dimension == 2 ) {
                Pyplot pyplot2d = new Pyplot();
                String[] files2d = new String[]{xDataPath, yDataPath};
                textArea.setText(pyplot2d.createPlot(folder, files2d, dimension, pyexec2d, false));
                String imgFile2d = "jpyplot2D_N" + vars[0] + ".png";
                // GET IMAGE FROM PYDPLOT.READPYPLOT()
                image2d = pyplot2d.readPyPlot(new File(path + "\\" + imgFile2d));
            }
        }
                
        // 3D DATA
        if ( dimension == 3 ) {
            zDataPath = path + "\\" + "z_path"
                + vars[4] + "D_"
                + vars[0] + ".xy";
            Pyplot pyplot3d = new Pyplot();
            String[] files3d = new String[]{xDataPath, yDataPath, zDataPath};
            textArea.setText(pyplot3d.createPlot(folder, files3d, dimension, pyexec3d, false));
            String imgFile3d = "jpyplot3D_N" + vars[0] + ".png";
            // GET IMAGE FROM PYDPLOT.READPYPLOT()
            image3d = pyplot3d.readPyPlot(new File(path + "\\" + imgFile3d));
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - Path Tracing");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel("N="+vars[0]+", "+steps+" steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(chartWidth/2-50,0,chartWidth/2+150,newFontSize);
        // PLOT 1D
        if ( dimension == 1 ) {
            ImageIcon figIcn = new ImageIcon(image1d);
            JLabel figLabel = new JLabel(figIcn);
            frame.add(titleLabel);
            frame.add(figLabel);
            frame.repaint();
            frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
            frame.pack();
            frame.setVisible(true);
        // PLOT 2D
        } else if ( dimension == 2 ) {
            ImageIcon figIcn = new ImageIcon(image2d);
            JLabel figLabel = new JLabel(figIcn);
            frame.add(titleLabel);
            frame.add(figLabel);
            frame.repaint();
            frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
            frame.pack();
            frame.setVisible(true);
        // PLOT 3D
        } else if ( dimension == 3 ) {
            ImageIcon figIcn = new ImageIcon(image3d);
            JLabel figLabel = new JLabel(figIcn);
            frame.add(titleLabel);
            frame.add(figLabel);
            frame.repaint();
            frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
            frame.pack();
            frame.setVisible(true);
        }
    }

    public void executeRms(File folder, TextArea textArea, JFrame frame, Data data, String[] vars ) {
        //this.vars[0] = "0" amount
        //this.vars[1] = size, from user
        //this.vars[2] = steps, from user
        //this.vars[3] = skip, from user
        //this.vars[4] = dimension, from user
        //this.vars[5] = "-" avoid (n/a: only one particle at a time)
        //this.vars[6] = "s" save (n/a: save is default)
        //this.vars[7] = "-" xgraph (n/a: normal save is default)
        String rmsDataPath = "";
        BufferedImage imagerms = null;

        textArea.setText(data.createData(folder, fexec, true));
        int dimension = Integer.valueOf(vars[4]);
        int steps = Integer.valueOf(vars[2]);
                
        String calcData = "";
        if ( dimension == 1 ){
            rmsDataPath = path + "\\" + "rms_1D_"
                + vars[2] + "S.xy";
        } else if ( dimension == 2 ){
            rmsDataPath = path + "\\" + "rms_2D_"
                + vars[2] + "S.xy";
        } else if ( dimension == 3 ){
            rmsDataPath = path + "\\" + "rms_3D_"
                + vars[2] + "S.xy";
        }

        Pyplot pyplotrms = new Pyplot();
        String[] filerms = new String[]{rmsDataPath};
        textArea.setText(pyplotrms.createPlot(folder, filerms, dimension, pyexecrms, true));
        String imgFile = "jpyplotRMS" + vars[4] + "D_" + vars[2] + "S.png";
        // GET IMAGE FROM PYDPLOT.READPYPLOT()
        imagerms = pyplotrms.readPyPlot(new File(path + "\\" + imgFile));

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - R_rms Calculation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel();
        if ( Integer.valueOf(vars[3]) > 1 )
            titleLabel.setText(
                vars[4] + "D, " + 
                steps + " steps, skip " + 
                Integer.valueOf(vars[3]));
        else
            titleLabel.setText(vars[4]+"D, "+steps+" steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(chartWidth/2-50,0,chartWidth/2+150,newFontSize);

        ImageIcon figIcn = new ImageIcon(imagerms);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
        frame.pack();
        frame.setVisible(true);
    }
}
