
package randomwalkjava;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 * 
 * Class for GUI main view logo and helptexts
 */
class HelpText {

    /**
    * @return MAIN STAGE HELP TEXT
    */
    String menu() {

        return " \n Button 'RMS vs SQRT(N)' shows a control panel with which you can\n"
            + " calculate root mean square (rms) distances of random walk particles.\n\n"
            + " Program plots 'rms<dist>' versus 'sqrt(steps)'.\n\n"
            + " Every run will save the data in a file replacing the previous one if\n"
            + " the options remain the same.\n\n"
            + " One run produces a data file ending with '.xy', and an image file in\n"
            + " '.pdf' format (600dpi). The files will be in the 'C:/RWDATA' folder.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'PATH TRACING' will show a control panel with which you can\n"
            + " plot different random walk path simulations. One run produces an\n"
            + " image file and four or five data files; two ending with '.xy', and\n"
            + " two or three ending with axis indication, i.e. '.x', '.y', or '.z',\n"
            + " depending on dimension user chooses. The image is in '.pdf' format\n"
            + " (600dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'REAL TIME RMS' opens a bigger window in which will be an\n"
            + " animation of fixed source random walk. Program loops the number of\n"
            + " steps user defines until user stops the run.\n\n"
            + " During the animation there will be an image with two real time plots\n"
            + " running different graphics: 'rms and sqrt(N) vs. walks' and either\n"
            + " 'rms standard normal distribution' or 'rms normal distribution'. No\n"
            + " files will be saved automatically.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'MMC DIFFUSION' also opens a bigger window in which will be an\n"
            + " animation if user so chooses.\n\n"
            + " User may choose between animation or plotting. The former only\n"
            + " creates one data file for its own use, ending with '.xy', and no\n"
            + " image will be saved. The latter creates an image file with start and\n"
            + " final positions of particles. In that case there will be four or five\n"
            + " data files; two ending with '.xy', and two or three ending with axis\n"
            + " indication, i.e. '.x', '.y', or '.z', depending on dimension user\n"
            + " chooses. The image will be saved in '.pdf' format (600dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button '1D DISTANCE' shows a control panel with which you can plot\n"
            + " particle distances versus steps.\n"
            + " Every run will save the data in a file replacing the previous one if\n"
            + " the options remain the same.\n"
            + " One run produces two redundant data files ending with '.xy', one data\n"
            + " file ending with axis indication '.x', and an image file in '.pdf'\n"
            + " format (600dpi).\n\n";
    }
    /**
    * @return RMS CALCULATION HELP TEXT
    */
    String calculation() {

        return "\n Number of Steps is a positive integer.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " Toggle button 'FREE'/'LATTICE' makes particles move either freely\n"
            + " or in lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' safely closes the\n"
            + " application.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Program plots 'rms<distance>' versus 'sqrt(steps)'.\n\n"
            + " Every run will save the data in '.xy' format and an image file in\n"
            + " '.png' formata, replacing the previous ones.";
    }
    /**
    * @return PATH TRACING HELP TEXT
    */
    String pathtracing() {

        return "\n Number of particles is a positive integer, at least 1.\n\n"
            + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
            + " Charge is either 1, 2, or 3. With 0 charge there is no charge, i.e.\n"
            + " the particles may be deemed as 'neutral'. With 1 charge the particles\n"
            + " are all the same charge, i.e. they will repel each other. With 2\n"
            + " charges the particles are randomly created with negative or positive\n"
            + " charges, so some will repel each other while others will attract each\n"
            + " other.\n\n"
            + " Number of steps is a positive integer. It means the cumulative random\n"
            + " steps the particles take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTONS:\n\n"
            + " - 'FIXED'/'SPREAD' decides whether to create the particles starting\n"
            + "    from fixed source point (ordinate), or to create them spread out\n"
            + "    randomly in the area.\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in\n"
            + "    lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' safely closes the\n"
            + " application.\n";
    }
    /**
    * @return REAL TIME RMS HELP TEXT
    */
    String realtimerms() {

        return "\n Number of particles is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the cumulative random steps\n"
            + " the particles take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two means moving on a\n"
            + " plane with axes x and y, three means moving in a cube with axes x, y, and z.\n\n"
            + " -------------------------------------------------------------------------------------\n\n"
            + " Toggle Button 'STD NORM'/'NORM' changes the lower side plot between standard normal\n"
            + " distribution and normal distribution.\n\n"
            + " Button 'RUN' starts an animation. During the animation there will be an image with\n"
            + " two real time plots running different graphics: 'rms and sqrt(N) vs. walks' and\n"
            + " either 'rms standard normal distribution' or 'rms normal distribution'.\n\n"
            + " User can save the image by right-clicking the mouse and choosing 'save' or 'export'.\n\n"
            + " 'Save' formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', and 'pdf'.\n\n"
            + " 'Export' will save the both plot data in 'csv' format separately.\n\n"
            + " Button 'RUN' runs the animation, and button 'CLOSE' safely closes the application.\n";
    }
    /**
    * @return MMC DIFFUSION HELP TEXT
    */
    String mmc() {

        return "\n Number of particles is a positive integer, at least 1.\n\n"
            + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
            + " Charge is either 1, 2, or 3. With 0 charge there is no charge, i.e. the particles may\n"
            + " be deemed as 'neutral'. With 1 charge the particles are all the same charge, i.e. they\n"
            + " will repel each other. With 2 charges the particles are randomly created with negative\n"
            + " or positive charges, so some will repel each other while others will attract each\n"
            + " other.\n\n"
            + " Dimension is either 2 or 3. Two means moving on a plane with axes x and y, three means\n"
            + " moving in a cube with axes x, y, and z.\n\n"
            + " --------------------------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTON:\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in lattice form.\n\n"
            + " CHOICE BUTTONS:\n\n"
            + " - 'ANIMATION' (and later 'REMOVE BARRIER') starts an animation with first drawing the\n"
            + "    initial positions of particles and then waiting for the user to remove the barrier.\n"
            + "    Then the particles will start moving in real time. During the run there's also an\n"
            + "    image in which runs a plot of energy minimization. User can save the image by right-\n"
            + "    clicking the mouse and choosing 'save' or 'export'.\n"
            + "    'Save' formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', and 'pdf'.\n"
            + "    'Export' will save the energy data in 'csv' format.\n\n"
            + " - 'PLOT' only creates an image of diffusion walk with two subplots: a start and final\n"
            + "    positions of particles.\n\n"
            + " Button 'CLOSE' safely closes the application.\n";
    }
    /**
     * @return 1D DISTANCE HELP TEXT
     */
    String distance1D() {

        return "\n Number of particles is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the number of\n"
            + " random steps the particles take while moving.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTON:\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in\n"
            + "    lattice form.\n";
    }

    /**
    * @return LOGO
    */
    String welcome() {

        return "\n\n"
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
                    + "                 |       Helsingin  yliopisto       |                \n"
                    + "       ////      |               2019               |    ////  ////  \n"
                    + "       ////      |                                  |   ////  ////   \n"
                    + "       ////       ----------------------------------   //// ////     \n"
                    + "       ////          ////  ////  ////      ////       ///////        \n"
                    + "       ////  ////   ////  ////   ////     ////       ////////        \n"
                    + "       //// ////// ////  ////////////    ////       ////   ////      \n"
                    + "       /////// ///////  ////     ////   ////       ////    ////      \n"
                    + "       //////  //////  ////      ////  /////////  ////     /////       ";
    }
}
