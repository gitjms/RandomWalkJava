
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
    String menuFI() {

        return "\n Nappi 'LIIKERADAT' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " tulostaa erilaisia satunnaiskulkusimulaatioita. Yksi ajo tuottaa\n"
            + " kuvatiedoston sekä neljä tai viisi datatiedostoa päätteellä '.xy'.\n"
            + " Kuva on 'pdf'-tiedosto (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi '1D-ETÄISYYS' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " tulostaa hiukkasten etäisyydet askelten funktiona.\n\n"
            + " Jokainen ajo tallentaa datan tiedostoon korvaten edellisen mikäli\n"
            + " valintaparametrit ovat samat.\n\n"
            + " Yksi ajo tuottaa kolme datatiedostoa päätteellä '.xy', joista kaksi\n"
            + " on turhia, sekä kuvan 'pdf'-tiedostona (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'RMS vs SQRT(S)' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " tulostaa neliösummaetäisyydet (R_rms) satunnaiskulkuhiukkasille\n"
            + " odotusarvon funktiona'.\n\n"
            + " Jokainen ajo tallentaa datan tiedostoon korvaten edellisen mikäli\n"
            + " valintaparametrit ovat samat.\n\n"
            + " Yksi ajo tuottaa datatiedoston päätteellä '.xy', sekä kuvan 'pdf'-\n"
            + " formaatissa (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'REAALIAIKA-RMS' avaa isomman ikkunan, jolla tulee olemaan\n"
            + " animaatio keskitetyn lähteen satunnaiskulusta. Ohjelma silmukoi\n"
            + " käyttäjän valitsemaa askelmäärää kunnes käyttäjä lopettaa ajon.\n\n"
            + " Käyttäjä voi valita neliösummaetäisyys-jakauman tai diffuusio-\n"
            + " jakauman.\n\n"
            + " Animaation ajan ohessa pyörii kolme reaaliaikaista graafia\n"
            + " erillisessä kuvaikkunassa: ensimmäisessä on 'R_rms ja odotusarvo\n"
            + " ajojen funktiona', toisessa on joko 'R_rms standardi normaalijakauma'\n"
            + " tai 'Diffuusio normaalijakauma' riippuen käyttäjän valinnasta, ja\n"
            + " viimeisessä on 'Etäisyys-histogrammi'.\n\n"
            + " Mitään tiedostoa ei tallenneta automaattisesti.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'MMC-DIFFUUSIO' avaa isomman ikkunan, jossa pyörii animaatio\n"
            + " käyttäjän niin valitessa. Animaatio kuvaa hiukkasten diffuusio-\n"
            + " liikettä 2D-tasossa tai 3D-tilassa.\n\n"
            + " Käyttäjä voi valita animaation tai tulostuksen. Edellinen luo vain\n"
            + " yhden datatiedoston omaan käyttöönsä, päätteellä '.xy', eikä mitään\n"
            + " kuvaa tallenneta. Jälkimmäinen luo kuvatiedoston hiukkasten alku- ja\n"
            + " loppuasetelmista. Tässä tapauksessa tulee olemaan neljä tai viisi\n"
            + " datatiedostoa päätteellä '.xy'. Kuva tallentuu 'pdf'-tiedostona\n"
            + " (300dpi).\n\n"
            + " Animaation ajan ohessa pyörii kolme reaaliaikaista graafia\n"
            + " erillisessä kuvaikkunassa: ensimmäisessä on dynaaminen viskositeetti,\n"
            + " toisessa on diffuusiovakio, ja kolmannessa on energian minimointi.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'REAALIAIKA-SAW' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " joko ajaa reaaliaikaisia kuvaajia itseään välttelevästä satunnais-\n"
            + " kulusta tai tulostaa kuvan satunnaiskulun liikeradasta.\n\n"
            + " Käyttäjä voi valita animaation tai kuvatulostuksen. Edellinen\n"
            + " silmukoi satunnaisia askelmääriä kunnes käyttäjä lopettaa ajon.\n"
            + " Jälkimmäinen tulostaa kuvan itseään välttelevän kulun liikeradasta.\n\n"
            + " Ajot suoritetaan yhdellä hiukkasella. Reaaliaikakuvaajat eivät tuota\n"
            + " lainkaan tiedostoja. Liikeratakuvaaja tuottaa yhden datatiedoston\n"
            + " päätteellä '.xy' sekä yhden kuvatiedoston 'pdf'-muodossa.\n\n"
            + " Animaation ajan ohessa pyörii kolme reaaliaikaista graafia\n"
            + " erillisessä kuvaikkunassa: kaksi kuvaajaa otsikolla 'Itseään\n"
            + " välttelevä kulku', ja yksi otsikolla 'Etäisyys-histogrammi.\n\n"
            + " Ensimmäisessä SAW-kuaajassa on mukana liikkuva x-akseli, kun taas\n"
            + " toisessa kuvaajassa kasvava x-akseli on kiinnitetty origoon.\n\n"
            + " Liikeratakuva tallentuu 'pdf'-tiedostona (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Kaikki tiedostot tallentuvat kansioon 'C:/RWDATA'.\n";
    }
    /**
     * @return MAIN STAGE HELP TEXT
     */
    String menuEN() {

        return "\n Button 'PATH TRACING' will show a control panel with which user can\n"
            + " plot different random walk path simulations. One run produces an\n"
            + " image file and four or five data files ending with '.xy'. The image\n"
            + " is in 'pdf' format (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button '1D DISTANCE' shows a control panel with which user can plot\n"
            + " particle distances as function of steps.\n\n"
            + " Every run will save the data in a file replacing the previous one if\n"
            + " the option parameters remain the same.\n\n"
            + " One run produces three data files ending with '.xy', of which two are\n"
            + " redundant, and an image file in 'pdf' format (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'RMS vs SQRT(S)' shows a control panel with which user can\n"
            + " calculate root mean square (rms) distances of random walk particles\n"
            + " as funcion of expected value'.\n\n"
            + " Every run will save the data in a file replacing the previous one if\n"
            + " the option parameters remain the same.\n\n"
            + " One run produces a data file ending with '.xy', and an image file in\n"
            + " 'pdf' format (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'REAL TIME RMS' opens a bigger window in which will be an\n"
            + " animation of fixed source random walk. Program loops the number of\n"
            + " steps user defines until user stops the run.\n\n"
            + " User may choose between root mean squared distance distribution or\n"
            + " diffusion distribution.\n\n"
            + " During the animation there will be an image window with three real\n"
            + " time graphs running different graphics: the first is 'R_rms and\n"
            + " Expected Value as functions of Walks', the second is either 'R_rms\n"
            + " Standard Normal Distribution' or 'Diffusion Distribution' according\n"
            + " to user's choice, and the last one is 'Distance Histogram'.\n\n"
            + " No files will be saved automatically.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'MMC DIFFUSION' opens a bigger window in which will be an\n"
            + " animation if user so chooses. Animation depicts diffusion movement\n"
            + " of particles in 2D plane or 3D volume.\n\n"
            + " User may choose between animation or plotting. The former only\n"
            + " creates one data file for its own use, ending with '.xy', and no\n"
            + " image will be saved. The latter creates an image file with initial\n"
            + " and final positions of particles. In that case there will be four or\n"
            + " five data files ending with '.xy'. The plot will be saved in 'pdf'\n"
            + " format (300dpi).\n\n"
            + " During the animation there will be an image window with three real\n"
            + " time plots running different graphics: in the first graph is dynamic\n"
            + " viscosity, in the second graph is diffusion coefficient, and in the\n"
            + " last one is energy minimizing.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'REAL TIME SAW' will show a control panel with which user can\n"
            + " either run an animation with real time graphs of self-avoiding random\n"
            + " walk, or plot one graph of the path of random walk.\n\n"
            + " User may choose between animation or plotting. The former loops\n"
            + " random step runs until user stops the run. The latter plots an image\n"
            + " of self-avoiding walk path.\n\n"
            + " The runs are done with one particle. Real time graphs do not produce\n"
            + " files. Path plot will produce one data file ending with '.xy', and\n"
            + " one image file in 'pdf' format.\n\n"
            + " During the animation there will be an image window with three real\n"
            + " time plots running different graphics: two graphs under 'Self-\n"
            + " avoiding walk', and one under 'Distance Histogram'.\n\n"
            + " The first SAW graph is with comoving x-axis, while the other is with\n"
            + " increasing x-axis fixed to ordinate.\n\n"
            + " The plot image will be saved in 'pdf' format (300dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " All files will be in the 'C:/RWDATA' folder.\n";
    }
    /**
     * @return PATH TRACING HELP TEXT
     */
    String pathtracingFI() {

        return "\n Liikeradat\n"
            + " ----------\n\n"
            + " Hiukkasten lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Hiukkasten halkaisija on positiivinen reaaliluku väliltä ]0.0, 1.0[.\n\n"
            + " Hiukkasten varaus on  0, 1 tai 2. Varauksella 0 ei ole varausta, eli\n"
            + " hiukkaset voi ymmärtää 'neutraaleiksi'. Varauksella 1 hiukkasilla on\n"
            + " sama varaus, eli ne hylkivät toisiaan. Varauksella 2 hiukkasilla on\n"
            + " satunnaisesti luotu negatiivinen tai positiivinen varaus, jolloin\n"
            + " toiset hylkivät toisiaan ja toiset vetävät toisiaan puoleensa.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " hiukkasten liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " Ulottuvuus on 1, 2, or 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " VAIHTONAPIT:\n\n"
            + " - 'KESKITETTY'/'HAJAUTETTU' päättää luodaanko hiukkaset alkaen\n"
            + "    keskitetystä lähteestä (origo), vai luodaanko ne hajautetusti\n"
            + "    tasoon/tilaan.\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko hiukkaset vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return PATH TRACING HELP TEXT
     */
    String pathtracingEN() {

        return "\n Path Tracing\n"
            + " ------------\n\n"
            + " Number of particles is a positive integer, at least 1.\n\n"
            + " Diameter of particles is a positive real number on the interval\n"
            + " ]0.0, 1.0[.\n\n"
            + " Charge of particles is either 0, 1, or 2. With 0 charge there is no\n"
            + " charge, i.e. the particles may be deemed as 'neutral'. With 1 charge\n"
            + " the particles are all the same charge, i.e. they will repel each\n"
            + " other. With 2 charges the particles are randomly created with\n"
            + " negative or positive charges, so some will repel each other while\n"
            + " others will attract each other.\n\n"
            + " Number of steps is a positive integer. It means the random steps the\n"
            + " particles take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTONS:\n\n"
            + " - 'FIXED'/'SPREAD' decides whether to create the particles starting\n"
            + "    from fixed source point (ordinate), or to create them spread out\n"
            + "    randomly in the area/volume.\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in\n"
            + "    lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' closes the\n"
            + " application.\n";
    }
    /**
     * @return 1D DISTANCE HELP TEXT
     */
    String distance1DFI() {

        return "\n 1D etäisyys\n"
            + " -----------\n\n"
            + " Hiukkasten lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " hiukkasten liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " ----------------------------------------------------------------------\n\n"
            + " VAIHTONAPPI:\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko hiukkaset vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return 1D DISTANCE HELP TEXT
     */
    String distance1DEN() {

        return "\n 1D Distance\n"
            + " -----------\n\n"
            + " Number of particles is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the number of\n"
            + " random steps the particles take while moving.\n\n"
            + " ----------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTON:\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in\n"
            + "    lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' closes the\n"
            + " application.\n";
    }
    /**
    * @return RMS CALCULATION HELP TEXT
    */
    String calculationFI() {

        return "\n Rms vs. sqrt(S)\n"
            + " ---------------\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku.\n\n"
            + " Dimensio on 1, 2 tai 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " Vaihtonappi 'VAPAA'/'HILA' saa hiukkaset liikkumaan joko vapaasti\n"
            + " tai hilamuodossa.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Ohjelma tulostaa R_rms odotusarvon funktiona.\n\n"
            + " Kukin ajo tallentaa datan '.xy'-päätteellä ja kuvan 'pdf'-muodossa\n"
            + " korvaten edelliset tiedostot.\n";
    }
    /**
     * @return RMS CALCULATION HELP TEXT
     */
    String calculationEN() {

        return "\n Rms vs sqrt(S)\n"
            + " ---------------\n\n"
            + " Number of Steps is a positive integer.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " Toggle button 'FREE'/'LATTICE' makes particles move either freely\n"
            + " or in lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' closes the\n"
            + " application.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Program plots R_rms as a function of expected value.\n\n"
            + " Every run will save the data ending with '.xy' and an image file in\n"
            + " 'pdf' format, replacing the previous ones.\n";
    }
    /**
    * @return REAL TIME RMS HELP TEXT
    */
    String realtimermsFI() {

        return "\n Reaaliaika-rms\n"
            + " --------------\n\n"
            + " Hiukkasten lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " hiukkasten liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " Ulottuvuus on 1, 2 tai 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Vaihtonappi 'NORM. JAKAUMA'/'NORM. DISTRIB.' päättää onko\n"
            + " keskimmäinen kuvaaja R_rms-normaalijakauma vai diffuusio-\n"
            + " normaalijakauma.\n\n"
            + " Nappi 'AJA' aloittaa animaation. Animaation ajan ohessa pyörii kolme\n"
            + " reaaliaikaista graafia erillisessä kuvaikkunassa: 'R_rms ja odotusarvo\n"
            + " ajojen funktiona', 'R_rms standardi normaalijakauma' tai 'Diffuusio\n"
            + " normaalijakauma', ja 'Etäisyys-histogrammi'.\n\n"
            + " Käyttäjä voi tallentaa kuvan klikkaamalla sitä hiiren oikealla\n"
            + " korvalla ja valitsemalla 'save'.\n\n"
            + " Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " NAPPI 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return REAL TIME RMS HELP TEXT
     */
    String realtimermsEN() {

        return "\n Real Time Rms\n"
            + " -------------\n\n"
            + " Number of particles is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the repeated random\n"
            + " steps the particles take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Toggle Button 'NORM. DISTRIB.'/'DIFF. DISTRIB.' changes the middle\n"
            + " side plot between R_rms normal distribution and diffusion normal\n"
            + " distribution.\n\n"
            + " Button 'RUN' starts an animation. During the animation there will be\n"
            + " an image with two real time plots running different graphics:\n"
            + " 'R_rms and Expected Value as functions of Walks', 'R_rms Standard\n"
            + " Normal Distribution' or 'Diffusion Distribution', and 'Distance\n"
            + " Histogram'.\n\n"
            + " User can save the image by right-clicking the mouse and choosing\n"
            + " 'save'.\n\n"
            + " Save formats: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " Button 'CLOSE' closes the application.\n";
    }
    /**
    * @return MMC DIFFUSION HELP TEXT
    */
    String mmcFI() {

        return "\n Mmc-diffuusio\n"
            + " -------------\n\n"
            + " Hiukkasten lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Hiukkasten halkaisija on positiivinen reaaliluku väliltä ]0.0, 1.0[.\n\n"
            + " Hiukkasten varaus on 1 tai 2. Varauksella 1 hiukkasilla on sama\n"
            + " varaus, eli ne hylkivät toisiaan. Varauksella 2 hiukkasilla on\n"
            + " satunnaisesti luotu negatiivinen tai positiivinen varaus, jolloin\n"
            + " toiset hylkivät toisiaan ja toiset vetävät toisiaan puoleensa.\n\n"
            + " Ulottuvuus on 2 tai 3. Kaksi on kulkua xy-tasossa, kolme on kulkua\n"
            + " kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " VAIHTONAPIT:\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko hiukkaset vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " - 'PALLOT'/'YMPYRÄT' päättää hiukkasten kuvaesityksestä (3D-pallot vai\n"
            + "    2D-tasoympyrät).\n\n"
            + " VALINTANAPIT:\n\n"
            + " - 'ANIMAATIO' (myöhemmin 'JATKA') aloittaa animaation piirtämällä\n"
            + "    ensin hiukkasten alkuasetelman ja odottaen käyttäjää poistamaan\n"
            + "    rajan. Sitten hiukkaset alkavat liikkua reaaliajassa. Animaation\n"
            + "    ajan ohessa pyörii kolme reaaliaikaista graafia: dynaaminen\n"
            + "    viskositeetti, diffuusiovakio ja energian minimointi.\n\n"
            + "    Käyttäjä voi tallentaa kuvan klikkaamalla sitä hiiren oikealla\n"
            + "    korvalla ja valitsemalla 'save'.\n"
            + "    Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " - 'KUVAAJA' luo vain kuvan diffuusiokulusta kahdella graafilla:\n"
            + "    hiukkasten alku- ja loppuasetelmat.\n\n"
            + " NAPPI 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return MMC DIFFUSION HELP TEXT
     */
    String mmcEN() {

        return "\n Mmc Diffusion\n"
            + " -------------\n\n"
            + " Number of particles is a positive integer, at least 1.\n\n"
            + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
            + " Charge is either 1 or 2. With 1 charge the particles possess the\n"
            + " same charge, i.e. they will repel each other. With 2 charges the\n"
            + " particles are randomly created with negative or positive charges,\n"
            + " so some will repel each other while others will attract each\n"
            + " other.\n\n"
            + " Dimension is either 2 or 3. Two means moving on a plane with axes x\n"
            + " and y, three means moving in a cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTONS:\n\n"
            + " - 'FREE'/'LATTICE' decides whether the particles move freely or in\n"
            + "    lattice form.\n\n"
            + " - 'BALLS'/'CIRCLES' decides whether the particle images are 3D balls\n"
            + "    or 2D flat circles.\n\n"
            + " CHOICE BUTTONS:\n\n"
            + " - 'ANIMATION' (later 'CONTINUE') starts an animation with first\n"
            + "    drawing the initial positions of particles and then waiting\n"
            + "    for the user to remove the barrier. Then the particles will start\n"
            + "    moving in real time. During the animation there will be an image\n"
            + "    window with three real time plots: dynamic viscosity, diffusion\n"
            + "    coefficient, and energy minimizing. User can save the image by\n"
            + "    right-clicking the mouse and choosing 'save'.\n"
            + "    Save formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " - 'PLOT' only creates an image of diffusion walk with two subplots:\n"
            + "    a start and final positions of particles.\n\n"
            + " Button 'CLOSE' closes the application.\n";
    }
    /**
     * @return REAL TIME SAW HELP TEXT
     */
    String realtimesawFI() {

        return "\n Reaaliaika-saw\n"
            + " --------------\n\n"
            + " Ulottuvuus on 1, 2, or 3. Kaksi on kulkua xy-tasossa, kolme on\n"
            + " kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " ----------------------------------------------------------------\n\n"
            + " VALINTANAPIT:\n\n"
            + " - 'AJA' aloittaa animaation, jonka aikana ohessa pyörii kolme\n"
            + "    reaaliaikaista graafia: kaksi 'Itseään välttelevää kulkua'\n"
            + "    sekä 'Etäisyys-histogrammi.\n\n"
            + "    Käyttäjä voi tallentaa kuvan klikkaamalla sitä hiiren oikeal-\n"
            + "    la korvalla ja valitsemalla 'save'.\n"
            + "    Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps',\n"
            + "    'pdf'.\n\n"
            + " - 'KUVAAJA' luo vain kuvan itseään välttelevän kulun liikeradas-\n"
            + "    ta.\n\n"
            + " Nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return REAL TIME SAW HELP TEXT
     */
    String realtimesawEN() {

        return "\n Real Time Saw\n"
            + " -------------\n\n"
            + " Dimension is either 2 or 3. Two means moving on a plane with axes\n"
            + " x and y, three means moving in a cube with axes x, y, and z.\n\n"
            + " -----------------------------------------------------------------\n\n"
            + " CHOICE BUTTONS:\n\n"
            + " - 'RUN' starts an animation. During the animation there will be\n"
            + "    an image window with three real time plots: two of 'Self-\n"
            + "    avoiding walk', and one 'Distance Histogram'.\n\n"
            + "    User can save the image by right-clicking the mouse and\n"
            + "    choosing 'save'.\n"
            + "    Save formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps',\n"
            + "    'pdf'.\n\n"
            + " - 'PLOT' only creates an image of self-avoiding walk path.\n\n"
            + " Button 'CLOSE' closes the application.\n";
    }

    /**
    * @return LOGO
    */
    String welcomeFI() {

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

    /**
     * @return LOGO
     */
    String welcomeEN() {

        return "\n\n"
            + "        /////       ///       //    // ///        /////       //   //\n"
            + "       ///  //     ////      ///   // //////    ///    //    ///  ///\n"
            + "      ///    //   /////     ////  // ///   // ///      //   //// ////\n"
            + "     ///   //    /// //    ///// // ///    /////       //  //////////\n"
            + "    //////      ///  //   /// //// ///     ////        // /// //// //\n"
            + "   ///   //    // ----------------------------------  // ///  ///  //\n"
            + "  ///     //  // |                                  | / ///   //   //\n"
            + " ///     /// /// |           Jari Sunnari           |  ///         //\n"
            + "                 |         Bachelor  Thesis         |                \n"
            + "                 |                                  |                \n"
            + "                 |      University of Helsinki      |                \n"
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
