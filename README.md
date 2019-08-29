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
 **MMC DIFFUSION**  | esittää vaihtoehtoisesti joko animaation satunnaiskulkijoiden diffuusioliikkeestä tai kuvan kulkijoiden aloitus- ja loppukonfiguraatioista. Animaatio tuottaa ajoa varten tarvittavan datatiedoston aloituskonfiguraatiosta *.xy*-päätteellä. Kuvia eitallennu. Kuvan suorittaminen sen sijaan tuottaa kaksi *.xy*-päätteistä datatiedostoa sekä lisäksi kaksi tai kolme tiedostoa päätteillä *.x*, *.y* ja *.z*, riippuen käyttäjän dimensiovalinnasta. Kuva tallentuu *pdf*-muodossa automaattisesti.
 **1D DISTANCE**    | esittää satunnaiskulkijoiden etäisyydet askelten suhteen. Yksi ajo tuottaa kaksi (tarpeetonta) datatiedostoa päätteellä on *.xy*, yhden tiedoston päätteellä *.x*, sekä kaaviokuvan pdf-muodossa.

Nämä ohjeet ovat myös saatavilla itse ohjelmassa painamalla *HELP*-nappia päämenussa (ensimmäinen näkymä). Kullekin ohjelman suoritusvaihtoehdolle on lisäksi oma *HELP*-nappinsa ja oma ohjeensa. Ohjeet ovat vain englanniksi.

## Asennustyökalu

Asennustyökalu *RandomWalk-1.0.msi* löytyy linkin *release* takaa:

<img src="https://user-images.githubusercontent.com/46410240/63167309-952e0200-c039-11e9-9d65-571ce3170085.png" alt="release" width="612" height="180" >

## Ongelmia

- Valinnassa *MMC Diffusion* hiukkasten alkuasetelma on toisinaan hieman syrjässä siitä, missä niiden pitäisi olla. Kun liike alkaa, asemoituvat ne kuitenkin taas hyvin. En ole vielä löytänyt syytä alkuasetelman poikkeamiin.
- Toisinaan MMC Diffusionissa ainakin lattice-valinnalla koko hilakenttä näkyy vain pienenä ruutuna mustan kuvataustan vasemmassa yläkulmassa, eli ei ole skaalautunut oikein.
Yleensä tapahtuu, kun on ensin käyttänyt jotain muuta osiota kuin MMC Diffusionia. Bugin syy ei selvillä.

## TODO

* MMC diffuusio -animaatioon 3D-hilarakenne.
* Skaalaukset paremmiksi.
* Ehkä muitakin hilarakenteita.

## Kaavio ohjelmarakenteesta

![tree](https://user-images.githubusercontent.com/46410240/63843911-db675780-c98f-11e9-8b2f-8a39ec67fe21.png)

## Kuvia

<img src="https://user-images.githubusercontent.com/46410240/63885060-3163eb80-c9e0-11e9-8cde-8af7e0b3a310.png" alt="main_view" width="450" height="321" >
<img src="https://user-images.githubusercontent.com/46410240/63885067-35900900-c9e0-11e9-8cb4-150413e834e5.png" alt="main_view" width="450" height="232" >
<img src="https://user-images.githubusercontent.com/46410240/63885078-388af980-c9e0-11e9-8d04-9126a3b30383.png" alt="main_view" width="450" height="227" >
<img src="https://user-images.githubusercontent.com/46410240/63885082-3c1e8080-c9e0-11e9-8193-86061f718e98.png" alt="main_view" width="450" height="270" >
<img src="https://user-images.githubusercontent.com/46410240/63885086-3fb20780-c9e0-11e9-984d-a2a815602d41.png" alt="main_view" width="450" height="265" >
<img src="https://user-images.githubusercontent.com/46410240/63885091-42acf800-c9e0-11e9-8388-0a421dc705b5.png" alt="main_view" width="450" height="200" >
