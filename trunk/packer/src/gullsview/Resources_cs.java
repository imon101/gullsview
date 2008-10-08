package gullsview;

import java.util.*;


public class Resources_cs extends ListResourceBundle {
	private static final Object[][] CONTENTS = new Object[][]{
		{"error-not-int", "Zadejte prosím celočíselnou hodnotu"},
		{"error-not-double", "Zadejte prosím číslo"},
		{"error-not-boolean", "Zadejte prosím ano nebo ne"},
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
		{"output-path", "Zadejte cestu k adresáři pro uložení výsledného midlet suite"},
		{"error-incorrect-path", "Chybná cesta k adresáři"},
		{"error-not-exist", "Zadaný adresář neexistuje"},
		{"error-not-directory", "Zadaná cesta neodpovídá adresáři"},
		{"start", "Začínám generovat midlet suite"},
		{"finish", "Hotovo"},
		{"accept-to-close", "Pro ukončení aplikace potvrďte vstup"},
		{"world", "Svět"},
		{"error-empty", "Prázdný vstup není povolen"},
		{"map-count", "Zadejte počet map"},
		{"map-name", "Identifikátor mapy (bez mezer, diakritiky a speciálních znaků)"},
		{"map-title", "Titulek mapy"},
		{"map-vendor", "Vaše jméno a příjmení"},
		{"map-scale", "Číselné označení měřítka mapy (větší číslo = větší detail)"},
		{"map-segment", "Rozměr mapové dlaždice (čtvercového obrázku) v pixelech"},
		{"map-xcount", "Počet mapových dlaždic ve vodorovném směru"},
		{"map-ycount", "Počet mapových dlaždic ve svislém směru"},
		{"error-not-coord", "Zadaný text nelze převést na souřadnici"},
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
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
	};
	
	public Object[][] getContents(){
		return CONTENTS;
	}
}


