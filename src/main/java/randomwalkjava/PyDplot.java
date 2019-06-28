
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

public class PyDplot {

    public PyDplot() {
    }

    public String createPlot(File folderPath, String[] files, int dim, String executable) {
        String teksti = "";
        String[] command = null;

        try {
            if ( dim == 2 )
                command = new String[]{"cmd","/c", executable,
                    files[0], files[1], files[2], files[3]};
            else if ( dim == 3 )
                command = new String[]{"cmd","/c", executable,
                    files[0], files[1], files[2]};

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
                        teksti = teksti + "\n" + line;
                }

                exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println(" Python execution ended with no errors");
                } else {
                    System.out.println(" Python execution ended with error code " + exitVal);
                    runtime.exit(exitVal);
                }
                fos.flush();
                fos.close();
            }
            
            //runtime.addShutdownHook(new Message());

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return teksti;
    }

    public BufferedImage readPyPlot(File filePath){
    
        BufferedImage image = null;

        try {
            System.out.println(filePath);
            image = ImageIO.read(filePath);
        } catch (IOException ex) {
            Logger.getLogger(PyDplot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return image;
    }
}
