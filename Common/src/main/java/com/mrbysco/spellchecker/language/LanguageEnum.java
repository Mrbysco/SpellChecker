package com.mrbysco.spellchecker.language;

public enum LanguageEnum {
	DA_DK("da_dk"),
	DE_CH("de_ch"),
	DE_DE("de_de"),
	EN_GB("en_gb"),
	EN_US("en_us"),
	ES_ES("es_es"),
	FR_FR("fr_fr"),
	IT_IT("it_it"),
	NL_NL("nl_nl"),
	NO_NO("no_no"),
	SV_SE("sv_se");

	private final String locale;

	LanguageEnum(String locale_name) {
		this.locale = locale_name;
	}

	public String getLocale() {
		return this.locale;
	}
}
