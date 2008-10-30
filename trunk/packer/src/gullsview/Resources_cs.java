package gullsview;

import java.util.*;


public class Resources_cs extends ListResourceBundle {
	private static final Object[][] CONTENTS = new Object[][]{
		{"error-not-int", "Zadejte prosím celočíselnou hodnotu"},
		{"error-not-double", "Zadejte prosím číslo"},
		{"error-not-boolean", "Zadejte prosím ano nebo ne"},
		{"error-incorrect-path", "Chybná cesta k adresáři"},
		{"error-not-exist", "Zadaný adresář neexistuje"},
		{"error-not-directory", "Zadaná cesta neodpovídá adresáři"},
		{"error-empty", "Prázdný vstup není povolen"},
		{"error-not-coord", "Zadaný text nelze převést na souřadnici"},
		{"error-file-not-exist", "Zadaný soubor neexistuje"},
		{"error-not-file", "Zadaná cesta neodpovídá souboru"},
		{"yes", "ano"},
		{"no", "ne"},
		{"title", "Gull's View - Packer"},
		{"accept", "Potvrdit"},
		{"question", "Dotaz"},
		{"answer", "Odpověď"},
		{"enable-fc", "Zapnout podporu JSR 075 - File Connection? (na zařízeních bez tohoto rozšíření API však aplikace nebude fungovat)"},
		{"enable-bt", "Zapnout podporu JSR 082 - Bluetooth API? (samotná podpora Bluetooth v zařízení nestačí, na zařízeních bez tohoto rozšíření API však aplikace nebude fungovat)"},
		{"enable-lapi", "Zapnout podporu JSR 179 - Location API? (na zařízeních bez tohoto rozšíření API však aplikace nebude fungovat)"},
		{"enable-m3g", "Zapnout podporu JSR 184 - Mobile 3D Graphics? (na zařízeních bez tohoto rozšíření API však aplikace nebude fungovat)"},
		{"output-path", "Zadejte cestu k adresáři pro uložení výsledného midlet suite (mobilní aplikace - JAR+JAD)"},
		{"start", "Začínám generovat midlet suite"},
		{"finish", "Hotovo"},
		{"accept-to-close", "Pro ukončení aplikace potvrďte vstup"},
		{"world", "Svět"},
		{"map-count", "Zadejte počet map"},
		{"map-name", "Identifikátor mapy (bez mezer, diakritiky a speciálních znaků)"},
		{"map-title", "Titulek mapy"},
		{"map-vendor", "Vaše jméno a příjmení"},
		{"map-scale", "Číselné označení měřítka mapy (větší číslo = větší detail)"},
		{"map-segment", "Rozměr mapové dlaždice (čtvercového obrázku) v pixelech"},
		{"map-xcount", "Počet mapových dlaždic ve vodorovném směru"},
		{"map-ycount", "Počet mapových dlaždic ve svislém směru"},
		{"map-lt-lat", "Souřadnice levého horního rohu mapy - zemská šířka (latitude)"},
		{"map-lt-lon", "Souřadnice levého horního rohu mapy - zemská délka (longitude)"},
		{"map-rt-lat", "Souřadnice pravého horního rohu mapy - zemská šířka (latitude)"},
		{"map-rt-lon", "Souřadnice pravého horního rohu mapy - zemská délka (longitude)"},
		{"map-lb-lat", "Souřadnice levého dolního rohu mapy - zemská šířka (latitude)"},
		{"map-lb-lon", "Souřadnice levého dolního rohu mapy - zemská délka (longitude)"},
		{"map-lat", "Výchozí souřadnice mapy - zemská šířka (latitude)"},
		{"map-lon", "Výchozí souřadnice mapy - zemská délka (longitude)"},
		{"map-data-dir", "Zadejte adresář obsahující mapové dlaždice"},
		{"map-data-format", "Formát názvů souborů s mapovými dlaždicemi - za {0} se dosadí vodorovný index dlaždice, za {1} svislý. Indexuje se od nuly."},
		{"map-data-included", "Mají být mapové dlaždice (obrázky) vloženy do výsledného aplikačního JAR archivu?"},
		{"map-no-params", "Parametry mapy číslo"},
		{"processing-manifest", "Zpracovávám Manifest"},
		{"processing-entry", "Zpracovávám JAR položku"},
		{"writing-resource-entry", "Zapisuji JAR položku z vlastních zdrojů"},
		{"adding-segment-file-to-archive", "Přidávám do archivu dlaždici"},
		{"adding-segment-file-to-dir", "Vytvářím soubor pro uložení na paměťovou kartu zařízení"},
		{"writing-output-start", "Vytvářím midlet suite"},
		{"writing-output-finish", "Midlet suite vytvořen"},
		{"copy-data-along", "POZOR!!! Před nasazením aplikace nejprve do mobilního zařízení nakopírujte vytvořený adresář s mapovými daty!"},
		{"usage-0", "Použití:"},
		{"usage-1", "java -jar GullsViewPacker.jar [-stdio] [-properties FILE] [-swing]"},
		{"usage-2", "kde jednotlivé volby znamenají toto:"},
		{"usage-3", "-stdio - spustí aplikaci v textovém režimu (z textové konzole)"},
		{"usage-4", "-properties FILE - spustí aplikaci v dávkovém režimu - odpovědi na všechny dotazy čerpá ze souboru FILE ve formátu Java Properties"},
		{"usage-5", "-swing - spustí aplikaci v grafickém režimu (výchozí volba)"},
		{"mercator", "Jsou vstupní data vytvořena v projekci jménem Mercator? (Mercator používá např. projekt www.openstreetmap.org nebo maps.google.com)"},
		{"segoffsetx", "Zadejte offset levé horní dlaždice na X-ové ose (tj. globální x-ový index té dlaždice mapy, která je interně uložena v souboru jménem 0_0)"},
		{"segoffsety", "Zadejte offset levé horní dlaždice na Y-ové ose (tj. globální y-ový index té dlaždice mapy, která je interně uložena v souboru jménem 0_0)"},
		{"", ""},
	};
	
	public Object[][] getContents(){
		return CONTENTS;
	}
}


