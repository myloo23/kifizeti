# KiFizeti? - Android beadandó

## Az alkalmazás célja
A *KiFizeti?* egy Android alkalmazás, amely baráti társaságok közös költéseinek kezelésére szolgál. A readme.md állománya tartalmazza az alkalmazás célját és a felhasználó által elérhető funkciók leírását. Segítségével nyomon követhető, hogy egy esemény (például buli, közös utazás) során ki mit fizetett, és a végén kinek mennyit kell adnia a többieknek az egyenlő teherviselés érdekében.

## Megvalósított funkciók és Feladatmegosztás

A projektet csoportos munkában valósítottuk meg, GitHub verziókezelő használatával. Minden csapattag egyaránt részt vett a felületek (Frontend) és az adatbázis/üzleti logika (Backend) fejlesztésében, folyamatos egyéni commitokkal.

**1. Események alapkezelése (Takács Milán - KWKABN)**
* **Funkció:** Új csoportos események (pl. "Siófok 2024") létrehozása és listából való törlése.
* **Frontend:** Új esemény hozzáadása űrlap és a törlést megerősítő párbeszédablakok elkészítése `ConstraintLayout` használatával.
* **Backend:** `Event` Room entitás létrehozása, alapvető DAO műveletek (`INSERT`, `DELETE`) implementálása.

**2. Főképernyő, Navigáció, Keresés és Rendezés (Tűri Krisztián Jenő - NLI24V)**
* **Funkció:** Az alkalmazás alapvázának összeállítása, valamint az események közötti keresési és rendezési funkció.
* **Frontend:** `Single Activity` beállítása, `Bottom Navigation Bar` beépítése a navigációhoz. Főképernyő felépítése: `RecyclerView` implementálása a listás nézethez, keresősáv és rendezési legördülő menü kialakítása.
* **Backend:** Room adatbázis inicializálása, a lista betöltéséhez, a szöveges kereséshez és az ABC/Dátum szerinti rendezéshez szükséges `SELECT` és `ORDER BY` DAO lekérdezések.

**3. Részletes nézet, Kiadások rögzítése és Módosítása (Mohácsi Richárd Norbert - ZKGA6I)**
* **Funkció:** Részletes nézet egy kiválasztott eseményhez, ahol új kiadásokat lehet felvinni (összeg, fizető fél, résztvevők), illetve a meglévő objektumok módosítása (kiadás szerkesztése).
* **Frontend:** Esemény részletes nézete (Fragment), és a Kiadás felvitele/szerkesztése képernyő dinamikus elemekkel. Felületek létrehozása `ConstraintLayout`-tal.
* **Backend:** `Expense` Room entitás és a relációk kezelése. Kiadások mentése és szerkesztése az adatbázisban (`INSERT`, `UPDATE` DAO műveletek).

**4. Elszámolás és Egyenlegek (Székely Attila - I8RXQL)**
* **Funkció:** Az app kiszámolja a tartozásokat és az egyenlegeket az adott eseményen belül (ki van pluszban/mínuszban).
* **Frontend:** Elszámolás képernyő (Fragment), amely egy `RecyclerView` segítségével listázza, hogy kinek kivel felé van tartozása. Felületek kialakítása `ConstraintLayout` segítségével.
* **Backend:** Az összes kiadás lekérdezése, az összegek szétosztása a résztvevők között, és a végső egyenlegek (tartozások) kiszámítása Java szinten.

## Technikai részletek

A projekt az oktatói követelményeknek megfelelően készült el:
* **Nyelv és Környezet:** Java programozási nyelv, Android Studio.
* **Architektúra:** Egyetlen Activity és több Fragment megközelítés, a felületek közötti váltást a Bottom Navigation biztosítja.
* **UI Elemek:** Minden nézet `ConstraintLayout` segítségével készült. A listás megjelenítésekhez mindenhol `RecyclerView`-t használtunk. Tartalmaz listás és részletes nézeteket is.
* **Adattárolás:** Készüléken lévő lokális adatbázis, az Architecture Components Room használatával.
* **Verziókezelés:** Git (GitHub/Gitlab), folyamatos egyéni commitokkal, Reporter szerepkörben az oktató meghívásával. Csak a szerveren megtalálható commit-ok számítanak. Egy felbontásra és egy készülékre elegendő az alkalmazást optimalizálni.

## Szerzők
* Takács Milán (KWKABN)
* Tűri Krisztián Jenő (NLI24V)
* Mohácsi Richárd Norbert (ZKGA6I)
* Székely Attila (I8RXQL)
