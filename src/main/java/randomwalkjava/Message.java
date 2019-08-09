
package randomwalkjava;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for thread failure message
 */
class Message extends Thread {
    @Override
    public void run() {
         System.out.println(" Program is closing due to errors.");
      }
}