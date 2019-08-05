
package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.image.BufferedImage;
import java.io.File;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for image file creation and preparing Python code executions
 */
public class Execution extends Data {

    private final int chartWidth = 860;
    private final int chartHeight = 605;
    private final int mmcWidth = 400;
    private final int mmcHeight = 600;
    private final int screenHeight = Screen.getMainScreen().getHeight();
    private final int a = 200
        / (int) Screen.getMainScreen().getRenderScale();
    private final int b = 150
        / (int) Screen.getMainScreen().getRenderScale();
    private final int c = 60
        / (int) Screen.getMainScreen().getRenderScale();
    private final int d = 20
        / (int) Screen.getMainScreen().getRenderScale();
    private final int e = 5
        / (int) Screen.getMainScreen().getRenderScale();
    
    private final String path = "C:\\DATA";
    private final String fexec = "walk.exe";
    private final File folder = new File("C:\\DATA");

    /**
     * empty constructor
     */
    public Execution() {
    }

    public void executePath(TextArea textArea, JFrame frame, Data data, String[] vars ) {
        /**
        * FROM SCENENOCALCULATION
        * vars from user:
        * vars[0] = particles,
        * vars[1] = diameter,
        * vars[2] = charge,
        * vars[3] = steps,
        * vars[4] = dimension,
        * vars[5] = mmc,
        * vars[6] = fixed,
        * vars[7] = lattice,
        * vars[8] = save       n/a
        */
        String pyexec1d = "python plot1d.py";
        String pyexec2d = "python plot2d.py";
        String pyexec3d = "python plot3d.py";
        String xDataPath;
        String yDataPath = null;
        String zDataPath;
        String titletext = null;
        BufferedImage image = null;

        Pair<Boolean, String> result = data.createData(this.folder, this.fexec, true);
        textArea.setText(result.getValue());
        if (result.getKey() == false)
            return;

        int particles = parseInt(vars[0]);
        int steps = parseInt(vars[3]);
        int dimension = parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") )
            titletext = "Fixed source lattice particles, ";
       else if ( vars[6].equals("f") && vars[7].equals("-") )
            titletext = "Fixed source free particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("l") )
            titletext = "Spread out lattice particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("-") )
            titletext = "Spread out free particles, ";

        xDataPath = this.path + "\\" + "x_path"
            + dimension + "D_"
            + particles + "N_"
            + steps + "S.x";

        String imgFile1d = "jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".png";

        /**
        * 1D DATA
        */
        if ( dimension == 1 ) {
            Pyplot pyplot1d = new Pyplot();
            String[] files1d = new String[]{xDataPath};
            textArea.setText(pyplot1d.createPlot(this.folder, files1d, dimension, pyexec1d, false, false));
            /**
            * GET IMAGE FROM PYDPLOT.READPYPLOT()
            */
            image = pyplot1d.readPyPlot(new File(this.path + "/" + imgFile1d));
        }

        /**
        * 2D DATA
        */
        if ( dimension == 2 || dimension == 3 ) {
            yDataPath = this.path + "\\" + "y_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.y";
            if ( dimension == 2 ) {
                Pyplot pyplot2d = new Pyplot();
                String[] files2d = new String[]{xDataPath, yDataPath};
                textArea.setText(pyplot2d.createPlot(this.folder, files2d, dimension, pyexec2d, false, false));
                /**
                * GET IMAGE FROM PYDPLOT.READPYPLOT()
                */
                image = pyplot2d.readPyPlot(new File(this.path + "/" + imgFile1d));
            }
        }
              
        /**
        * 3D DATA
        */
        if ( dimension == 3 ) {
            zDataPath = this.path + "\\" + "z_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.z";
            Pyplot pyplot3d = new Pyplot();
            String[] files3d = new String[]{xDataPath, yDataPath, zDataPath};
            textArea.setText(pyplot3d.createPlot(this.folder, files3d, dimension, pyexec3d, false, false));
            /**
            * GET IMAGE FROM PYDPLOT.READPYPLOT()
            */
            image = pyplot3d.readPyPlot(new File(this.path + "/" + imgFile1d));
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - Path Tracing");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.chartWidth/2-this.a,0,this.chartWidth/2+this.b,newFontSize);
        /**
        * PLOT
        */
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(this.d, (this.screenHeight-this.chartHeight)/2-this.c, this.chartWidth, this.chartHeight);
        frame.pack();
        frame.setVisible(true);
    }

    public void executeMMC(VBox valikkoMMC, TextArea textArea, JFrame frame, Data data, String[] vars ) {
        /**
        * FROM SCENENOCALCULATION
        * vars from user:
        * vars[0] = particles,
        * vars[1] = diameter,
        * vars[2] = charge,
        * vars[3] = steps,         n/a
        * vars[4] = dimension,
        * vars[5] = mmc,           n/a
        * vars[6] = fixed,         n/a
        * vars[7] = lattice,
        * vars[8] = save           n/a
        */
        String pyexec = "";
        String imgFile1d = "";
        BufferedImage image;

        Pair<Boolean, String> result = data.createData(this.folder, this.fexec, true);
        textArea.setText(result.getValue());
        if (result.getKey() == false)
            return;

        int particles = parseInt(vars[0]);
        double diameter = parseDouble(vars[1]);
        int dimension = parseInt(vars[4]);

        String startDataMMC = this.path + "\\" + "startMMC_"
            + dimension + "D_"
            + particles + "N.xy";
        String finalDataMMC = this.path + "\\" + "finalMMC_"
            + dimension + "D_"
            + particles + "N.xy";

        if ( dimension == 2 ) {
            pyexec = "python plotmmc2d.py";
            imgFile1d = "jpyplotmmc2D_N" + particles + "_diam" + diameter + ".png";
        } else if ( dimension == 3 ) {
            pyexec = "python plotmmc3d.py";
            imgFile1d = "jpyplotmmc3D_N" + particles + "_diam" + diameter + ".png";
        }
        
        Pyplot pyplotmmc = new Pyplot();
        String[] files = new String[]{startDataMMC, finalDataMMC};
        textArea.setText(pyplotmmc.createPlot(this.folder, files, dimension, pyexec, false, true));
        /**
        * GET IMAGE FROM PYDPLOT.READPYPLOT()
        */
        image = pyplotmmc.readPyPlot(new File(this.path + "/" + imgFile1d));

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - MMC Diffusion Plot");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /**
        * PLOT
        */
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(this.e, (this.mmcHeight-this.mmcHeight)/2, this.mmcWidth, this.mmcHeight);
        frame.pack();
        frame.setVisible(true);
        valikkoMMC.setDisable(false);
    }

    public void executeRms(TextArea textArea, JFrame frame, Data data, String[] vars ) {
        /**
        * FROM SCENECALCULATION
        * vars from user:
        * vars[0] = particles,     n/a
        * vars[1] = diameter,      n/a
        * vars[2] = charge,        n/a
        * vars[3] = steps,         USER
        * vars[4] = dimension,     USER
        * vars[5] = mmc,           n/a
        * vars[6] = fixed,         n/a
        * vars[7] = lattice,       USER
        * vars[8] = save           n/a
        */
        String pyexecrms = "python plotrms.py";
        String rmsDataPath;
        String titletext = null;
        BufferedImage imagerms;

        Pair<Boolean, String> result = data.createData(this.folder, this.fexec, true);
        textArea.setText(result.getValue());

        if (result.getKey() == false)
            return;

        int steps = parseInt(vars[3]);
        int dimension = parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") )
            titletext = "Fixed source lattice particles, ";
        else if ( vars[6].equals("f") && vars[7].equals("-") )
            titletext = "Fixed source free particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("l") )
            titletext = "Spread out lattice particles, ";
        else if ( vars[6].equals("-") && vars[7].equals("-") )
            titletext = "Spread out free particles, ";
                
        rmsDataPath = this.path
            + "\\" + "rms_"
            + dimension + "D_"
            + steps + "S.xy";

        Pyplot pyplotrms = new Pyplot();
        String[] filerms = new String[]{rmsDataPath};
        textArea.setText(pyplotrms.createPlot(this.folder, filerms, dimension, pyexecrms, true, false));
        String imgFile = "jpyplotRMS" + dimension + "D_" + steps + "S.png";
        /**
        * GET IMAGE FROM PYDPLOT.READPYPLOT()
        */
        imagerms = pyplotrms.readPyPlot(new File(this.path + "/" + imgFile));

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - R_rms Calculation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + dimension + "D, " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.chartWidth/2-this.b,0,this.chartWidth,newFontSize);

        ImageIcon figIcn = new ImageIcon(imagerms);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(this.d, (this.screenHeight-this.chartHeight)/2-this.c, this.chartWidth, this.chartHeight);
        frame.pack();
        frame.setVisible(true);
    }
}
