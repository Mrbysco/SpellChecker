package com.mrbysco.spellchecker.util;

import java.util.ArrayList;

public class SuggestionInfo {
	
	private int posX;
	private int posY;
	private ArrayList<String> suggestions;
	private String word;
	
	public SuggestionInfo(int x, int y, ArrayList<String> suggestions, String word) {
		this.posX = x;
		this.posY = y;
		this.suggestions = suggestions;
		this.word = word;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public ArrayList<String> getSuggestions() {
		return suggestions;
	}
	
	public String getWord() {
		return word;
	}
}
