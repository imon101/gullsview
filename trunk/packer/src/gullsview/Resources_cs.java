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
		{"enable-fc", "Zapnout podporu JSR 075 - File Connection? (na zařízeních bez tohoto rozšíření API aplikace nebude fungovat)"},
		{"enable-bt", "Zapnout podporu JSR 082 - Bluetooth API? (samotná podpora Bluetooth v zařízení nestačí, na zařízeních bez tohoto rozšíření API aplikace nebude fungovat)"},
		{"enable-lapi", "Zapnout podporu JSR 179 - Location API? (na zařízeních bez tohoto rozšíření API aplikace nebude fungovat)"},
		{"enable-m3g", "Zapnout podporu JSR 184 - Mobile 3D Graphics? (na zařízeních bez tohoto rozšíření API aplikace nebude fungovat)"},
		{"output-path", "Zadejte cestu k adresáři pro uložení výsledného midlet suite"},
		{"error-incorrect-path", "Chybná cesta k adresáři"},
		{"error-not-exist", "Zadaný adresář neexistuje"},
		{"error-not-directory", "Zadaná cesta neodpovídá adresáři"},
		{"start", "Začínám generovat midlet suite"},
		{"finish", "Hotovo"},
		{"please-close-window", "Pro ukončení aplikace zavřete toto okno"},
		{"world", "Svět"},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
		{"", ""},
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


