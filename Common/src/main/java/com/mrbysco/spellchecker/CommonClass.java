package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.platform.Services;
import com.mrbysco.spellchecker.util.DictionaryUtil;
import com.swabunga.spell.engine.SpellDictionary;

import java.io.File;

public class CommonClass {

	private static SpellDictionary dict;

	public static void init() {
		Constants.LOGGER.info("Detecting / Creating Personal Dictionary Folder");
		DictionaryUtil.personalFolder = new File(Services.PLATFORM.getConfigDir().toFile(), "/personaldictionary");
		if (!DictionaryUtil.personalFolder.exists()) {
			DictionaryUtil.personalFolder.mkdirs();
		}
		DictionaryUtil.personalDictionary = new File(DictionaryUtil.personalFolder, "/dictionary.txt");
	}

	public static SpellDictionary getDict() {
		if (dict != null) {
			return dict;
		}
		DictionaryUtil.buildLanguageMap(Services.PLATFORM.getConfiguredLocale());
		return dict;
	}

	public static void setDict(SpellDictionary dictionary) {
		dict = dictionary;
	}
}