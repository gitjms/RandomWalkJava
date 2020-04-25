# Satunnaiskulun ominaisuuksia ja visualisointeja

********************************************************************************

Kandidaatintutkielma,
Helsingin yliopisto,
Jari Sunnari 2019-20

********************************************************************************

## Kuvaus

Java-ohjelma suorittaa erilaisia satunnaisliikeskenaarioita esittäen niiden perusteella eri tyyppisiä kavioita ja/tai animaatioita.
Ensimmäisen kerran käynnistettäessä ohjelma luo työkansion **C:/RWDATA**, johon *Fortran*- ja *Python*-koodit tallentuvat. Tämän jälkeen data- ja kuvatiedostot tallentuvat samaan paikkaan.

### Ohjelman suoritusvaihtoehtoja:

 VALINTA | KUVAUS 
---------|---------
 **LIIKERADAT**     | piirtää satunnaisliikeradat kuvina *pdf*-muodossa. Yksi ajo tuottaa myös yksi kolme *.xy*-päätteistä datatiedostoa, riippuen käyttäjän dimensiovalinnasta.
 **1D-ETÄISYYS**    | esittää satunnaiskulkijoiden etäisyydet askelten suhteen. Yksi ajo tuottaa yhden datatiedoston päätteellä *.xy*, sekä kaaviokuvan pdf-muodossa.
 **RMS vs SQRT(N)** | laskee neliöllisen keskiarvoetäisyyden satunnaiskulkijoille, ja esittää sen kaaviona suhteessa kulkijoiden askelten lukumäärän neliöjuureen. Yksi ajo tuottaa yhden datatiedoston, jonka pääte on *.xy*. Kaaviokuva tallentuu työkansioon pdf-muodossa.
 **REAALIAIKA-RMS** | esittää reaaliaikaisen animaation satunnaiskulkijoista, jotka luodaan yhdestä pisteestä (fixed source). Ohjelma silmukoi kulkijoita halutulla askelmäärällä, laskien samalla neliöllistä keskiarvoetäisyyttä, jonka normaalijakauma esitetään grafiikkana. Toinen grafiikka esittää sekä neliökeskiarvoetäisyyden että odotusarvoetäisyyden sqrt(S). Mitään tiedostoja ei tallennu automaattisesti, mutta käyttäjä voi halutesaan tallentaa kuvan eri muodoissa klikkaamalla kuvaa hiiren oikealla korvalla. Tallennusvaihtoehtoja ovat *png*, *jpg*, *bmp*, *gif*, *svg*, *eps* ja *pdf*.
 **DIFFUUSIO**      | esittää vaihtoehtoisesti joko animaation satunnaiskulkijoiden diffuusioliikkeestä tai kuvan kulkijoiden aloitus- ja loppukonfiguraatioista. Animaatio tuottaa ajoa varten tarvittavan datatiedoston aloituskonfiguraatiosta *.xy*-päätteellä. Kuvia ei tallennu. Kuvan suorittaminen sen sijaan tuottaa kaksi *.xy*-päätteistä datatiedostoa. Kuva tallentuu *pdf*-muodossa automaattisesti.
 **REAALIAIKA-SAW** | esittää itseään välttelevää satunnaiskulkua vaihtoehtoisesti joko reaaliaikaisilla kuvaajilla neliöllisestä keskiarvoetäisyydestä yms. tai yhden liikeratakuvan. Myös reaaliaikainen tehokkuuskuvaaja on valittavissa. Reaaliaikaiset ajot eivät tuota tiedostoja. Liikeratakuva tuottaa yhden liikeratadatatiedoston *.xy*-päätteellä sekä kuvan pdf-muodossa.

Nämä ohjeet ovat myös saatavilla itse ohjelmassa painamalla *HELP*-nappia päämenussa (ensimmäinen näkymä). Osalla ohjelman suoritusvaihtoehdoista on lisäksi oma *HELP*-nappinsa ja oma ohjeensa.

Ohjelmaa käynnistettäessä ilmestyy aluksi pieni kielivalintaikkuna, jonka vaihtoehtoina ovat suomi ja englanti. Valinta vaikuttaa ohjeisiin sekä kuvissa oleviin teksteihin.
 
## Asennustyökalu

Vanha Java 1.8 -version asennustyökalu *RandomWalk-1.0.msi* löytyy linkin *release* takaa:

<img src="https://user-images.githubusercontent.com/46410240/63167309-952e0200-c039-11e9-9d65-571ce3170085.png" alt="release" width="612" height="180" >

Uudempi versio on päivitetty versioon Java 11. Sille ei ole olemassa erillistä asennustyökalua, vaan kyse on ns. "fat jar" -tiedostosta, jonka voi ajaa komentoikkunassa komennolla

```
java -jar randomwalk.jar
```

tai Windowsissa tuplaklikkaamalla tiedostoa. Linux-versiota ei vieläkään ole. Tavoitteena on saada se aikaan joskus.

## TODO (mahdollisia tulevia kehitysnäkymiä)

* Diffuusio -animaatioon 3D-hilarakenne näkyviin
* Skaalaukset paremmiksi
* muitakin hilarakenteita
* lisää sisältöä SAW-osioon

## Kaavio ohjelmarakenteesta
<img src="https://user-images.githubusercontent.com/46410240/72285782-85e4cc00-364c-11ea-82d9-e71c52f0a45f.png" alt="Java_kaavio" width="450" height="882" >

## Kuvia

<img src="https://user-images.githubusercontent.com/46410240/72285467-d27bd780-364b-11ea-9476-918a3a30f3c4.png" alt="main_view" width="313" height="222" >
<img src="https://user-images.githubusercontent.com/46410240/72285507-ede6e280-364b-11ea-853d-164fb869125b.png" alt="path_view" width="580" height="264" >
<img src="https://user-images.githubusercontent.com/46410240/72285521-f93a0e00-364b-11ea-887a-5425a0862139.png" alt="1ddist_view" width="616" height="238" >
<img src="https://user-images.githubusercontent.com/46410240/72285538-00f9b280-364c-11ea-8009-091e465768f7.png" alt="calc_view" width="584" height="256" >
<img src="https://user-images.githubusercontent.com/46410240/72285554-06ef9380-364c-11ea-8c27-1e26c1c4892d.png" alt="real_view" width="546" height="335" >
<img src="https://user-images.githubusercontent.com/46410240/72285567-0e16a180-364c-11ea-8302-b68ed14034e1.png" alt="diff_view1" width="636" height="336" >
<img src="https://user-images.githubusercontent.com/46410240/72285585-14a51900-364c-11ea-9407-ef3d4ce6687b.png" alt="diff_view2" width="544" height="336" >
<img src="https://user-images.githubusercontent.com/46410240/72285604-21297180-364c-11ea-82df-d643872026f9.png" alt="saw_view1" width="540" height="334" >
<img src="https://user-images.githubusercontent.com/46410240/72285612-2686bc00-364c-11ea-886d-c8ce5d3b845d.png" alt="saw_view2" width="575" height="236" >
<img src="https://user-images.githubusercontent.com/46410240/72285619-2be40680-364c-11ea-8e9d-ac06a33fcc05.png" alt="saw_view3" width="637" height="344" >
