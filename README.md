# Random Walk Model of Diffusion - Random walk diffuusiomalli

********************************************************************************

Kandidaatintutkielma,
Helsingin yliopisto,
Jari Sunnari 2019

********************************************************************************

## Kuvaus

Java-ohjelma suorittaa erilaisia satunnaisliikeskenaarioita esittäen niiden perusteella eri tyyppisiä kavioita ja/tai animaatioita.
Ensimmäisen kerran käynnistettäessä ohjelma luo työkansion **C:/RWDATA**, johon *Fortran*- ja *Python*-koodit tallentuvat. Tämän jälkeen data- ja kuvatiedostot tallentuvat samaan paikkaan.

### Ohjelman suoritusvaihtoehtoja:

 VALINTA | KUVAUS 
---------|---------
 **PATH TRACING**   | piirtää satunnaisliikeradat kuvina *pdf*-muodossa. Yksi ajo tuottaa myös neljä tai viisi *.xy*-päätteistä datatiedostoa, riippuen käyttäjän dimensiovalinnasta.
 **1D DISTANCE**    | esittää satunnaiskulkijoiden etäisyydet askelten suhteen. Yksi ajo tuottaa kolme datatiedostoa päätteellä on *.xy*, joista kaksi on tarpeetonta, sekä kaaviokuvan pdf-muodossa.
 **RMS vs SQRT(N)** | laskee neliöllisen keskiarvoetäisyyden satunnaiskulkijoille, ja esittää sen kaaviossa suhteessa etäisyyden keskiarvoon, eli kulkijoiden lukumäärän neliöjuureen. Yksi ajo tuottaa yhden datatiedoston, jonka pääte on *.xy*. Kaaviokuva tallentuu työkansioon pdf-muodossa.
 **REAL TIME RMS**  | esittää reaaliaikaisen animaation satunnaiskulkijoista, jotka luodaan yhdestä pisteestä (fixed source). Ohjelma silmukoi kulkijoita halutulla askelmäärällä, laskien samalla neliöllistä keskiarvoetäisyyttä, jonka normaalijakauma esitetään grafiikkana. Toinen grafiikka esittää sekä neliökeskiarvoetäisyyden että odotusarvoetäisyyden sqrt(S). Mitään tiedostoja ei tallennu automaattisesti, mutta käyttäjä voi halutesaan tallentaa kuvan eri muodoissa klikkaamalla kuvaa hiiren oikealla korvalla. Tallennusvaihtoehtoja ovat *png*, *jpg*, *bmp*, *gif*, *svg*, *eps* ja *pdf*.
 **MMC DIFFUSION**  | esittää vaihtoehtoisesti joko animaation satunnaiskulkijoiden diffuusioliikkeestä tai kuvan kulkijoiden aloitus- ja loppukonfiguraatioista. Animaatio tuottaa ajoa varten tarvittavan datatiedoston aloituskonfiguraatiosta *.xy*-päätteellä. Kuvia ei tallennu. Kuvan suorittaminen sen sijaan tuottaa neljä tai viisi *.xy*-päätteistä datatiedostoa riippuen käyttäjän dimensiovalinnasta. Kuva tallentuu *pdf*-muodossa automaattisesti.
 **REAL TIME SAW**  | esittää itseään välttelevää satunnaiskulkua vaihtoehtoisesti joko reaaliaikaisilla kuvaajilla neliöllisestä keskiarvoetäisyydestä yms. tai yhden liikeratakuvan. Edellinen ei tuota tiedostoja. Jälkimmäinen tuottaa yhden liikeratadatatiedoston *.xy*-päätteellä sekä kuvan pdf-muodossa.

Nämä ohjeet ovat myös saatavilla itse ohjelmassa painamalla *HELP*-nappia päämenussa (ensimmäinen näkymä). Kullekin ohjelman suoritusvaihtoehdolle on lisäksi oma *HELP*-nappinsa ja oma ohjeensa.

Ohjelmaa käynnistettäessä ilmestyy aluksi pieni kielivalintaikkuna, jonka vaihtoehtoina ovat suomi ja englanti. Valinta vaikuttaa ohjeisiin sekä kuvissa oleviin teksteihin.
 
## Asennustyökalu

Asennustyökalu *RandomWalk-1.0.msi* löytyy linkin *release* takaa:

<img src="https://user-images.githubusercontent.com/46410240/63167309-952e0200-c039-11e9-9d65-571ce3170085.png" alt="release" width="612" height="180" >

## TODO (mahdollisia tulevia kehitysnäkymiä)

* MMC diffuusio -animaatioon 3D-hilarakenne näkyviin
* Skaalaukset paremmiksi
* muitakin hilarakenteita

## Kaavio ohjelmarakenteesta

![Java_kaavio](https://user-images.githubusercontent.com/46410240/67178661-350e6000-f3dc-11e9-9987-7e752aeac723.png)

## Kuvia

<img src="https://user-images.githubusercontent.com/46410240/64206211-843b1880-cea2-11e9-9892-bd7dd466b7f1.png" alt="main_view" width="450" height="318" >
<img src="https://user-images.githubusercontent.com/46410240/63885078-388af980-c9e0-11e9-8d04-9126a3b30383.png" alt="path_view" width="450" height="227" >
<img src="https://user-images.githubusercontent.com/46410240/63885091-42acf800-c9e0-11e9-8388-0a421dc705b5.png" alt="1ddist_view" width="450" height="200" >
<img src="https://user-images.githubusercontent.com/46410240/64206501-2ce97800-cea3-11e9-9149-19ce7ab11b10.png" alt="calc_view" width="450" height="194" >
<img src="https://user-images.githubusercontent.com/46410240/63885082-3c1e8080-c9e0-11e9-8193-86061f718e98.png" alt="real_view" width="450" height="270" >
<img src="https://user-images.githubusercontent.com/46410240/63885086-3fb20780-c9e0-11e9-984d-a2a815602d41.png" alt="mmc_view" width="450" height="265" >
