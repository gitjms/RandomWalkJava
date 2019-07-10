
package randomwalkjava;

public class HelpText {

    public HelpText() {
    }

    public String menu() {
        String text = " Button 'RRMS vs SQRT(N)' shows a control panel with which you can\n"
                    + " calculate root mean square distances (R_rms) of random walk particles.\n\n"
                    + " Program plots 'R_rms' versus 'sqrt(steps)'.\n\n"
                    + " Every run will save the data in a file replacing the previous one.\n\n"
                    + " ----------------------------------------------------------------------\n\n"
                    + " Button 'RANDOM WALK' shows a control panel with which you can plot\n"
                    + " different random walk simulations.\n\n"
                    + " You can choose to save the data or to only plot without saving.";
    
        return text;
    }

    public String calculation() {
        String text = " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Skip is a positive integer meaning jumping in the iteration steps.\n"
                    + " Iteration starts from skip, not from 1. No skip is 0 or 1.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " Program plots 'R_rms' versus 'sqrt(steps)'.\n\n"
                    + " Every run will save the data in a file replacing the previous one.\n\n"
                    + " You can save the image with 'Right-click + Save As...' or 'ctrl+S'.\n"
                    + " Saving formats are: PNG, JPEG, BMP, GIF, SVG, EPS, and PDF.";
    
        return text;
    }

    public String simulation() {
        String text = " Number of particles is a positive integer, at least 1.\n\n"
                    + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " - Avoid sets the particles to self avoiding mode.\n"
                    + " - Save toggle changes mode between realtime (no save) and save mode.\n"
                    + "   Real time doesn't save the data, but shows the path trace in real\n"
                    + "   time. Save mode saves the data, and you can plot the trace paths\n"
                    + "   by yourself.";
    
        return text;
    }

    public String animation() {
        String text = " Number of particles is a positive integer, at least 1.\n\n"
                    + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " - Avoid sets the particles to self avoiding mode.\n"
                    + " - Save toggle changes mode between realtime (no save) and save mode.\n"
                    + "   Real time doesn't save the data, but shows the path trace in real\n"
                    + "   time. Save mode saves the data, and you can plot the trace paths\n"
                    + "   by yourself.";
    
        return text;
    }

    public String mmc() {
        String text = " Number of particles is a positive integer, at least 1.\n\n"
                    + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
                    + " Steps is a positive integer. It means the cumulative random steps\n"
                    + " the particles take while moving.\n\n"
                    + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
                    + " means moving on a plane of x and y axes, three means moving in a\n"
                    + " cube of x, y, and z axes.\n\n"
                    + " --------------------------------------------------------------------\n\n"
                    + " - Avoid sets the particles to self avoiding mode.\n"
                    + " - Save toggle changes mode between realtime (no save) and save mode.\n"
                    + "   Real time doesn't save the data, but shows the path trace in real\n"
                    + "   time. Save mode saves the data, and you can plot the trace paths\n"
                    + "   by yourself.";
    
        return text;
    }

    public String welcome() {
        String text = "\n"
                    + "        /////       ///       //    // ///        /////       //   //\n" 
                    + "       ///  //     ////      ///   // //////    ///    //    ///  ///\n"
                    + "      ///    //   /////     ////  // ///   // ///      //   //// ////\n"
                    + "     ///   //    /// //    ///// // ///    /////       //  //////////\n"
                    + "    //////      ///  //   /// //// ///     ////        // /// //// //\n"
                    + "   ///   //    // ----------------------------------  // ///  ///  //\n"
                    + "  ///     //  // |                                  | / ///   //   //\n"
                    + " ///     /// /// |           Jari Sunnari           |  ///         //\n"
                    + "                 |       Kandidaatintutkielma       |                \n"
                    + "                 |                                  |                \n"
                    + "       ////      |               2019               |    ////  ////  \n"
                    + "       ////      |                                  |   ////  ////   \n"
                    + "       ////       ----------------------------------   //// ////     \n"
                    + "       ////          ////  ////  ////      ////       ///////        \n"
                    + "       ////  ////   ////  ////   ////     ////       ////////        \n"
                    + "       //// ////// ////  ////////////    ////       ////   ////      \n"
                    + "       /////// ///////  ////     ////   ////       ////    ////      \n"
                    + "       //////  //////  ////      ////  /////////  ////     /////       ";
    
        return text;
    }
}
