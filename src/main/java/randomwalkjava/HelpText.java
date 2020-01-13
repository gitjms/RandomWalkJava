
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
            + " kuvatiedoston sekä yhdestä kolmeen datatiedostoa päätteellä '.xy'.\n"
            + " Kuva on 'pdf'-tiedosto (200dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi '1D-ETÄISYYS' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " tulostaa kulkijoiden etäisyydet askelten funktiona.\n\n"
            + " Jokainen ajo tallentaa datan tiedostoon korvaten edellisen mikäli\n"
            + " valintaparametrit ovat samat.\n\n"
            + " Yksi ajo tuottaa yhden datatiedoston päätteellä '.xy', sekä kuvan\n"
			+ " 'pdf'-tiedostona (200dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'RMS vs SQRT(S)' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " tulostaa neliösummaetäisyyden (Rrms) satunnaiskulkijoille\n"
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
            + " erillisessä kuvaikkunassa: ensimmäisessä on 'Rrms ja odotusarvo\n"
            + " ajojen funktiona', toisessa on joko 'Rrms normaalijakauma'\n"
            + " tai 'Diffuusio normaalijakauma' riippuen käyttäjän valinnasta, ja\n"
            + " viimeisessä on 'Etäisyys-histogrammi'.\n\n"
            + " Mitään tiedostoa ei tallenneta automaattisesti.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'DIFFUUSIO' avaa isomman ikkunan, jossa pyörii animaatio\n"
            + " käyttäjän niin valitessa. Animaatio kuvaa kulkijoiden diffuusio-\n"
            + " liikettä 2D-tasossa tai 3D-tilassa.\n\n"
            + " Käyttäjä voi valita animaation tai tulostuksen. Edellinen luo vain\n"
            + " yhden datatiedoston omaan käyttöönsä, päätteellä '.xy', eikä mitään\n"
            + " kuvaa tallenneta. Jälkimmäinen luo kuvatiedoston kulkijoiden alku- ja\n"
            + " loppuasetelmista. Tässä tapauksessa tulee olemaan kaksi\n"
            + " datatiedostoa päätteellä '.xy'. Kuva tallentuu 'pdf'-tiedostona\n"
            + " (300dpi).\n\n"
            + " Animaation ajan ohessa pyörii kolme reaaliaikaista graafia\n"
            + " erillisessä kuvaikkunassa: ensimmäisessä on energian minimointi,\n"
            + " toisessa on diffuusiovakio, ja kolmannessa on dynaaminen viskosi-\n"
            + " teetti. Viskositeetin sijaan voi myös valita liikkuvuuskuvaajan.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Nappi 'REAALIAIKA-SAW' näyttää ohjauspaneelin, jolla käyttäjä voi\n"
            + " joko ajaa reaaliaikaisia kuvaajia itseään välttelevästä satunnais-\n"
            + " kulusta tai tulostaa kuvan satunnaiskulun liikeradasta.\n\n"
            + " Käyttäjä voi valita kuvaajat tai liikeratatulostuksen.\n\n"
            + " Käyttäjä voi valita yksinkertaisen kulun tai biasoidun Monte Carlo\n"
            +  "-kulun. Edellinen silmukoi satunnaisia askelmääriä kunnes käyttäjä\n"
            + " lopettaa ajon. Jälkimmäinen käyttäjän asettamia askelmääriä.\n"
            + " Kuvaaja-valinta tulostaa kuvan yksinkertaisen kulun liikeradasta.\n\n"
            + " Ajot suoritetaan yhdellä kulkijalla. Reaaliaikakuvaajat eivät tuota\n"
            + " lainkaan tiedostoja. Liikeratakuvaaja tuottaa yhden datatiedoston\n"
            + " päätteellä '.xy' sekä yhden kuvatiedoston 'pdf'-muodossa.\n\n"
            + " Ajojen aikana ohessa pyörii kolme reaaliaikaista graafia\n"
            + " erillisessä kuvaikkunassa: kaksi kuvaajaa otsikolla 'Itseään\n"
            + " välttelevä kulku', ja yksi otsikolla 'Etäisyys-histogrammi.\n\n"
            + " Ensimmäisessä SAW-kuaajassa on mukana liikkuva x-akseli, kun taas\n"
            + " toisessa kuvaajassa kasvava x-akseli on kiinnitetty origoon.\n\n"
            + " Liikeratakuva tallentuu 'pdf'-tiedostona (300dpi).\n\n"
            + " Käyttäjä voi myös valita reaaliaikaisen tehokkuuskuvaajan, josta"
            + " ei tallennu mitään tiedostoja.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Kaikki tiedostot tallentuvat kansioon 'C:/RWDATA'.\n";
    }
    /**
     * @return MAIN STAGE HELP TEXT
     */
    String menuEN() {

        return "\n Button 'PATH TRACING' shows a control panel with which user can\n"
            + " plot different random walk path simulations. One run produces an\n"
            + " image file and one to three data files ending with '.xy'. The image\n"
            + " is in 'pdf' format (200dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button '1D DISTANCE' shows a control panel with which user can plot\n"
            + " walker distances as function of steps.\n\n"
            + " Every run will save the data in a file replacing the previous one if\n"
            + " the option parameters remain the same.\n\n"
            + " One run produces one data file ending with '.xy', and an image file\n"
			+ " in 'pdf' format (200dpi).\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'RMS vs SQRT(S)' shows a control panel with which user can\n"
            + " calculate root mean square (Rrms) distance of random walkers as\n"
            + " funcion of expected value'.\n\n"
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
            + " time graphs running different graphics: the first is 'Rrms and\n"
            + " Expected Value as functions of Walks', the second is either 'Rrms\n"
            + " Standard Normal Distribution' or 'Diffusion Distribution' according\n"
            + " to user's choice, and the last one is 'Distance Histogram'.\n\n"
            + " No files will be saved automatically.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'DIFFUSION' opens a bigger window in which will be an\n"
            + " animation if user so chooses. Animation depicts diffusion movement\n"
            + " of walkers in 2D plane or 3D volume.\n\n"
            + " User may choose between animation or plotting. The former only\n"
            + " creates one data file for its own use, ending with '.xy', and no\n"
            + " image will be saved. The latter creates an image file with initial\n"
            + " and final positions of walkers. In that case there will be two\n"
            + " data files ending with '.xy'. The plot will be saved in 'pdf'\n"
            + " format (300dpi).\n\n"
            + " During the animation there will be an image window with three real\n"
            + " time plots running different graphics: in the first graph is energy\n"
            + " minimizing, in the second graph is diffusion coefficient, and in the\n"
            + " last one is dynamic viscosity. Instead of viscosity user may choose\n"
			+ " electrical mobility.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Button 'REAL TIME SAW' will show a control panel with which user can\n"
            + " either run an animation with real time graphs of self-avoiding random\n"
            + " walk, or plot one graph of the path of random walk.\n\n"
            + " User may choose between graphs or path plot.\n\n"
            + " User may choose a simple self-avoiding walk or a biased Monte Carlo\n"
            + " walk. The former loops random step runs until user stops the run. The\n"
            + " latter loops the amount of steps user defines. Plot option plots an\n"
            + " image of self-avoiding walk path.\n\n"
            + " The runs are done with one walker. Real time graphs do not produce\n"
            + " files. Path plot will produce one data file ending with '.xy', and\n"
            + " one image file in 'pdf' format.\n\n"
            + " During the animation there will be an image window with three real\n"
            + " time plots running different graphics: two graphs under 'Self-\n"
            + " avoiding walk', and one under 'Distance Histogram'.\n\n"
            + " The first SAW graph is with a comoving x-axis, while the other is\n"
            + " with increasing x-axis fixed to ordinate.\n\n"
            + " The plot image will be saved in 'pdf' format (300dpi).\n\n"
            + " User may also choose a real time efficiency graph, which will not"
            + " produce any files.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " All files will be in the 'C:/RWDATA' folder.\n";
    }
    /**
     * @return PATH TRACING HELP TEXT
     */
    String pathtracingFI() {

        return "\n Liikeradat\n"
            + " ----------\n\n"
            + " Kulkijoiden lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " kulkijoiden liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " Ulottuvuus on 1, 2, or 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " VAIHTONAPIT:\n\n"
            + " - 'KESKITETTY'/'HAJAUTETTU' päättää luodaanko kulkijat alkaen\n"
            + "    keskitetystä lähteestä (origo), vai luodaanko ne hajautetusti\n"
            + "    tasoon/tilaan.\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko kulkijat vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return PATH TRACING HELP TEXT
     */
    String pathtracingEN() {

        return "\n Path Tracing\n"
            + " ------------\n\n"
            + " Number of walkers is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the random steps the\n"
            + " walkers take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTONS:\n\n"
            + " - 'FIXED'/'SPREAD' decides whether to create the walkers starting\n"
            + "    from fixed source point (ordinate), or to create them spread out\n"
            + "    randomly in the area/volume.\n\n"
            + " - 'FREE'/'LATTICE' decides whether the walkers move freely or in\n"
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
            + " Kulkijoiden lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " kulkijoiden liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " ----------------------------------------------------------------------\n\n"
            + " VAIHTONAPPI:\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko kulkijat vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return 1D DISTANCE HELP TEXT
     */
    String distance1DEN() {

        return "\n 1D Distance\n"
            + " -----------\n\n"
            + " Number of walkers is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the number of\n"
            + " random steps the walkers take while moving.\n\n"
            + " ----------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTON:\n\n"
            + " - 'FREE'/'LATTICE' decides whether the walkers move freely or in\n"
            + "    lattice form.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' closes the\n"
            + " application.\n";
    }
    /**
    * @return RMS CALCULATION HELP TEXT
    */
    String calculationFI() {

        return "\n Rrms-laskenta eli neliösummaetäisyys vs. odotusarvo\n"
            + " ---------------------------------\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku.\n\n"
            + " 'Ulottuvuus on 1, 2 tai 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " Vaihtonappi 'VAPAA'/'HILA' saa kulkijat liikkumaan joko vapaasti\n"
            + " tai hilamuodossa.\n\n"
            + " Vaihtonappi 'KORJAUS' lisää laskentakaavaan korjaustermin.\n\n"
            + " Nappi 'SUORITA' ajaa koodin ja nappi 'SULJE' sulkee sovelluksen.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Ohjelma tulostaa Rrms odotusarvon funktiona.\n\n"
            + " Kukin ajo tallentaa datan '.xy'-päätteellä ja kuvan 'pdf'-muodossa\n"
            + " korvaten edelliset tiedostot.\n";
    }
    /**
     * @return RMS CALCULATION HELP TEXT
     */
    String calculationEN() {

        return "\n Root Mean Squared Distance vs Expected Value\n"
            + " --------------------------------------------\n\n"
            + " Number of Steps is a positive integer.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " Toggle button 'FREE'/'LATTICE' makes walkers move either freely\n"
            + " or in lattice form.\n\n"
            + " Toggle button 'FIX' adds equation a correction term.\n\n"
            + " Button 'EXECUTE' runs the code, and button 'CLOSE' closes the\n"
            + " application.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Program plots Rrms as a function of expected value.\n\n"
            + " Every run will save the data ending with '.xy' and an image file in\n"
            + " 'pdf' format, replacing the previous ones.\n";
    }
    /**
    * @return REAL TIME RMS HELP TEXT
    */
    String realtimermsFI() {

        return "\n Reaaliaikainen neliösummaetäisyys\n"
            + " ---------------------------------\n\n"
            + " Kulkijoiden lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " kulkijoiden liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " Ulottuvuus on 1, 2 tai 3. Yksi on kulkua x-akselilla, kaksi on\n"
            + " kulkua xy-tasossa, kolme on kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Vaihtonappi 'VAPAA'/'HILA' saa kulkijat liikkumaan joko vapaasti\n"
            + " tai hilamuodossa.\n\n"
            + " Nappi 'NORM. JAKAUMA' asettaa keskimmäiseen kuvaajaan normaali-\n"
            + " jakauman.\n\n"
            + " Nappi 'DIFF. JAKAUMA' asettaa keskimmäiseen kuvaajaan diffuusio-\n"
            + " jakauman.\n\n"
            + " Nappi 'AJA' aloittaa animaation. Animaation ajan ohessa pyörii kolme\n"
            + " reaaliaikaista graafia erillisessä kuvaikkunassa: 'Rrms ja odotusarvo\n"
            + " ajojen funktiona', 'Rrms standardi normaalijakauma' tai 'Diffuusio\n"
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

        return "\n Real Time Root Mean Squared Distance\n"
            + " ------------------------------------\n\n"
            + " Number of walkers is a positive integer, at least 1.\n\n"
            + " Number of steps is a positive integer. It means the repeated random\n"
            + " steps the walkers take while moving.\n\n"
            + " Dimension is either 1, 2, or 3. One means moving along x-axis, two\n"
            + " means moving on a plane with axes x and y, three means moving in a\n"
            + " cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " Toggle button 'FREE'/'LATTICE' makes walkers move either freely\n"
            + " or in lattice form.\n\n"
            + " Button 'NORM. DISTRIB.' sets a normal distribution to the middle\n"
            + " plot.\n"
            + " Button 'DIFF. DISTRIB.' sets a diffusion distribution to the middle\n"
            + " plot.\n"
            + " Button 'RUN' starts an animation. During the animation there will be\n"
            + " an image with two real time plots running different graphics:\n"
            + " 'Rrms and Expected Value as functions of Walks', 'Rrms Standard\n"
            + " Normal Distribution' or 'Diffusion Distribution', and 'Distance\n"
            + " Histogram'.\n\n"
            + " User can save the image by right-clicking the mouse and choosing\n"
            + " 'save'.\n\n"
            + " Save formats: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " Button 'CLOSE' closes the application.\n";
    }
    /**
    * @return DIFFUSION HELP TEXT
    */
    String diffFI() {

        return "\n Diffuusio\n"
            + " -------------\n\n"
            + " Kulkijoiden lukumäärä on positiivinen kokonaisluku, vähintään 1.\n\n"
            + " Kulkijoiden halkaisija on positiivinen reaaliluku väliltä ]0.0, 1.0[.\n\n"
            + " Ulottuvuus on 2 tai 3. Kaksi on kulkua xy-tasossa, kolme on kulkua\n"
            + " kuutiossa akseleilla x, y, ja z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " VAIHTONAPIT:\n\n"
            + " - 'PALLOT'/'YMPYRÄT' päättää kulkijoiden kuvaesityksestä (3D-pallot\n"
            + "    vai 2D-tasoympyrät).\n\n"
            + " - 'VAPAA'/'HILA' päättää kulkevatko kulkijat vapaasti vai hila-\n"
            + "    muodossa.\n\n"
            + " - 'VISKOSITEETTI'/'LIIKKUVUUS' päättää näytetäänkö yhtenä reaaliai-\n"
			+ "    kaisena kuvaajana viskositeetti vai sähköinen liikkuvuus.\n\n"
            + " VALINTANAPIT:\n\n"
            + " - 'ANIMAATIO' (myöhemmin 'JATKA') aloittaa animaation piirtämällä\n"
            + "    ensin kulkijoiden alkuasetelman ja odottaen käyttäjää poistamaan\n"
            + "    rajan. Sitten kulkijat alkavat liikkua reaaliajassa. Animaation\n"
            + "    ajan ohessa pyörii kolme reaaliaikaista graafia: dynaaminen\n"
            + "    viskositeetti, diffuusiovakio ja energian minimointi.\n\n"
            + "    Käyttäjä voi tallentaa kuvan klikkaamalla sitä hiiren oikealla\n"
            + "    korvalla ja valitsemalla 'save'.\n"
			+ "    Ajon voi myös peruuttaa napista 'PERUUTA'\n"
            + "    Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " - 'KUVA' luo vain kuvan diffuusiokulusta kahdella graafilla:\n"
            + "    kulkijoiden alku- ja loppuasetelmat.\n\n"
            + " NAPPI 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return DIFFUSION HELP TEXT
     */
    String diffEN() {

        return "\n Diffusion\n"
            + " -------------\n\n"
            + " Number of walkers is a positive integer, at least 1.\n\n"
            + " Diameter is a positive real number on the interval ]0.0, 1.0[.\n\n"
            + " Dimension is either 2 or 3. Two means moving on a plane with axes x\n"
            + " and y, three means moving in a cube with axes x, y, and z.\n\n"
            + " ---------------------------------------------------------------------\n\n"
            + " TOGGLE BUTTONS:\n\n"
            + " - 'BALLS'/'CIRCLES' decides whether the walker images are 3D balls\n"
            + "    or 2D flat circles.\n\n"
            + " - 'FREE'/'LATTICE' decides whether the walkers move freely or in\n"
            + "    lattice form.\n\n"
            + " CHOICE BUTTONS:\n\n"
            + " - 'ANIMATION' (later 'CONTINUE') starts an animation with first\n"
            + "    drawing the initial positions of walkers and then waiting\n"
            + "    for the user to remove the barrier. Then the walkers will start\n"
            + "    moving in real time. During the animation there will be an image\n"
            + "    window with three real time plots: dynamic viscosity, diffusion\n"
            + "    coefficient, and energy minimizing. User can save the image by\n"
            + "    right-clicking the mouse and choosing 'save'.\n"
			+ "    User may cancel the run pressing button 'CANCEL'\n"
            + "    Save formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " - 'PLOT' only creates an image of diffusion walk with two subplots:\n"
            + "    a start and final positions of walkers.\n\n"
            + " Button 'CLOSE' closes the application.\n";
    }
    /**
     * @return REAL TIME SAW HELP TEXT
     */
    String realtimesawFI() {

        return "\n Reaaliaikainen itseään välttelevä kulku\n"
            + " ---------------------------------------\n\n"
            + " Askelten lukumäärä on positiivinen kokonaisluku. Se tarkoittaa\n"
            + " kulkijoiden liikkuessa ottamien satunnaisten askelten määrää.\n\n"
            + " Ulottuvuus on 2 tai 3. Kaksi on kulkua xy-tasossa, kolme on\n"
            + " kulkua kuutiossa akseleilla x, y, ja z.\n\n"
            + " Liukukytkimellä voi säätää amplitudia 'A'.\n\n"
            + " ----------------------------------------------------------------\n\n"
            + " VALINTANAPIT:\n\n"
            + " - 'AJA SAW' silmukoi yksinkertaista itseään välttelevää kulkua\n"
            + "    jonka aikana ohessa pyörii kolme reaaliaikaista graafia:\n"
            + "    kaksi 'Itseään välttelevää kulkua', joista ensimmäisessä on\n"
            + "    mukana liikkuva x-akseli ja toisessa origoon sidottu kasvava\n"
            + "    x-akseli, sekä alimpana 'Etäisyys-histogrammi.\n\n"
            + "    Käyttäjä voi tallentaa pysäytetyn kuvan klikkaamalla sitä\n"
            + "    hiiren oikealla korvalla ja valitsemalla 'save'.\n"
            + "    Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps',\n"
            + "    'pdf'.\n\n"
            + " - 'AJA MC' silmukoi biasoitua itseään välttelevää kulkua, joka\n"
            + "    hyödyntää Monte Carlo -metodia.\n\n"
            + "    Ajon aikana ohessa pyörii kolme reaaliaikaista graafia:\n"
            + "    kaksi 'Itseään välttelevää kulkua', joista ensimmäisessä on\n"
            + "    mukana liikkuva x-akseli ja toisessa origoon sidottu kasvava\n"
            + "    x-akseli, sekä alimpana 'Etäisyys-histogrammi.\n\n"
            + "    Käyttäjä voi tallentaa pysäytetyn kuvan klikkaamalla sitä\n"
            + "    hiiren oikealla korvalla ja valitsemalla 'save'.\n"
            + "    Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps',\n"
            + "    'pdf'.\n\n"
            + " - 'TEHOKKUUS (MC)' näyttää reaaliajassa kuvaajan tehokkuudesta\n"
			+ "    askelmäärän funktiona. Napin alapuolella olevaan tekstikent-\n"
			+ "    tään syötetään maksimi-askelmäärä. Kuvan voi tallentaa klik-\n"
			+ "    kaamalla sitä hiiren oikealla korvalla ja valitsemalla\n"
			+ "    'save'. Tallennusmuodot: 'png', 'jpg', 'bmp', 'gif', 'svg',\n"
			+ "    'eps', 'pdf'.\n\n"
            + " - 'KUVA' luo kuvan itseään välttelevän kulun liikeradasta.\n"
            + "    Kuvaajan luontiin käytettyjä ajoja voi rajoittaa napin alla\n"
            + "    olevassa laatikossa.\n\n"
            + " Nappi 'SULJE' sulkee sovelluksen.\n";
    }
    /**
     * @return REAL TIME SAW HELP TEXT
     */
    String realtimesawEN() {

        return "\n Real Time Self Avoiding Walk\n"
            + " ----------------------------\n\n"
            + " Steps is a positive integer. It means the repeated random\n"
            + " steps the walkers take while moving.\n\n"
            + " Dimension is either 2 or 3. Two means moving on a plane with axes\n"
            + " x and y, three means moving in a cube with axes x, y, and z.\n\n"
            + " Slider adjusts the amplitude 'A'.\n\n"
            + " -----------------------------------------------------------------\n\n"
            + " CHOICE BUTTONS:\n\n"
            + " - 'RUN SAW' loops a simple self-avoiding walk, during which there\n"
            + "    will be an image window with three real time plots:\n"
            + "    two of 'Self-avoiding walk', of which the first one has a co-\n"
            + "    moving x-axis and the second one has an increasing x-axis\n"
            + "    fixed to ordinate'. The last graph is a distance histogram'.\n\n"
            + "    User can save the image by right-clicking the mouse and\n"
            + "    choosing 'save'.\n"
            + "    Save formats are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps',\n"
            + "    'pdf'.\n\n"
            + " - 'RUN MC' loops biased self-avoiding walk,\n"
            + "    which uses Monte Carlo method. Otherwise the same as RUN SAW.\n\n"
            + " - 'EFFICIENCY (MC)' shows a real time graph of efficiency as\n"
			+ "    function of steps. User inputs a maximum steps value int the\n"
			+ "    text input field box below the button. User may save the image\n"
			+ "    by right-clicking the mouse and choosing 'save'. Save formats\n"
            + "    are: 'png', 'jpg', 'bmp', 'gif', 'svg', 'eps', 'pdf'.\n\n"
            + " - 'PLOT' creates an image of self-avoiding walk path. Amount of\n"
            + "    runs to create a plot can be restricted in the input box below\n"
            + "    the button.\n\n"
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
            + "       ////      |             2019-20              |    ////  ////  \n"
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
            + "       ////      |             2019-20              |    ////  ////  \n"
            + "       ////      |                                  |   ////  ////   \n"
            + "       ////       ----------------------------------   //// ////     \n"
            + "       ////          ////  ////  ////      ////       ///////        \n"
            + "       ////  ////   ////  ////   ////     ////       ////////        \n"
            + "       //// ////// ////  ////////////    ////       ////   ////      \n"
            + "       /////// ///////  ////     ////   ////       ////    ////      \n"
            + "       //////  //////  ////      ////  /////////  ////     /////       ";
    }
}
