# GfxLab


## Nameštanje

- Uključite JAR fajlove iz lib foldera u projekat, ako se to ne desi automatski.
- Potrebno je da vaš projekat koristi JavaFX biblioteke za vaš OS.
  - Najlakši način da to namestite je da koristite BellSoft Liberica JDK (full), koji, za razliku od većine drugih JDK-ova, dolazi sa ugrađenim JavaFX modulima.
    - Ako koristite IntelliJ, ovo je lako namestiti: File > Project Structure... > Project > SDK dropdown > Download JDK... > Version: 25, Vendor: BellSoft Liberica JDK (Full).
    - Alternativno, sami preuzmite JDK sa [https://bell-sw.com/pages/downloads/](https://bell-sw.com/pages/downloads/). Izaberite vaš OS, poslednju verziju, i Full JDK (jedino Full JDK uključuje JavaFX). Kada instalirate/raspakujete JDK, namestite u IDE-u da projekat koristi baš taj JDK.
  - Ako nećete da koristite BellSoft Liberica JDK, snađite se da preuzmete odgovarajuće biblioteke na neki način (direktni download svih potrebnih jar-fajlova, Maven, ...). Potrebni su vam javafx-base, javafx-controls, javafx-graphics, i javafx-swing.
  - U nekim slučajevima JavaFX neće koristiti GPU za iscrtavanje interfejsa i sve će biti pomalo laggy (meni se to dešava uz Linux i integrisani GPU). U tom slučaju (a ni inače verovatno ne može da škodi), dodajte system property `prism.forceGPU = true`, npr. kroz VM argument `-Dprism.forceGPU=true`. (Ovo bi sad trebalo da se automatski podešava pri pokretanju koda, ali ipak ostavljam ovu informaciju ovde.)
- Ako u korisničkom interfejsu vidite "arg0", "arg1", itd. kao imena parametara, kompajlirajte projekat sa `-parameters` kompajlerskim argumentom.
  - U IntelliJ, to uradite tako što u Settings (Ctrl+Alt+S) odaberete Build, Execution, Deployment > Compiler > Java Compiler, pa u Additional command line parameters polju upišete `-parameters`. Nakon toga treba da uradite rebuild projekta.
  

## Šta-gde

- Pokrećete klasu `gui.App`.
- Strukturu renderovanja definišete u klasi `playground.GfxLab`.
- Sve što budemo razvijali u toku kursa biće u paketu `graphics3d`.

