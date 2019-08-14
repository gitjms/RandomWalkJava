
package randomwalkjava;

import javafx.scene.layout.VBox;
import com.sun.glass.ui.Screen;
import org.apache.pdfbox.rendering.ImageType;
import org.jetbrains.annotations.Contract;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Fortran and Python code execution,
 * image file creation, image converting and reading
 */
@SuppressWarnings("ALL")
class Execution {

    private int screenHeight;
    private Runtime runtime;
    private boolean running;
    private JFrame frame;
    private Image iconImg;

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
        this.setFrame(frame);
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

        File pdfFile = new File(path + "\\" + "jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".pdf");
        if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();

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
            if (Files.notExists(pdfFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(pdfFile.toPath())) {
                try {
                    PDDocument document = PDDocument.load(pdfFile);
                    PDFRenderer renderer = new PDFRenderer(document);
                    image = renderer.renderImageWithDPI(0,600, ImageType.RGB);
                    document.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                break;
            }
        }

        this.getFrame().getContentPane().removeAll();
        this.getFrame().setTitle("Random Walk - Path Tracing");
        this.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + " steps\n");
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 1.3);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setBounds(this.getChartWidth()/2-this.getXMarginBig(),0, this.getChartWidth(),newFontSize);
        /*
        * PLOT
        */
        assert image != null;
        this.getFrame().setSize(this.getChartWidth(), this.getChartHeight());
        this.getFrame().setLocation(0, (this.getScreenHeight()-this.getChartHeight())/2);
        Image image2 = image.getScaledInstance(this.getChartWidth(), this.getChartHeight()-this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(titleLabel);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
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
                    String pyexecmmc3d, JFrame frame, VBox valikkoMMC, Data data, String[] vars) {
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
        this.setFrame(frame);
        File pdfFile = null;
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
            pdfFile = new File(path + "\\" + "jpyplotmmc2D_N" + particles + "_diam" + diameter + ".pdf");
            if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();
            command = new String[]{"cmd","/c", pyexecmmc2d, startDataMMC, finalDataMMC};
        } else if ( dimension == 3 ) {
            pdfFile = new File(path + "\\" + "jpyplotmmc3D_N" + particles + "_diam" + diameter + ".pdf");
            if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();
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
            if (Files.notExists(pdfFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(pdfFile.toPath())) {
                try {
                    PDDocument document = PDDocument.load(pdfFile);
                    PDFRenderer renderer = new PDFRenderer(document);
                    image = renderer.renderImageWithDPI(0,600, ImageType.RGB);
                    document.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                break;
            }
        }

        this.getFrame().getContentPane().removeAll();
        this.getFrame().setTitle("Random Walk - MMC Diffusion Plot");
        this.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /*
        * PLOT
        */
        assert image != null;
        this.getFrame().setSize(this.getMmcWidth(), this.getMmcHeight());
        this.getFrame().setLocation(this.getXMarginSmall(), 0);
        Image image2 = image.getScaledInstance(this.getMmcWidth(), this.getMmcHeight()-this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
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
        this.setFrame(frame);
        String rmsDataPath;
        String titletext = null;
        BufferedImage image = null;

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
            titletext = "Fixed source lattice particles";
        } else if ( vars[6].equals("f") && vars[7].equals("-") ) {
            titletext = "Fixed source free particles";
        } else if ( vars[6].equals("-") && vars[7].equals("l") ) {
            titletext = "Spread out lattice particles";
        } else if ( vars[6].equals("-") && vars[7].equals("-") ) {
            titletext = "Spread out free particles";
        }
                
        rmsDataPath = path
            + "/" + "rms_"
            + dimension + "D_"
            + steps + "S.xy";

        File pdfFile = new File(path + "\\" + "jpyplotRMS" + dimension + "D_" + steps + "S.pdf");
        if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();

        String[] command = new String[]{"cmd","/c", pyexecrms, rmsDataPath};
        this.setRuntime(Runtime.getRuntime());
        runtimeStart();
        try {
            this.getRuntime().exec(command, null, folder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        /*
        * GET IMAGE
        */
        while (true) {
            if (Files.notExists(pdfFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Execution.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (Files.exists(pdfFile.toPath())) {
                try {
                    PDDocument document = PDDocument.load(pdfFile);
                    PDFRenderer renderer = new PDFRenderer(document);
                    image = renderer.renderImageWithDPI(0,600, ImageType.RGB);
                    document.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                break;
            }
        }


        this.getFrame().getContentPane().removeAll();
        this.getFrame().setTitle("Random Walk - R_rms Calculation - " + titletext);
        this.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        assert image != null;
        this.getFrame().setSize(this.getChartWidth(), this.getChartHeight());
        this.getFrame().setLocation(0, (this.getScreenHeight()-this.getChartHeight())/2-getYMargin());
        Image image2 = image.getScaledInstance(this.getChartWidth(), this.getChartHeight()+this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * @param frame the frame to set
     */
    private void setFrame(JFrame frame) {
        this.frame = frame;
        this.frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.png"));
    }

    /**
     * @return the frame
     */
    JFrame getFrame() { return frame; }

    /**
     * sets runtime running to true
     */
    private void runtimeStart() { this.setRunning(true); }

    /**
     * @return isRunning
     */
    boolean runtimeIsRunning() { return isRunning(); }

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
    private int getChartWidth() {return 600 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the chartHeight
     */
    @Contract(pure = true)
    private int getChartHeight() { return 500 / (int) Screen.getMainScreen().getPlatformScaleY(); }

    /**
     * @return the mmcWidth
     */
    @Contract(pure = true)
    private int getMmcWidth() { return 450 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the mmcHeight
     */
    @Contract(pure = true)
    private int getMmcHeight() { return 800 / (int) Screen.getMainScreen().getPlatformScaleY(); }

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
     * @return the XMarginSmall
     */
    @Contract(pure = true)
    private int getXMarginSmall() { return 50 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the XMarginBig
     */
    @Contract(pure = true)
    private int getXMarginBig() { return 150 / (int) Screen.getMainScreen().getPlatformScaleX(); }

    /**
     * @return the YMarginSmall
     */
    @Contract(pure = true)
    private int getYMargin() { return 10 / (int) Screen.getMainScreen().getPlatformScaleY(); }

    /**
     * @return the runtime
     */
    @Contract(pure = true)
    private Runtime getRuntime() { return runtime; }

    /**
     * @param runtime the runtime to set
     */
    private void setRuntime(Runtime runtime) { this.runtime = runtime; }

    /**
     * @return the running
     */
    @Contract(pure = true)
    private boolean isRunning() { return running; }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) { this.running = running; }
}
