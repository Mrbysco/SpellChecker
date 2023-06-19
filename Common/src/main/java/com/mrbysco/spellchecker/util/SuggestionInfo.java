package com.mrbysco.spellchecker.util;

import java.util.List;

public record SuggestionInfo(int posX, int posY, List<String> suggestions, String word) {
}
