package randomwalkjava;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for Fortan code error stream and input stream reading in Data class
 */
class StreamGobbler extends Thread {

    private final InputStream is;
    private final String type;
    private final OutputStream os;
    
    StreamGobbler(InputStream is) {
        this.is = is;
        this.type = "ERROR ";
        this.os = null;
    }

 /*   StreamGobbler(InputStream is, OutputStream redirect) {
        this.is = is;
        this.type = "";
        this.os = redirect;
    }*/

    @Override
    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);

            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null){
                if (pw != null)
                    pw.println(line);
                System.out.println(type + line);
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
}
