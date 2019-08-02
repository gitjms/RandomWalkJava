
package randomwalkjava;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Pyplot {

    public Pyplot() {
    }

    public String createPlot(File folderPath, String[] files, int dim,
        String executable, boolean rms, boolean mmc) {

        String teksti = "";
        String[] command = null;

        try {
            if ( dim == 1 || rms == true )
                command = new String[]{"cmd","/c", executable,
                    files[0]};
            if ( dim == 2 && rms == false && mmc == false )
                command = new String[]{"cmd","/c", executable,
                    files[0], files[1]};
            else if ( dim == 3 && rms == false && mmc == false )
                command = new String[]{"cmd","/c", executable,
                    files[0], files[1], files[2]};
            else if ( mmc == true )
                command = new String[]{"cmd","/c", executable,
                    files[0], files[1]};

            FileOutputStream fos = new FileOutputStream(command[0]);
            Runtime runtime = Runtime.getRuntime();

            // print the state of the program
            System.out.println(" Python execution begins...");
            Process process = runtime.exec(command, null, folderPath);
            
            int exitVal;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()))) {

                StreamGobbler errorGobbler = new StreamGobbler(
                    process.getErrorStream(), "ERROR ");
                errorGobbler.start();
                String line = null;

                StreamGobbler outputGobbler = new StreamGobbler(
                    process.getInputStream(), "", fos);
                outputGobbler.start();

                while ((line = input.readLine()) != null){
                    System.out.println(line);
                    if (teksti.isEmpty())
                        teksti = line;
                    else
                        teksti = teksti + "\n" + line + "\n";
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println(" Python execution ended with no errors");
                } else {
                    System.out.println(" Python execution ended with error code " + exitVal);
                    runtime.addShutdownHook(new Message());
                    runtime.exit(exitVal);
                }
                fos.flush();
                fos.close();
            }

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return teksti;
    }

    public BufferedImage readPyPlot(File filePath){
    
        BufferedImage image = null;
        System.out.println(filePath);
        try {
            image = ImageIO.read(filePath);
        } catch (IOException ex) {
            Logger.getLogger(Pyplot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return image;
    }
}
