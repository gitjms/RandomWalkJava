# Random Walk Model of Diffusion - Random walk diffuusiomalli

********************************************************************************

Kandidaatintutkielma,
Helsingin yliopisto,
Jari Sunnari 2019

********************************************************************************

## Kuvaus

Java-ohjelma suorittaa erilaisia satunnaisliikeskenaarioita esittäen niiden pe-
rusteella eri tyyppisiä kavioita ja/tai animaatioita.
Ohjelma luo työkansion **C:/RWDATA**, johon tallentuvat niin *Fortran*- ja
*Python*-koodit kuin data- ja kuvatiedostotkin.

### Ohjelman suoritusvaihtoehtoja:

 VALINTA | KUVAUS 
---------|---------
**RMS vs SQRT(N)** | laskee neliöllisen keskiarvoetäisyyden satunnaiskulkijoille,
                   | ja esittää sen kaaviossa suhteessa etäisyyden keskiarvoon, eli kulkijoiden lukumäärän neliöjuureen. Yksi ajo tuottaa yhden datatiedoston, jonka pääte on *.xy*. Kaaviokuva tallentuu työkansioon pdf-muodossa.
**PATH TRACING**   | piirtää satunnaisliikepolut kuvina *pdf*-muodossa. Yksi ajo
                   | tuottaa myös kaksi *.xy*-päätteistä datatiedostoa sekä li-
                   | säksi kaksi tai kolme tiedostoa päätteillä *.x*, *.y* ja
                   | *.z*, riippuen käyttäjän dimensiovalinnasta.
**REAL TIME RMS**  | esittää reaaliaikaisen animaation satunnaiskulkijoista,
                   | jotka luodaan yhdestä pisteestä (fixed source). Ohjelma
                   | silmukoi kulkijoita halutulla askelmäärällä, laskien samal-
                   | la neliöllistä keskiarvoetäisyyttä, jonka normaalijakauma
                   | esitetään grafiikkana. Toinen grafiikka esittää sekä neliö-
                   | keskiarvoetäisyyden että odotusarvoetäisyyden sqrt(N).
                   | Mitään tiedostoja ei tallennu automaattisesti, mutta käyt-
                   | täjä voi halutesaan tallentaa kuvan eri muodoissa klikkaa-
                   | malla kuvaa hiiren oikeallakorvalla. Tallennusvaihtoehtoja
                   | ovat *png*, *jpg*, *bmp*, *gif*, *svg*, *eps* ja *pdf*.
**MMC DIFFUSION**  | esittää vaihtoehtoisesti joko animaation satunnaiskulkijoi-
                   | den diffuusioliikkeestä tai kuvan kulkijoiden aloitus- ja
                   | loppukonfiguraatioista. Animaatio tuottaa ajoa varten tar-
                   | vittavan datatiedos-ton aloituskonfiguraatiosta *.xy*-päät-
                   | teellä. Kuvia eitallennu. Kuvan suorittaminen sen sijaan
                   | tuottaa kaksi *.xy*-päätteistä datatiedostoa sekä lisäksi
                   | kaksi tai kolme tiedostoa päätteillä *.x*, *.y* ja *.z*,
                   | riippuen käyttäjän dimensiovalinnasta. Kuva tallentuu
                   | *pdf*-muodossa automaattisesti.

Nämä ohjeet ovat myös saatavilla itse ohjelmassa painamalla *HELP*-nappia pääme-
nussa (ensimmäinen näkymä). Kullekin ohjelman suoritusvaihtoehdolle on lisäksi
oma *HELP*-nappinsa ja oma ohjeensa. Ohjeet ovat vain englanniksi.

## Ongelmia

Ikkunoiden kuvakkeet/kulmaikonit eivät toimi.
Asennustyökalussa voi olla paljon turhiakin kirjastoja.

## Asennusohjeet ladattaville asennustyökaluille

Asennustyökalut löytyvät linkin *release* takaa:

![release_link](https://user-images.githubusercontent.com/46410240/62998769-33249f80-be75-11e9-9512-b0081174ec47.png)

Exe-päätteinen asennustyökalu tuottaa ohjatun asennuksen, jonka avulla voi itse
päättää esim. mihin ohjelma asennetaan.

Msi-loppuinen asennusväline puolestaan asentaa ohjelman mitään kysymättä suoraan
C-aseman Program Files -kansioon samalla nimellä, kuin itse työkalukin.

## TODO

* MMC diffuusio -animaatioon 3D-hilarakenne.
* Skaalaukset paremmiksi.
* Ehkä muitakin hilarakenteita.

# Kuvia

![main_view](https://user-images.githubusercontent.com/46410240/62991754-956fa700-be59-11e9-8d28-224e2acffe00.png)

![rms_calc](https://user-images.githubusercontent.com/46410240/62991788-ba641a00-be59-11e9-92d6-d47989631952.png)

![path_trac](https://user-images.githubusercontent.com/46410240/62991794-c0f29180-be59-11e9-8424-9cd9abd8bc4c.png)

![real_rms](https://user-images.githubusercontent.com/46410240/62991808-ca7bf980-be59-11e9-9253-d177dc285eac.png)

![mmc_diff](https://user-images.githubusercontent.com/46410240/62991812-cea81700-be59-11e9-9726-770193265ce8.png)

