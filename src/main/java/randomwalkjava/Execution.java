
package randomwalkjava;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Contract;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for image file creation, Fortran and Python code
 * execution and image reading
 */
@SuppressWarnings("ALL")
class Execution {

    private int screenHeight;
    private Runtime runtime;
    private boolean running;

    /**
     * Initiating variables
     */
    Execution() {
        setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    /**
     * method for Path tracing Fortran and Python execution and image creation
     * @param folder datafolder C:/RWDATA
     * @param path datapath C:/RWDATA
     * @param fexec Fortran executable walk.exe
     * @param pyexec1d Python executable plot1d.py
     * @param pyexec2d Python executable plot2d.py
     * @param pyexec3d Python executable plot3d.py
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    void executePath(File folder, String path, String fexec,
                     String pyexec1d, String pyexec2d, String pyexec3d, JFrame frame,
                     Data data, String[] vars) {
        /*
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

        Boolean result = false;
        try {
            result = data.createData(folder, fexec);
        } catch (Throwable ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!result)
            return;

        int particles = parseInt(vars[0]);
        int steps = parseInt(vars[3]);
        int dimension = parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") ) {
            titletext = "Fixed source lattice particles, ";
        } else if ( vars[6].equals("f") && vars[7].equals("-") ) {
            titletext = "Fixed source free particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("l") ) {
            titletext = "Spread out lattice particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("-") ) {
            titletext = "Spread out free particles, ";
        }

        xDataPath = "x_path"
            + dimension + "D_"
            + particles + "N_"
            + steps + "S.x";

        File imgFile = new File(path + "\\" + "jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".png");

        /*
        * 1D DATA
        */
        if ( dimension == 1 ) {
            command = new String[]{"cmd","/c", pyexec1d, xDataPath};
        }

        /*
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
              
        /*
        * 3D DATA
        */
        if ( dimension == 3 ) {
            zDataPath =  "z_path"
                + dimension + "D_"
                + particles + "N_"
                + steps + "S.z";
            command = new String[]{"cmd","/c", pyexec3d, xDataPath, yDataPath, zDataPath};
        }

        /*
        * CREATE IMAGE
        */
        this.setRuntime(Runtime.getRuntime());
        runtimeStart();
        try {
            assert command != null;
            this.getRuntime().exec(command, null, folder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        /*
        * GET IMAGE
        */
        while (true) {
            if (Files.notExists(imgFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    image = ImageIO.read(imgFile);
                } catch (IOException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - Path Tracing");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.getChartWidth()/2-this.getA(),0, this.getChartWidth(),newFontSize);
        /*
        * PLOT
        */
        assert image != null;
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setLocation(0, (this.getScreenHeight()-this.getChartHeight())/2);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * method for MMC Fortran and Python execution and image creation
     * @param folder datafolder C:/RWDATA
     * @param path datapath C:/RWDATA
     * @param fexec Fortran executable walk.exe
     * @param pyexecmmc2d Python executable plotmmc2d.py
     * @param pyexecmmc3d Python executable plotmmc3d.py
     * @param valikkoMMC VBox component in GUI to disable during run
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    void executeMMC(File folder, String path, String fexec, String pyexecmmc2d,
                    String pyexecmmc3d, VBox valikkoMMC, JFrame frame, Data data, String[] vars) {
        /*
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

        Boolean result = false;
        try {
            result = data.createData(folder, fexec);
        } catch (Throwable ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!result)
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
        
        this.setRuntime(Runtime.getRuntime());
        runtimeStart();
        try {
            assert command != null;
            this.getRuntime().exec(command, null, folder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        while (true) {
            if (Files.notExists(imgFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(imgFile.toPath())) {
                try {
                    image = ImageIO.read(imgFile);
                } catch (IOException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - MMC Diffusion Plot");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /*
        * PLOT
        */
        assert image != null;
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(figLabel);
        frame.repaint();
        frame.setLocation(100, 10);
        frame.pack();
        frame.setVisible(true);
        valikkoMMC.setDisable(false);
    }

    /**
     * method for Rms calculation Fortran and Python execution and image creation
     * @param folder datafolder C:/RWDATA
     * @param path datapath C:/RWDATA
     * @param fexec Fortran executable walk.exe
     * @param pyexecrms Python executable plotrms.py
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    void executeRms(File folder, String path, String fexec,
                    String pyexecrms, JFrame frame, Data data, String[] vars) {
        /*
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
        String rmsDataPath;
        String titletext = null;
        BufferedImage image = null;
        File imgFile;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec);
        } catch (Throwable ex) {
            Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!result)
            return;

        int steps = parseInt(vars[3]);
        int dimension = parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") ) {
            titletext = "Fixed source lattice particles, ";
        } else if ( vars[6].equals("f") && vars[7].equals("-") ) {
            titletext = "Fixed source free particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("l") ) {
            titletext = "Spread out lattice particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("-") ) {
            titletext = "Spread out free particles, ";
        }
                
        rmsDataPath = path
            + "/" + "rms_"
            + dimension + "D_"
            + steps + "S.xy";

        String[] command = new String[]{"cmd","/c", pyexecrms, rmsDataPath};
        this.setRuntime(Runtime.getRuntime());
        runtimeStart();
        try {
            this.getRuntime().exec(command, null, folder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        imgFile = new File(path + "\\" + "jpyplotRMS" + dimension + "D_" + steps + "S.png");
        /*
        * GET IMAGE
        */
        while (true) {
            if (Files.notExists(imgFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(imgFile.toPath())) {
                try {
                    image = ImageIO.read(imgFile);
                } catch (IOException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }

        frame.getContentPane().removeAll();
        frame.setTitle("Random Walk - R_rms Calculation");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + dimension + "D, " + steps + " steps");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.5);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.getChartWidth()/2-this.getA(),0, this.getChartWidth(),newFontSize);

        assert image != null;
        ImageIcon figIcn = new ImageIcon(image);
        JLabel figLabel = new JLabel(figIcn);
        frame.add(titleLabel);
        frame.add(figLabel);
        frame.repaint();
        frame.setLocation(0, (this.getScreenHeight()-this.getChartHeight())/2);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * sets runtime running to true
     */
    private void runtimeStart() {
        this.setRunning(true);
    }

    /**
     * @return isRunning
     */
    boolean runtimeIsRunning() {
        return isRunning();
    }

    /**
     * sets runtime running to false and
     * exits runtime
     */
    void stopRuntime() {
        this.setRunning(false);
        this.getRuntime().exit(0);
    }

    /**
     * @return the chartWidth
     */
    @Contract(pure = true)
    private int getChartWidth() {return 750; }

    /**
     * @return the chartHeight
     */
    @Contract(pure = true)
    private int getChartHeight() { return 750; }

    /**
     * @return the mmcWidth
     */
    @Contract(pure = true)
    private int getMmcWidth() { return 300; }

    /**
     * @return the mmcHeight
     */
    @Contract(pure = true)
    private int getMmcHeight() { return 500; }

    /**
     * @return the screenHeight
     */
    @Contract(pure = true)
    private int getScreenHeight() {
        return screenHeight;
    }

    /**
     * @param screenHeight the screenHeight to set
     */
    private void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * @return the a
     */
    @Contract(pure = true)
    private int getA() { return 100; }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() {
        return runtime;
    }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the running
     */
    @Contract(pure = true)
    private boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) {
        this.running = running;
    }
}
