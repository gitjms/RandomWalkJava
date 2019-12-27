
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

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Fortran and Python code execution,
 * image file creation, image converting and reading
 */
@SuppressWarnings("ALL")
class Execution {

    private String language;
    private int screenHeight;
    private Runtime runtime;
    private boolean running;
    private JFrame frame;
    private int exitVal;

    /**
     * Initiating variables
     */
    Execution(String language) {
        this.setLanguage(language);
        setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    /**
     * method for Path tracing Fortran and Python execution and image creation
     * @param folder datafolder "C:/RWDATA"
     * @param path datapath "C:/RWDATA"
     * @param fexec Fortran executable "walk.exe"
     * @param pyexec1d Python executable "plot1d.py"
     * @param pyexec2d Python executable "plot2d.py"
     * @param pyexec3d Python executable "plot3d.py"
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    void executePath(File folder, String path, String fexec, String pyexec1d, String pyexec2d,
                     String pyexec3d, Data data, String[] vars) {
        /*
        * FROM SCENEPATHTRACING
        * vars from user:
        * vars[0] = which simulation,   USER
        * vars[1] = particles,          USER
        * vars[2] = diameter,           n/a
        * vars[3] = steps,              USER
        * vars[4] = dimension,          USER
        * vars[5] = calcfix or sawplot, USER
        * vars[6] = fixed,              USER
        * vars[7] = lattice,            USER
        * vars[8] = save                n/a
        */
        pyexec1d = "python ".concat(pyexec1d);
        pyexec2d = "python ".concat(pyexec2d);
        pyexec3d = "python ".concat(pyexec3d);
        this.setFrame();
        String yDataPath = null;
        String zDataPath = null;
        String titletext = null;
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) return;

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") ) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen hilahiukkaset, " : "Fixed source lattice particles, ";
        } else if ( vars[6].equals("f") && vars[7].equals("-") ) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen vapaat hiukkaset, " : "Fixed source free particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("l") ) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut hilahiukkaset, " : "Spread out lattice particles, ";
        } else if ( vars[6].equals("-") && vars[7].equals("-") ) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut vapaat hiukkaset, " : "Spread out free particles, ";
        }

        String xDataPath = "x_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".pdf");
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
            yDataPath =  "y_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            if ( dimension == 2 ) {
                command = new String[]{"cmd","/c", pyexec2d, xDataPath, yDataPath};
            }
        }
              
        /*
        * 3D DATA
        */
        if ( dimension == 3 ) {
            zDataPath =  "z_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            command = new String[]{"cmd","/c", pyexec3d, xDataPath, yDataPath, zDataPath};
        }

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 200);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - liikeradat" : "Random Walk - Path Tracing");
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + (this.getLanguage().equals("fin") ? " askelta\n" :" steps\n"));
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int)(labelFont.getSize() * 2.0);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        /*
        * PLOT
        */
        assert image != null;
        this.getFrame().setSize(this.getChartWidth(), this.getChartHeight()+this.getYMargin());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getChartHeight()-2*this.getYMargin())/2.0));
        Image image2 = image.getScaledInstance(this.getChartWidth(), this.getChartHeight(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        Box vbox = Box.createVerticalBox();
        vbox.setOpaque(true);
        vbox.setBackground(Color.WHITE);
        vbox.add(titleLabel);
        vbox.add(figLabel);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        figLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.getFrame().add(vbox);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * method for Diffusion Fortran and Python execution and image creation
     * @param folder datafolder "C:/RWDATA"
     * @param path datapath "C:/RWDATA"
     * @param fexec Fortran executable "walk.exe"
     * @param pyexecdiff2d Python executable "plotdiff2d.py"
     * @param pyexecdiff3d Python executable "plotdiff3d.py"
     * @param valikkoDiff VBox component in GUI to disable during run
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    void executeDiff(File folder, String path, String fexec, String pyexecdiff2d,
                     String pyexecdiff3d, VBox valikkoDiff, Data data, String[] vars) {
        /*
        * FROM SCENEDIFFUSION
        * vars from user:
        * vars[0] = which simulation,   USER
        * vars[1] = particles,          USER
        * vars[2] = diameter,           USER
        * vars[3] = steps,              n/a
        * vars[4] = dimension,          USER
        * vars[5] = calcfix or sawplot, n/a
        * vars[6] = fixed,              n/a
        * vars[7] = lattice,            USER
        * vars[8] = save                n/a
        */
        pyexecdiff2d = "python ".concat(pyexecdiff2d);
        pyexecdiff3d = "python ".concat(pyexecdiff3d);
        this.setFrame();
        File pdfFile = null;
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result)
            return;

        int particles = Integer.parseInt(vars[1]);
        double diameter = Double.parseDouble(vars[2]);
        int steps = Integer.parseInt(vars[3]);
        final int dimension = Integer.parseInt(vars[4]);

        String startDataDiff = "startDiff_" + dimension + "D_" + particles + "N.xy";
        String finalDataDiff = "finalDiff_" + dimension + "D_" + particles + "N.xy";

        if ( dimension == 2 ) {
            pdfFile = new File(path + "/jpyplotdiff2D_N" + particles + "_diam" + diameter + ".pdf");
            if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();
            command = new String[]{"cmd","/c", pyexecdiff2d, startDataDiff, finalDataDiff, this.getLanguage()};
        } else if ( dimension == 3 ) {
            pdfFile = new File(path + "/jpyplotdiff3D_N" + particles + "_diam" + diameter + ".pdf");
            if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();
            command = new String[]{"cmd","/c", pyexecdiff3d, startDataDiff, finalDataDiff, this.getLanguage()};
        }

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 300);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - diffuusiokuvaaja" : "Random Walk - Diffusion Plot");

        /*
        * PLOT
        */
        assert image != null;
        this.getFrame().setSize(this.getDiffWidth(), this.getDiffHeight());
        this.getFrame().setLocation(this.getXMarginSmall(), 0);
        Image image2 = image.getScaledInstance(this.getDiffWidth(), this.getDiffHeight()-this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
        valikkoDiff.setDisable(false);
    }

    /**
     * method for Rms calculation Fortran and Python execution and image creation
     * @param folder datafolder "C:/RWDATA"
     * @param path datapath "C:/RWDATA"
     * @param fexec Fortran executable "walk.exe"
     * @param pyexecrms Python executable "plotrms.py"
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    void executeRms(File folder, String path, String fexec,
                    String pyexecrms, Data data, String[] vars) {
        /*
        * FROM SCENEREALTIMERMS
        * vars from user:
        * vars[0] = which simulation,   n/a
        * vars[1] = particles,          n/a
        * vars[2] = diameter,           n/a
        * vars[3] = steps,              USER
        * vars[4] = dimension,          n/a
        * vars[5] = calcfix or sawplot, n/a
        * vars[6] = fixed,              USER
        * vars[7] = lattice             n/a
        * vars[8] = save                n/a
        */
        pyexecrms = "python ".concat(pyexecrms);
        this.setFrame();
        String titletext = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }

        if (!result) return;

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if ( vars[6].equals("f") && vars[7].equals("l") ) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen hilahiukkaset" : "Fixed source lattice particles";
        } else if ( vars[6].equals("f") && vars[7].equals("-") ) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen vapaat hiukkaset" : "Fixed source free particles";
        } else if ( vars[6].equals("-") && vars[7].equals("l") ) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut hilahiukkaset" : "Spread out lattice particles";
        } else if ( vars[6].equals("-") && vars[7].equals("-") ) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut vapaat hiukkaset" : "Spread out free particles";
        }

        String rmsDataPath = "rms_" + dimension + "D_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplotRMS" + dimension + "D_" + steps + "S.pdf");
        if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();

        String[] command = new String[]{"cmd","/c", pyexecrms, rmsDataPath, this.getLanguage()};

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 300);

        this.getFrame().setTitle(this.getLanguage()
            .equals("fin") ? "Satunnaiskulku - rms-laskenta - " + titletext : "Random Walk - R_rms Calculation - " + titletext);

        assert image != null;
        this.getFrame().setSize(this.getChartWidth(), this.getChartHeight());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getChartHeight())/2.0)-getYMargin());
        Image image2 = image.getScaledInstance(this.getChartWidth(), this.getChartHeight()+this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * method for 1D distance Fortran and Python execution and image creation
     * @param folder datafolder "C:/RWDATA"
     * @param path datapath "C:/RWDATA"
     * @param fexec Fortran executable "walk.exe"
     * @param pyexecrms Python executable "plotrms.py"
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    void execute1Ddist(File folder, String path, String fexec, String pyexec1d, Data data, String[] vars) {
        /*
         * FROM SCENE1Ddist
         * vars from user:
         * vars[0] = which simulation,  USER
         * vars[1] = particles,         USER
         * vars[2] = diameter,          n/a
         * vars[3] = steps,             USER
         * vars[4] = dimension,         n/a
         * vars[5] = calcfix or sawplot,n/a
         * vars[6] = fixed,             n/a
         * vars[7] = lattice,           USER
         * vars[8] = save               n/a
         */
        pyexec1d = "python ".concat(pyexec1d);
        this.setFrame();
        String titletext = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) return;

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if ( vars[7].equals("l") ) {
            titletext = this.getLanguage().equals("fin") ? "Hilahiukkaset" : "Lattice particles";
        } else if ( vars[7].equals("-") ) {
            titletext = this.getLanguage().equals("fin") ? "Vapaat hiukkaset" : "Free particles";
        }

        String xDataPath = "x_path" + "1D_" + particles + "N_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplot1Ddist_N" + particles + "_S" + steps + ".pdf");
        if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();

        String[] command = new String[]{"cmd","/c", pyexec1d, xDataPath, this.getLanguage()};

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 200);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - 1D-etäisyys - " + titletext : "Random Walk - 1D Distance - " + titletext);

        assert image != null;
        this.getFrame().setSize(this.getBigChartWidth(), this.getBigChartHeight());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getBigChartHeight())/2.0)-getYMargin());
        Image image2 = image.getScaledInstance(this.getBigChartWidth(), this.getBigChartHeight()+this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * method for Saw Fortran and Python execution and image creation
     * @param folder datafolder "C:/RWDATA"
     * @param path datapath "C:/RWDATA"
     * @param fexec Fortran executable "walk.exe"
     * @param pyexecsaw2d Python executable "plotsaw2d.py"
     * @param pyexecsaw3d Python executable "plotsaw3d.py"
     * @param frame JFrame for image
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    boolean executeSAW(File folder, String path, String fexec, String pyexecsaw2d,
                    String pyexecsaw3d, VBox valikkoSAW, Data data, String[] vars, boolean iscbmc) {
        /*
         * FROM SCENEPATHTRACING
         * vars from user:
         * vars[0] = which simulation,  n/a
         * vars[1] = particles,         n/a
         * vars[2] = diameter,          n/a
         * vars[3] = steps,             USER
         * vars[4] = dimension,         USER
         * vars[5] = calcfix or sawplot,n/a
         * vars[6] = fixed,             n/a
         * vars[7] = lattice,           n/a
         * vars[8] = save               n/a
         */

        pyexecsaw2d = "python ".concat(pyexecsaw2d);
        pyexecsaw3d = "python ".concat(pyexecsaw3d);
        this.setFrame();
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, iscbmc, true);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) return false;

        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        String dataPath;
        if (iscbmc) dataPath = "cbmc_" + dimension + "D_" + steps + "S.xy";
        else dataPath = "saw_" + dimension + "D.xy";

        File pdfFile = new File(path + "/jpyplotSAW" + dimension + "D.pdf");
        if ( Files.exists(pdfFile.toPath()) ) pdfFile.delete();

        /*
         * 2D DATA
         */
        if ( dimension == 2 ) {
            command = new String[]{"cmd","/c", pyexecsaw2d, dataPath, this.getLanguage()};
        }

        /*
         * 3D DATA
         */
        if ( dimension == 3 ) {
            command = new String[]{"cmd","/c", pyexecsaw3d, dataPath, this.getLanguage()};
        }

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, 1, 1, dimension, 50, 300);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - SAW-liikeradat" : "Random Walk - SAW Path Tracing");
        /*
         * PLOT
         */
        assert image != null;
        this.getFrame().setSize(this.getChartWidth(), this.getSawHeight());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight()-this.getSawHeight())/2.0));
        Image image2 = image.getScaledInstance(this.getChartWidth(), this.getSawHeight()-this.getYMargin(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
        valikkoSAW.setDisable(false);

        return true;
    }

    /**
     *
     * @param folder datafolder "C:/RWDATA"
     * @param command command string array to execute
     * @param pdfFile image file to get
     * @param particles number of particles from vars
     * @param steps number of steps from vars
     * @param dim dimension from vars
     * @param fac time factor
     * @return pdf image file
     */
    BufferedImage createPdf(File folder, String[] command, File pdfFile, int particles, int steps, int dim, int fac, int dpi) {

        BufferedImage image;

        this.setRuntime(Runtime.getRuntime());
        runtimeStart();

        /*
         * CREATE IMAGE
         */
        try {
            this.getRuntime().exec(command, null, folder);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        while (true) {
            if (Files.notExists(pdfFile.toPath())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (Files.exists(pdfFile.toPath())) {
                while (true) {
                    image = null;
                    if (!pdfFile.canRead()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if (pdfFile.canRead()) {
                        /*
                         * WAIT FOR THE PDF FILE
                         */
                        try {
                            Thread.sleep((long) (Math.log10(particles*steps)*Math.pow(dim,2.0))*fac);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        try {
                            PDDocument document = PDDocument.load(pdfFile);
                            PDFRenderer renderer = new PDFRenderer(document);
                            image = renderer.renderImageWithDPI(0, dpi, ImageType.RGB);
                            document.close();
                        } catch (IOException ex) {
                            //System.out.println(ex.getMessage());
                        }
                    }
                    if (image != null) break;
                }
                if (image != null) break;
            }
        }
        return image;
    }

    /**
     *
     * @param frame the JFrame to set
     */
    private void setFrame() {
        this.frame = new JFrame();
        //this.frame.getContentPane().removeAll();
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setIconImage(new ImageIcon(Execution.class.getResource("/icon64.png")).getImage());
		//this.frame.setIconImage(new ImageIcon(getClass().getResource("/icon64.png")).getImage());
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
    private int getBigChartWidth() {return 900 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the chartHeight
     */
    @Contract(pure = true)
    private int getBigChartHeight() { return 650 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the chartWidth
     */
    @Contract(pure = true)
    private int getChartWidth() {return 800 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the chartHeight
     */
    @Contract(pure = true)
    private int getChartHeight() { return 700 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the diffWidth
     */
    @Contract(pure = true)
    private int getDiffWidth() { return 530 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the diffHeight
     */
    @Contract(pure = true)
    private int getDiffHeight() { return 950 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the diffHeight
     */
    @Contract(pure = true)
    private int getSawHeight() { return 800 / (int) Screen.getMainScreen().getRenderScale(); }

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
    private int getXMarginSmall() { return 50 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the XMarginBig
     */
    @Contract(pure = true)
    private int getXMarginBig() { return 200 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginSmall
     */
    @Contract(pure = true)
    private int getYMargin() { return 10 / (int) Screen.getMainScreen().getRenderScale(); }

    /**
     * @return the YMarginSmall
     */
    @Contract(pure = true)
    private int getYMarginBig() { return 30 / (int) Screen.getMainScreen().getRenderScale(); }

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

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

    /**
     * @return the exitVal
     */
    @Contract(pure = true)
    private int getExitVal() { return this.exitVal; }

    /**
     * @param exitVal the exitVal to set
     */
    private void setExitVal(int exitVal) { this.exitVal = exitVal; }
}
