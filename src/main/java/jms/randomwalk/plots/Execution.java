package jms.randomwalk.plots;

import enums.IntSizes;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.rendering.ImageType;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import jms.randomwalk.datahandling.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.apache.maven.surefire.shade.booter.org.apache.commons.lang3.SystemUtils;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Fortran and Python code execution, image file creation, image converting and reading.
 */
public class Execution {

    private String language;
    private final boolean isWin;
    private int screenHeight;
    private Runtime runtime;
    private boolean running;
    private JFrame frame;

    /**
     * Initiating variables.
     * @param language which ui language: finnish or english
     */
    public Execution(String language) {
        this.setLanguage(language);
        this.isWin = SystemUtils.IS_OS_WINDOWS;
        setScreenHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
    }

    /**
     * Method for Path tracing Fortran and Python execution and image creation.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param path datapath "C:/RWDATA" or "home/user/RWDATA"
     * @param fexec Fortran executable "walk.exe" or "walkLx"
     * @param pyexec1d Python executable "plot1d.py"
     * @param pyexec2d Python executable "plot2d.py"
     * @param pyexec3d Python executable "plot3d.py"
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    public void executePath(File folder, String path, String fexec, String pyexec1d, String pyexec2d,
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
        if (this.isWin) {        
            pyexec1d = "python ".concat(pyexec1d);
            pyexec2d = "python ".concat(pyexec2d);
            pyexec3d = "python ".concat(pyexec3d);
        }
        this.setFrame();
        String yDataPath = null;
        String zDataPath;
        String titletext = null;
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) {
            return;
        }

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if (vars[6].equals("f") && vars[7].equals("l")) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen hilahiukkaset, " : "Fixed source lattice particles, ";
        } else if (vars[6].equals("f") && vars[7].equals("-")) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen vapaat hiukkaset, " : "Fixed source free particles, ";
        } else if (vars[6].equals("-") && vars[7].equals("l")) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut hilahiukkaset, " : "Spread out lattice particles, ";
        } else if (vars[6].equals("-") && vars[7].equals("-")) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut vapaat hiukkaset, " : "Spread out free particles, ";
        }

        String xDataPath = "x_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplot" + dimension + "D_N" + particles + "_S" + steps + ".pdf");
        if (Files.exists(pdfFile.toPath())) {
            pdfFile.delete();
        }

        /*
        * 1D DATA
        */
        if (dimension == 1 && this.isWin) {
            command = new String[]{"cmd", "/c", pyexec1d, xDataPath};
        } else if (dimension == 1) {
            command = new String[]{"python", pyexec1d, xDataPath};
        }

        /*
        * 2D DATA
        */
        if ((dimension == 2 || dimension == 3) && this.isWin) {
            yDataPath =  "y_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            if (dimension == 2) {
                command = new String[]{"cmd", "/c", pyexec2d, xDataPath, yDataPath};
            }
        } else if (dimension == 2 || dimension == 3) {
            yDataPath =  "y_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            if (dimension == 2) {
                command = new String[]{"python", pyexec2d, xDataPath, yDataPath};
            }
        }
              
        /*
        * 3D DATA
        */
        if (dimension == 3 && this.isWin) {
            zDataPath =  "z_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            command = new String[]{"cmd", "/c", pyexec3d, xDataPath, yDataPath, zDataPath};
        } else if (dimension == 3) {
            zDataPath =  "z_path" + dimension + "D_" + particles + "N_" + steps + "S.xy";
            command = new String[]{"python", pyexec3d, xDataPath, yDataPath, zDataPath};
        }

        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 200);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - liikeradat" : "Random Walk - Path Tracing");
        JLabel titleLabel = new JLabel(titletext + "N=" + particles + ", " + steps + (this.getLanguage().equals("fin") ? " askelta\n" : " steps\n"));
        java.awt.Font labelFont = titleLabel.getFont();
        int newFontSize = (int) (labelFont.getSize() * 2.0);
        titleLabel.setFont(new java.awt.Font(labelFont.getName(), java.awt.Font.PLAIN, newFontSize));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        /*
        * PLOT
        */
        assert image != null;
        this.getFrame().setSize(IntSizes.MDMSIZE.getIntSize(), IntSizes.SIZE.getIntSize() + IntSizes.SMLMRGN.getIntSize());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight() - IntSizes.SIZE.getIntSize() - 2 * IntSizes.SMLMRGN.getIntSize()) / 2.0));
        Image image2 = image.getScaledInstance(IntSizes.MDMSIZE.getIntSize(), IntSizes.SIZE.getIntSize(), Image.SCALE_AREA_AVERAGING);
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
     * Method for Diffusion Fortran and Python execution and image creation.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param path datapath "C:/RWDATA" or "home/user/RWDATA"
     * @param fexec Fortran executable "walk.exe" or "walkLx"
     * @param pyexecdiff2d Python executable "plotdiff2d.py"
     * @param pyexecdiff3d Python executable "plotdiff3d.py"
     * @param valikkoDiff VBox component in GUI to disable during run
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    public void executeDiff(File folder, String path, String fexec, String pyexecdiff2d,
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
        if (this.isWin) {
            pyexecdiff2d = "python ".concat(pyexecdiff2d);
            pyexecdiff3d = "python ".concat(pyexecdiff3d);
        }
        this.setFrame();
        File pdfFile = null;
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) {
            return;
        }

        int particles = Integer.parseInt(vars[1]);
        double diameter = Double.parseDouble(vars[2]);
        int steps = Integer.parseInt(vars[3]);
        final int dimension = Integer.parseInt(vars[4]);

        String startDataDiff = "startDiff_" + dimension + "D_" + particles + "N.xy";
        String finalDataDiff = "finalDiff_" + dimension + "D_" + particles + "N.xy";

        if (dimension == 2 && this.isWin) {
            pdfFile = new File(path + "/jpyplotdiff2D_N" + particles + "_diam" + diameter + ".pdf");
            if (Files.exists(pdfFile.toPath())) {
                pdfFile.delete();
            }
            command = new String[]{"cmd", "/c", pyexecdiff2d, startDataDiff, finalDataDiff, this.getLanguage()};
        } else if (dimension == 2) {
            pdfFile = new File(path + "/jpyplotdiff2D_N" + particles + "_diam" + diameter + ".pdf");
            if (Files.exists(pdfFile.toPath())) {
                pdfFile.delete();
            }
            command = new String[]{"python", pyexecdiff2d, startDataDiff, finalDataDiff, this.getLanguage()};
        } else if (dimension == 3 && this.isWin) {
            pdfFile = new File(path + "/jpyplotdiff3D_N" + particles + "_diam" + diameter + ".pdf");
            if (Files.exists(pdfFile.toPath())) {
                pdfFile.delete();
            }
            command = new String[]{"cmd", "/c", pyexecdiff3d, startDataDiff, finalDataDiff, this.getLanguage()};
        } else if (dimension == 3) {
            pdfFile = new File(path + "/jpyplotdiff3D_N" + particles + "_diam" + diameter + ".pdf");
            if (Files.exists(pdfFile.toPath())) {
                pdfFile.delete();
            }
            command = new String[]{"python", pyexecdiff3d, startDataDiff, finalDataDiff, this.getLanguage()};
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
        this.getFrame().setSize(IntSizes.DIFFW.getIntSize(), IntSizes.DIFFH.getIntSize());
        this.getFrame().setLocation(IntSizes.MDMMRGN.getIntSize(), 0);
        Image image2 = image.getScaledInstance(IntSizes.DIFFW.getIntSize(), IntSizes.DIFFH.getIntSize() - IntSizes.SMLMRGN.getIntSize(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
        valikkoDiff.setDisable(false);
    }

    /**
     * Method for Rms calculation Fortran and Python execution and image creation.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param path datapath "C:/RWDATA" or "home/user/RWDATA"
     * @param fexec Fortran executable "walk.exe" or "walkLx"
     * @param pyexecrms Python executable "plotrms.py"
     * @param data instance of Data class
     * @param vars user data from GUI
     */
    public void executeCalc(File folder, String path, String fexec, String pyexecrms, Data data, String[] vars) {
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
        if (this.isWin) {
            pyexecrms = "python ".concat(pyexecrms);
        }
        this.setFrame();
        String titletext = null;
        String[] command;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }

        if (!result) {
            return;
        }

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if (vars[6].equals("f") && vars[7].equals("l")) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen hilahiukkaset" : "Fixed source lattice particles";
        } else if (vars[6].equals("f") && vars[7].equals("-")) {
            titletext = this.getLanguage().equals("fin") ? "Keskitetyn lähteen vapaat hiukkaset" : "Fixed source free particles";
        } else if (vars[6].equals("-") && vars[7].equals("l")) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut hilahiukkaset" : "Spread out lattice particles";
        } else if (vars[6].equals("-") && vars[7].equals("-")) {
            titletext = this.getLanguage().equals("fin") ? "Hajautetut vapaat hiukkaset" : "Spread out free particles";
        }

        String rmsDataPath = "rms_" + dimension + "D_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplotRMS" + dimension + "D_" + steps + "S.pdf");
        if (Files.exists(pdfFile.toPath())) {
            pdfFile.delete();
        }

        if (this.isWin) {
            command = new String[]{"cmd", "/c", pyexecrms, rmsDataPath, this.getLanguage()};
        } else {
            command = new String[]{"python", pyexecrms, rmsDataPath, this.getLanguage()};
        }
        
        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 300);

        this.getFrame().setTitle(this.getLanguage()
            .equals("fin") ? "Satunnaiskulku - rms-laskenta - " + titletext : "Random Walk - R_rms Calculation - " + titletext);

        assert image != null;
        this.getFrame().setSize(IntSizes.MDMSIZE.getIntSize(), IntSizes.SIZE.getIntSize());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight() - IntSizes.SIZE.getIntSize()) / 2.0) - IntSizes.SMLMRGN.getIntSize());
        Image image2 = image.getScaledInstance(IntSizes.MDMSIZE.getIntSize(), IntSizes.SIZE.getIntSize() + IntSizes.SMLMRGN.getIntSize(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * Method for 1D distance Fortran and Python execution and image creation.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param path datapath "C:/RWDATA" or "home/user/RWDATA"
     * @param fexec Fortran executable "walk.exe" or "walkLx"
     * @param pyexec1d Python executable "plot1d.py"
     * @param data instance of Data class
     * @param vars user data from GUI via
     */
    public void execute1Ddist(File folder, String path, String fexec, String pyexec1d, Data data, String[] vars) {
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
        if (this.isWin) {
            pyexec1d = "python ".concat(pyexec1d);
        }
        this.setFrame();
        String titletext = null;
        String[] command;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, false, false);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) {
            return;
        }

        int particles = Integer.parseInt(vars[1]);
        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        if (vars[7].equals("l")) {
            titletext = this.getLanguage().equals("fin") ? "Hilahiukkaset" : "Lattice particles";
        } else if (vars[7].equals("-")) {
            titletext = this.getLanguage().equals("fin") ? "Vapaat hiukkaset" : "Free particles";
        }

        String xDataPath = "x_path" + "1D_" + particles + "N_" + steps + "S.xy";

        File pdfFile = new File(path + "/jpyplot1Ddist_N" + particles + "_S" + steps + ".pdf");
        if (Files.exists(pdfFile.toPath())) {
            pdfFile.delete();
        }

        if (this.isWin) {
            command = new String[]{"cmd", "/c", pyexec1d, xDataPath, this.getLanguage()};
        } else {
            command = new String[]{"python", pyexec1d, xDataPath, this.getLanguage()};
        }
        
        /*
         * GET IMAGE
         */
        BufferedImage image = createPdf(folder, command, pdfFile, particles, steps, dimension, 500, 200);

        this.getFrame().setTitle(this.getLanguage().equals("fin") ? "Satunnaiskulku - 1D-etäisyys - " + titletext : "Random Walk - 1D Distance - " + titletext);

        assert image != null;
        this.getFrame().setSize(IntSizes.BIGSIZE.getIntSize(), IntSizes.SMLSIZE.getIntSize());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight() - IntSizes.SMLSIZE.getIntSize()) / 2.0) - IntSizes.SMLMRGN.getIntSize());
        Image image2 = image.getScaledInstance(IntSizes.BIGSIZE.getIntSize(), IntSizes.SMLSIZE.getIntSize() + IntSizes.SMLMRGN.getIntSize(), Image.SCALE_AREA_AVERAGING);
        ImageIcon figIcn = new ImageIcon(image2);
        JLabel figLabel = new JLabel(figIcn);
        this.getFrame().add(figLabel);
        this.getFrame().repaint();
        this.getFrame().pack();
        this.getFrame().setVisible(true);
    }

    /**
     * Method for Saw Fortran and Python execution and image creation.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param path datapath "C:/RWDATA" or "home/user/RWDATA"
     * @param fexec Fortran executable "walk.exe" or "walkLx"
     * @param pyexecsaw2d Python executable "plotsaw2d.py"
     * @param pyexecsaw3d Python executable "plotsaw3d.py"
     * @param valikkoSAW VBox component in GUI to disable during run
     * @param data instance of Data class
     * @param vars user data from GUI via
     * @param ismcsaw whether is mc saw or saw
     * @return true or false
     */
    public boolean executeSAW(File folder, String path, String fexec, String pyexecsaw2d,
        String pyexecsaw3d, VBox valikkoSAW, Data data, String[] vars, boolean ismcsaw) {
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
        if (this.isWin) {
            pyexecsaw2d = "python ".concat(pyexecsaw2d);
            pyexecsaw3d = "python ".concat(pyexecsaw3d);
        }
        this.setFrame();
        String[] command = null;

        Boolean result = false;
        try {
            result = data.createData(folder, fexec, ismcsaw, true);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
        if (!result) {
            return false;
        }

        int steps = Integer.parseInt(vars[3]);
        int dimension = Integer.parseInt(vars[4]);

        String dataPath;
        if (ismcsaw) {
            dataPath = "mcsaw_" + dimension + "D_" + steps + "S.xy";
        } else {
            dataPath = "saw_" + dimension + "D.xy";
        }

        File pdfFile = new File(path + "/jpyplotSAW" + dimension + "D.pdf");
        if (Files.exists(pdfFile.toPath())) {
            pdfFile.delete();
        }

        if (dimension == 2 && this.isWin) {
            command = new String[]{"cmd", "/c", pyexecsaw2d, dataPath, this.getLanguage()};
        } else if (dimension == 2) {
            command = new String[]{"python", pyexecsaw2d, dataPath, this.getLanguage()};
        } else if (dimension == 3 && this.isWin) {
            command = new String[]{"cmd", "/c", pyexecsaw3d, dataPath, this.getLanguage()};
        } else if (dimension == 3) {
            command = new String[]{"python", pyexecsaw3d, dataPath, this.getLanguage()};
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
        this.getFrame().setSize(IntSizes.MDMSIZE.getIntSize(), IntSizes.MDMSIZE.getIntSize());
        this.getFrame().setLocation(0, (int) ((this.getScreenHeight() - IntSizes.MDMSIZE.getIntSize()) / 2.0));
        Image image2 = image.getScaledInstance(IntSizes.MDMSIZE.getIntSize(), IntSizes.MDMSIZE.getIntSize() - IntSizes.SMLMRGN.getIntSize(), Image.SCALE_AREA_AVERAGING);
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
     * Method for creating pdf image.
     * @param folder datafolder "C:/RWDATA" or "home/user/RWDATA"
     * @param command command string array to execute
     * @param pdfFile image file to get
     * @param particles number of particles from vars
     * @param steps number of steps from vars
     * @param dim dimension from vars
     * @param fac time factor
     * @param dpi image resolution
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
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (Files.exists(pdfFile.toPath())) {
                while (true) {
                    image = null;
                    if (!pdfFile.canRead()) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if (pdfFile.canRead()) {
                        /*
                         * WAIT FOR THE PDF FILE
                         */
                        try {
                            TimeUnit.MILLISECONDS.sleep((long) (Math.log10(particles * steps) * Math.pow(dim, 2.0)) * fac);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        try {
                            try (PDDocument document = PDDocument.load(pdfFile)) {
                                PDFRenderer renderer = new PDFRenderer(document);
                                image = renderer.renderImageWithDPI(0, dpi, ImageType.RGB);
                            }
                        } catch (IOException ex) {
                        }
                    }
                    if (image != null) {
                        break;
                    }
                }
                if (image != null) {
                    break;
                }
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
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setIconImage(new ImageIcon(Execution.class.getResource("/icon64.png")).getImage());
    }

    /**
     * @return the frame
     */
    JFrame getFrame() {
        return frame;
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
    public boolean runtimeIsRunning() {
        return isRunning();
    }

    /**
     * sets runtime running to false and
     * exits runtime
     */
    public void stopRuntime() {
        this.setRunning(false);
        this.getRuntime().exit(0);
    }

    /**
     * @return the screenHeight
     */
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
     * @return the runtime
     */
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
    private boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    private void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the language
     */
    private String getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) {
        this.language = language;
    }
}
