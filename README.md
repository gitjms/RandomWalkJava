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

Valinnassa *MMC Diffusion* hiukkasten alkuasetelma on toisinaan hieman syrjässä siitä, missä niiden pitäisi olla. Kun liike alkaa, asemoituvat ne kuitenkin taas hyvin. En ole vielä löytänyt syytä alkuasetelman poikkeamiin.

## TODO

* MMC diffuusio -animaatioon 3D-hilarakenne.
* Skaalaukset paremmiksi.
* Ehkä muitakin hilarakenteita.

## Kaavio ohjelmarakenteesta

![Lopullinen_kaavio](https://user-images.githubusercontent.com/46410240/63222695-b61a6280-c1b3-11e9-9646-2faca39e54e2.png)

## Kuvia

<img src="https://user-images.githubusercontent.com/46410240/62991754-956fa700-be59-11e9-8d28-224e2acffe00.png" alt="main_view" width="480" height="341" >
<img src="https://user-images.githubusercontent.com/46410240/62991788-ba641a00-be59-11e9-92d6-d47989631952.png" alt="rms_calc" width="480" height="270" >
<img src="https://user-images.githubusercontent.com/46410240/62991794-c0f29180-be59-11e9-8424-9cd9abd8bc4c.png" alt="path_trac" width="480" height="270" >
<img src="https://user-images.githubusercontent.com/46410240/62991808-ca7bf980-be59-11e9-9253-d177dc285eac.png" alt="real_rms" width="480" height="270" >
<img src="https://user-images.githubusercontent.com/46410240/62991812-cea81700-be59-11e9-9726-770193265ce8.png" alt="mmc_diff" width="478" height="255" >
<img src="https://user-images.githubusercontent.com/46410240/63804122-1da48080-c91f-11e9-8b65-371fac5a9bba.png" alt="1D_dist" width="480" height="238" >
