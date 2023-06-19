package com.mrbysco.spellchecker.util;

import com.mrbysco.spellchecker.CommonClass;
import com.mrbysco.spellchecker.Constants;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;

public class DictionaryUtil {

	public static File personalFolder;
	public static File personalDictionary;

	public static void AddToPersonal(String word) {
		try {
			if (personalDictionary.exists()) {
				FileWriter fileWriter = new FileWriter(personalDictionary, true);
				if (!containsWordAlready(word.toLowerCase())) {
					fileWriter.write(word.toLowerCase());
					fileWriter.write("\n");
				}
				fileWriter.close();

			} else {
				FileWriter fileWriter = new FileWriter(personalDictionary);
				fileWriter.write(word.toLowerCase());
				fileWriter.write("\n");
				fileWriter.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean containsWordAlready(String word) throws FileNotFoundException {
		boolean found = false;
		try {
			Scanner scanner = new Scanner(personalDictionary);

			//now read the file line by line...
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().toLowerCase();
				if (line.equals(word)) {
					scanner.close();
					return true;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			//handle this
		}
		return found;
	}

	public static InputStream getDictionaryWords(String locale_name) throws IOException {
		if (!locale_name.isEmpty()) {
			try {
				return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(Constants.MOD_ID, "dictionaries/" + locale_name + "/dictionary.txt")).get().open();
			} catch (IOException e) {
				Constants.LOGGER.error("Invalid locale {}", locale_name);
				e.printStackTrace();
				return null;
			}
		} else {
			return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(Constants.MOD_ID, "dictionaries/en_us/dictionary.txt")).get().open();
		}
	}

	public static void buildLanguageMap(String locale) {
		try {
			String line;
			InputStream dictionary = DictionaryUtil.getDictionaryWords(locale);
			BufferedReader dictReader = new BufferedReader(new InputStreamReader(dictionary, Charset.forName("UTF-8")));
			CommonClass.setDict(new SpellDictionaryHashMap());
			if (dictReader != null) {
				while ((line = dictReader.readLine()) != null) {
					//build dictionary
					CommonClass.getDict().addWord(line);
				}

				dictReader.close();
				dictReader = null;
				dictionary = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addPersonalToLanguageMap() {
		try {
			if (personalDictionary != null && personalDictionary.exists()) {
				Scanner scanner = new Scanner(personalDictionary);

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().toLowerCase();
					if (!line.isEmpty()) {
						if (!CommonClass.getDict().isCorrect(line)) {
							CommonClass.getDict().addWord(line);
						}
					}
				}
				scanner.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
