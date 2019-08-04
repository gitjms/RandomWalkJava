
package randomwalkjava;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for code thread failure message
 */
public class Message extends Thread {
    public void run() {
         System.out.println(" Program is closing due to errors.");
      }
}