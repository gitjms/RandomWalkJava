
package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.scene.control.TextArea;
import javafx.util.Pair;
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
    
    public Execution() {
    }

    public void executeSim(File folder, TextArea textArea, JFrame frame, Data data, String[] vars ) {
        // FROM SCENENOCALCULATION
        // vars from user:
        // vars[0] = particles,
        // vars[1] = diameter,
        // vars[2] = charge,
        // vars[3] = steps,
        // vars[4] = dimension,
        // vars[5] = temperature,
        // vars[6] = fixed,
        // vars[7] = lattice,
        // vars[8] = avoid,
        // vars[9] = save           n/a
        String pyexec1d = "python plot1d.py";
        String pyexec2d = "python plot2d.py";
        String pyexec3d = "python plot3d.py";
        String xDataPath = "";
        String yDataPath = "";
        String zDataPath = "";
        String titletext = "";
        BufferedImage image = null;

        Pair<Boolean, String> result = data.createData(folder, fexec, true);
        textArea.setText(result.getValue());
        if (result.getKey() == false)
            return;

        int particles = Integer.valueOf(vars[0]);
        int steps = Integer.valueOf(vars[3]);
        int dimension = Integer.valueOf(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") && vars[8].equals("a") )
            titletext = "Fixed source lattice particles, avoid, ";
        else if ( vars[6].equals("f") && vars[7].equals("l") && vars[8].equals("-") )
            titletext = "Fixed source lattice particles, no avoid, ";
        else if ( vars[6].equals("f") && vars[7].equals("-") && vars[8].equals("a") )
            titletext = "Fixed source free particles, avoid, ";
        else if ( vars[6].equals("f") && vars[7].equals("-") && vars[8].equals("-") )
            titletext = "Fixed source free particles, no avoid, ";
        else if ( vars[6].equals("-") && vars[7].equals("l") && vars[8].equals("a") )
            titletext = "Spread out lattice particles, avoid, ";
        else if ( vars[6].equals("-") && vars[7].equals("l") && vars[8].equals("-") )
            titletext = "Spread out lattice particles, no avoid, ";
        else if ( vars[6].equals("-") && vars[7].equals("-") && vars[8].equals("a") )
            titletext = "Spread out free particles, avoid, ";
        else if ( vars[6].equals("-") && vars[7].equals("-") && vars[8].equals("-") )
            titletext = "Spread out free particles, no avoid, ";

        xDataPath = path + "\\" + "x_path"
            + dimension + "D_"
            + particles + "N_"
            + steps + "S.xy";

        String imgFile1d = "jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".png";

        // 1D DATA
        if ( dimension == 1 ) {
            Pyplot pyplot1d = new Pyplot();
            String[] files1d = new String[]{xDataPath};
            textArea.setText(pyplot1d.createPlot(folder, files1d, dimension, pyexec1d, false));
            // GET IMAGE FROM PYDPLOT.READPYPLOT()
            image = pyplot1d.readPyPlot(new File(path + "/" + imgFile1d));
        }

        // 2D DATA
        if ( dimension == 2 || dimension == 3 ) {
            yDataPath = path + "\\" + "y_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.xy";
            if ( dimension == 2 ) {
                Pyplot pyplot2d = new Pyplot();
                String[] files2d = new String[]{xDataPath, yDataPath};
                textArea.setText(pyplot2d.createPlot(folder, files2d, dimension, pyexec2d, false));
                // GET IMAGE FROM PYDPLOT.READPYPLOT()
                image = pyplot2d.readPyPlot(new File(path + "/" + imgFile1d));
            }
        }
                
        // 3D DATA
        if ( dimension == 3 ) {
            zDataPath = path + "\\" + "z_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.xy";
            Pyplot pyplot3d = new Pyplot();
            String[] files3d = new String[]{xDataPath, yDataPath, zDataPath};
            textArea.setText(pyplot3d.createPlot(folder, files3d, dimension, pyexec3d, false));
            // GET IMAGE FROM PYDPLOT.READPYPLOT()
            image = pyplot3d.readPyPlot(new File(path + "/" + imgFile1d));
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - Path Tracing");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(chartWidth/2-200,0,chartWidth/2+150,newFontSize);
        // PLOT
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(20, (screenHeight-chartHeight)/2-60, chartWidth, chartHeight);
        frame.pack();
        frame.setVisible(true);
    }

    public void executeRms(File folder, TextArea textArea, JFrame frame, Data data, String[] vars ) {
        // FROM SCENECALCULATION
        // vars from user:
        // vars[0] = particles,     n/a
        // vars[1] = diameter,
        // vars[2] = charge,        n/a
        // vars[3] = steps,
        // vars[4] = dimension,
        // vars[5] = temperature,   n/a
        // vars[6] = fixed,         n/a
        // vars[7] = lattice,
        // vars[8] = avoid,         n/a
        // vars[9] = save           n/a
        String pyexecrms = "python plotrms.py";
        String rmsDataPath = "";
        String titletext = "";
        BufferedImage imagerms = null;

        Pair<Boolean, String> result = data.createData(folder, fexec, true);
        textArea.setText(result.getValue());

        if (result.getKey() == false)
            return;

        int steps = Integer.valueOf(vars[3]);
        int dimension = Integer.valueOf(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") )
            titletext = "Fixed source lattice particles, ";
        else if ( vars[6].equals("f") && vars[7].equals("-") )
            titletext = "Fixed source free particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("l") )
            titletext = "Spread out lattice particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("-") )
            titletext = "Spread out free particles, ";
                
        rmsDataPath = path
            + "\\" + "rms_"
            + dimension + "D_"
            + steps + "S.xy";

        Pyplot pyplotrms = new Pyplot();
        String[] filerms = new String[]{rmsDataPath};
        textArea.setText(pyplotrms.createPlot(folder, filerms, dimension, pyexecrms, true));
        String imgFile = "jpyplotRMS" + dimension + "D_" + steps + "S.png";
        // GET IMAGE FROM PYDPLOT.READPYPLOT()
        imagerms = pyplotrms.readPyPlot(new File(path + "/" + imgFile));

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - R_rms Calculation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + dimension + "D, " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(chartWidth/2-150,0,chartWidth/2+150,newFontSize);

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
