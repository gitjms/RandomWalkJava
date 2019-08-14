# Random Walk Model of Diffusion - Random walk diffuusiomalli

********************************************************************************

Kandidaatintutkielma,
Helsingin yliopisto,
Jari Sunnari 2019

********************************************************************************

## Kuvaus

Java-ohjelma suorittaa erilaisia satunnaisliikeskenaarioita esittäen niiden perusteella eri tyyppisiä kavioita ja/tai animaatioita.
Ohjelma luo työkansion **C:/RWDATA**, johon tallentuvat niin *Fortran*- ja *Python*-koodit kuin data- ja kuvatiedostotkin.

### Ohjelman suoritusvaihtoehtoja:

 VALINTA | KUVAUS 
---------|---------
**RMS vs SQRT(N)** | laskee neliöllisen keskiarvoetäisyyden satunnaiskulkijoille, ja esittää sen kaaviossa suhteessa etäisyyden keskiarvoon, eli kulkijoiden lukumäärän neliöjuureen. Yksi ajo tuottaa yhden datatiedoston, jonka pääte on *.xy*. Kaaviokuva tallentuu työkansioon pdf-muodossa.
**PATH TRACING**   | piirtää satunnaisliikepolut kuvina *pdf*-muodossa. Yksi ajo tuottaa myös kaksi *.xy*-päätteistä datatiedostoa sekä lisäksi kaksi tai kolme tiedostoa päätteillä *.x*, *.y* ja *.z*, riippuen käyttäjän dimensiovalinnasta.
**REAL TIME RMS**  | esittää reaaliaikaisen animaation satunnaiskulkijoista, jotka luodaan yhdestä pisteestä (fixed source). Ohjelma silmukoi kulkijoita halutulla askelmäärällä, laskien samalla neliöllistä keskiarvoetäisyyttä, jonka normaalijakauma esitetään grafiikkana. Toinen grafiikka esittää sekä neliökeskiarvoetäisyyden että odotusarvoetäisyyden sqrt(N). Mitään tiedostoja ei tallennu automaattisesti, mutta käyttäjä voi halutesaan tallentaa kuvan eri muodoissa klikkaamalla kuvaa hiiren oikeallakorvalla. Tallennusvaihtoehtoja ovat *png*, *jpg*, *bmp*, *gif*, *svg*, *eps* ja *pdf*.
**MMC DIFFUSION**  | esittää vaihtoehtoisesti joko animaation satunnaiskulkijoiden diffuusioliikkeestä tai kuvan kulkijoiden aloitus- ja loppukonfiguraatioista. Animaatio tuottaa ajoa varten tarvittavan datatiedos-ton aloituskonfiguraatiosta *.xy*-päätteellä. Kuvia eitallennu. Kuvan suorittaminen sen sijaan tuottaa kaksi *.xy*-päätteistä datatiedostoa sekä lisäksi kaksi tai kolme tiedostoa päätteillä *.x*, *.y* ja *.z*, riippuen käyttäjän dimensiovalinnasta. Kuva tallentuu *pdf*-muodossa automaattisesti.

Nämä ohjeet ovat myös saatavilla itse ohjelmassa painamalla *HELP*-nappia päämenussa (ensimmäinen näkymä). Kullekin ohjelman suoritusvaihtoehdolle on lisäksi oma *HELP*-nappinsa ja oma ohjeensa. Ohjeet ovat vain englanniksi.

## Ongelmia

Ikkunoiden kuvakkeet/kulmaikonit eivät toimi.
Asennustyökalussa voi olla paljon turhiakin kirjastoja.

## TODO

MMC diffuusio -animaatioon 3D-hilarakenne.
Skaalaukset paremmiksi.
