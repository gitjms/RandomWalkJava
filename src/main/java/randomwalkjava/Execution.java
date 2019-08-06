
package randomwalkjava;

import com.sun.glass.ui.Screen;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for image file creation, Python code execution and image reading
 */
public class Execution {

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

    private Runtime runtime;
    private boolean running;

    public void runtimeStart() {
        this.running = true;
    }
    public boolean runtimeIsRunning() {
        return this.running;
    }
    public void stopRuntime() {
        this.running = false;
        this.runtime.exit(0);
    }

    /**
     * empty constructor
     */
    public Execution() {
    }

    public void executePath(File folder, String path, String fexec,
        String pyexec1d, String pyexec2d, String pyexec3d, JFrame frame,
        Data data, String[] vars ) {
        /**
        * FROM SCENEPATHTRACING
        * vars from user:
        * vars[0] = particles,  USER
        * vars[1] = diameter,   USER
        * vars[2] = charge,     USER
        * vars[3] = steps,      USER
        * vars[4] = dimension,  USER
        * vars[5] = mmc,        USER
        * vars[6] = fixed,      USER
        * vars[7] = lattice,    USER
        * vars[8] = save        n/a
        */
        pyexec1d = "python ".concat(pyexec1d);
        pyexec2d = "python ".concat(pyexec2d);
        pyexec3d = "python ".concat(pyexec3d);
        String xDataPath;
        String yDataPath = null;
        String zDataPath;
        String titletext = null;
        BufferedImage image = null;
        String[] command = null;

        Boolean result = data.createData(folder, fexec, true);
        if (result == false)
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

        xDataPath = "x_path"
            + dimension + "D_"
            + particles + "N_"
            + steps + "S.x";

        File imgFile = new File(path + "\\" + "jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".png");

        /**
        * 1D DATA
        */
        if ( dimension == 1 ) {
            command = new String[]{"cmd","/c", pyexec1d, xDataPath};
        }

        /**
        * 2D DATA
        */
        if ( dimension == 2 || dimension == 3 ) {
            yDataPath =  "y_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.y";
            if ( dimension == 2 ) {
                command = new String[]{"cmd","/c", pyexec2d, xDataPath, yDataPath};
            }
        }
              
        /**
        * 3D DATA
        */
        if ( dimension == 3 ) {
            zDataPath =  "z_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.z";
            command = new String[]{"cmd","/c", pyexec3d, xDataPath, yDataPath, zDataPath};
        }

        /**
        * CREATE IMAGE
        */
        this.runtime = Runtime.getRuntime();
        runtimeStart();
        try {
            this.runtime.exec(command, null, folder);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        /**
        * GET IMAGE
        */
        try {
            Thread.sleep(1000*(int) Math.log10((double) particles));
            image = ImageIO.read(imgFile);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
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

    public void executeMMC(File folder, String path, String fexec, String pyexecmmc2d,
        String pyexecmmc3d, VBox valikkoMMC, JFrame frame, Data data, String[] vars ) {
        /**
        * FROM SCENEMMC
        * vars from user:
        * vars[0] = particles,     USER
        * vars[1] = diameter,      USER
        * vars[2] = charge,        USER
        * vars[3] = steps,         n/a
        * vars[4] = dimension,     USER
        * vars[5] = mmc,           n/a
        * vars[6] = fixed,         n/a
        * vars[7] = lattice,       USER
        * vars[8] = save           n/a
        */
        pyexecmmc2d = "python ".concat(pyexecmmc2d);
        pyexecmmc3d = "python ".concat(pyexecmmc3d);
        File imgFile = null;
        BufferedImage image = null;
        String[] command = null;

        Boolean result = data.createData(folder, fexec, true);
        if (result == false)
            return;

        int particles = parseInt(vars[0]);
        double diameter = parseDouble(vars[1]);
        final int dimension = parseInt(vars[4]);

        String startDataMMC = "startMMC_"
            + dimension + "D_"
            + particles + "N.xy";
        String finalDataMMC = "finalMMC_"
            + dimension + "D_"
            + particles + "N.xy";

        if ( dimension == 2 ) {
            imgFile =  new File(path + "\\" + "jpyplotmmc2D_N" + particles + "_diam" + diameter + ".png");
            command = new String[]{"cmd","/c", pyexecmmc2d, startDataMMC, finalDataMMC};
        } else if ( dimension == 3 ) {
            imgFile =  new File(path + "\\" + "jpyplotmmc3D_N" + particles + "_diam" + diameter + ".png");
            command = new String[]{"cmd","/c", pyexecmmc3d, startDataMMC, finalDataMMC};
        }
        
        this.runtime = Runtime.getRuntime();
        runtimeStart();
        try {
            this.runtime.exec(command, null, folder);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            Thread.sleep(1000*(int) Math.log10((double) particles));
            image = ImageIO.read(imgFile);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    public void executeRms(File folder, String path, String fexec,
        String pyexecrms, JFrame frame, Data data, String[] vars ) {
        /**
        * FROM SCENEREALTIMERMS
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
        pyexecrms = "python ".concat(pyexecrms);
        String rmsDataPath = null;
        String titletext = null;
        BufferedImage image = null;
        File imgFile = null;

        Boolean result = data.createData(folder, fexec, true);

        if (result == false)
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
                
        rmsDataPath = path
            + "\\" + "rms_"
            + dimension + "D_"
            + steps + "S.xy";

        String[] command = new String[]{"cmd","/c", pyexecrms, rmsDataPath};
        this.runtime = Runtime.getRuntime();
        runtimeStart();
        try {
            this.runtime.exec(command, null, folder);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        imgFile = new File(path + "\\" + "jpyplotRMS" + dimension + "D_" + steps + "S.png");
        /**
        * GET IMAGE
        */
        try {
            Thread.sleep(1000*(int) Math.log10((double) steps));
            image = ImageIO.read(imgFile);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - R_rms Calculation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + dimension + "D, " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.chartWidth/2-this.b,0,this.chartWidth,newFontSize);

        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setBounds(this.d, (this.screenHeight-this.chartHeight)/2-this.c, this.chartWidth, this.chartHeight);
        frame.pack();
        frame.setVisible(true);
    }
}
