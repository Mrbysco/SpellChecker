package com.mrbysco.spellchecker.util;

import com.mrbysco.spellchecker.Reference;
import com.mrbysco.spellchecker.SpellChecker;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

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

	public static void AddToPersonal(String word) throws IOException {
		try {
			if (personalDictionary.exists()) {
				FileWriter fileWriter = new FileWriter(personalDictionary, true);
				if(!containsWordAlready(word.toLowerCase()))
				{
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

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean containsWordAlready(String word) throws FileNotFoundException {
		boolean found = false;
		try {
		    Scanner scanner = new Scanner(personalDictionary);

		    //now read the file line by line...
		    int lineNum = 0;
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine().toLowerCase();
		        if(line.equals(word)) {
				   scanner.close();
		           return true;
		        }
		    }
		    scanner.close();
		}
		catch(FileNotFoundException e) {
		    //handle this
		}
		return found;
	}

	public static InputStream getDictionaryWords(String locale_name) throws IOException {
		if(!locale_name.isEmpty()) {
			return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(Reference.MOD_ID, "dictionaries/" + locale_name + "/dictionary.txt")).getInputStream();
		} else {
			return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(Reference.MOD_ID, "dictionaries/en_us/dictionary.txt")).getInputStream();
		}
	}

	public static void buildLanguageMap(String locale) {
		try {
			String line;
			InputStream dictionary = DictionaryUtil.getDictionaryWords(locale);
			BufferedReader dictReader = new BufferedReader(new InputStreamReader(dictionary, Charset.forName("UTF-8")));
			SpellChecker.dict = new SpellDictionaryHashMap();
			if(dictReader != null) {
				while ((line = dictReader.readLine()) != null) {
		            //build dictionary
					SpellChecker.dict.addWord(line);
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
			if(personalDictionary != null && personalDictionary.exists()) {
				Scanner scanner = new Scanner(personalDictionary);

			    int lineNum = 0;
			    while (scanner.hasNextLine()) {
			        String line = scanner.nextLine().toLowerCase();
			        if(!line.isEmpty()) {
				        if(!SpellChecker.dict.isCorrect(line)) {
							SpellChecker.dict.addWord(line);
				        }
			        }
			    }
			    scanner.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
