package randomwalkjava;

import java.io.*;

public class StreamGobbler extends Thread {

    InputStream is;
    String type;
    
    StreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }
    
    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null){
                System.out.println(type + ">" + line);
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
}
