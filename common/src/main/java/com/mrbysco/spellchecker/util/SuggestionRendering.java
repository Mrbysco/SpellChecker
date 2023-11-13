package com.mrbysco.spellchecker.util;

import com.mrbysco.spellchecker.mixin.ChatScreenAccessor;
import com.mrbysco.spellchecker.mixin.EditBoxAccessor;
import com.mrbysco.spellchecker.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionRendering {
	public static void renderSuggestions(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, ChatScreen chat) {
		final Font font = Minecraft.getInstance().font;
		if (!SuggestionUtil.keptSuggestions.isEmpty()) {
			for (SuggestionInfo info : SuggestionUtil.keptSuggestions) {
				int posX = info.posX();
				int posY = info.posY();
				String word = info.word();
				List<String> suggestions = info.suggestions();

				drawInfoTooltip(guiGraphics, font, suggestions, posX, posY);
			}
		}

		if (SuggestionUtil.wronglySpelledWords != null && !SuggestionUtil.wronglySpelledWords.isEmpty() &&
				SuggestionUtil.wordSuggestions != null && !SuggestionUtil.wordSuggestions.isEmpty() &&
				SuggestionUtil.wordPosition != null && !SuggestionUtil.wordPosition.isEmpty()) {
			final boolean showSuggestionsLive = Services.PLATFORM.showSuggestionsLive();
			for (int i = 0; i < SuggestionUtil.wronglySpelledWords.size(); i++) {
				boolean isLastWord = i == SuggestionUtil.wronglySpelledWords.size() - 1;
				String word = SuggestionUtil.wronglySpelledWords.get(i);
				ArrayList<String> suggestions = SuggestionUtil.wordSuggestions.get(word);

				EditBox editBox = ((ChatScreenAccessor) chat).spellchecker_getEditbox();
				int lineScrollOffset = ((EditBoxAccessor) editBox).spellchecker_getDisplayPos();
				String chatText = editBox.getValue();

				if (chatText.length() > lineScrollOffset) {
					String currentlyDisplayedText = chatText.substring(lineScrollOffset);

					for (LocationData data : SuggestionUtil.wordPosition) {
						String originalWord = data.word();
						String wordUntilTypo = data.wordsUntil();

						if (originalWord.equals(word)) {
							if (currentlyDisplayedText.contains(wordUntilTypo)) {
								int width = font.width(wordUntilTypo);

								StringBuilder wrongSquigly = new StringBuilder();
								wrongSquigly.append("~".repeat(word.length()));

								if (font.width(word) <= font.width(wrongSquigly.toString())) {
									int left = font.width(wrongSquigly.toString()) - font.width(word);
									int removeCount = (int) Math.floor((double) left / (double) font.width("~"));
									wrongSquigly = new StringBuilder(wrongSquigly.substring(removeCount));
								}

								if (font.width(word) <= font.width(wrongSquigly.toString())) {
									wrongSquigly = new StringBuilder(wrongSquigly.substring(1));
								}

								if (font.width(wrongSquigly.toString()) == 0 && font.width(word) > 0) {
									wrongSquigly = new StringBuilder("~");
								}

								guiGraphics.drawString(font, wrongSquigly.toString(), width + 4, chat.height - 4, 16733525, false);
								boolean hoveredFlag = SuggestionUtil.hoverBoolean(mouseX, mouseY, 2 + width, chat.height - 12, font.width(word), font.lineHeight);
								if (hoveredFlag || (showSuggestionsLive && isLastWord)) {
									drawInfoTooltip(guiGraphics, font, suggestions, width - 6, chat.height - (6 + (suggestions.size() * 12)));
								}
							} else {
								String[] Words = currentlyDisplayedText.split(" ");
								if (Words.length > 0) {
									String firstWord = Words[0];
									if (!firstWord.isEmpty() && word.contains(firstWord)) {
										int width = font.width(firstWord);

										StringBuilder wrongSquigly = new StringBuilder();
										wrongSquigly.append("~".repeat(firstWord.length()));

										if (font.width(word) <= font.width(wrongSquigly.toString())) {
											int left = font.width(wrongSquigly.toString()) - font.width(word);
											int removeCount = (int) Math.floor((double) left / (double) font.width("~"));
											wrongSquigly = new StringBuilder(wrongSquigly.substring(removeCount));
										}

										if (font.width(word) <= font.width(wrongSquigly.toString())) {
											wrongSquigly = new StringBuilder(wrongSquigly.substring(1));
										}

										if (font.width(wrongSquigly.toString()) == 0 && font.width(word) > 0) {
											wrongSquigly = new StringBuilder("~");
										}

										guiGraphics.drawString(font, wrongSquigly.toString(), width + 2, chat.height - 4, 16733525, false);
										boolean hoveredFlag = SuggestionUtil.hoverBoolean(mouseX, mouseY, 2 + width, chat.height - 12, font.width(word), font.lineHeight);
										if (hoveredFlag) {
											drawInfoTooltip(guiGraphics, font, suggestions, width - 6, chat.height - (6 + (suggestions.size() * 12)));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void drawInfoTooltip(GuiGraphics guiGraphics, Font font, List<String> textLines, int x, int y) {
		guiGraphics.renderTooltip(font, textLines.stream().map(text -> Component.literal(text).getVisualOrderText()).collect(Collectors.toList()), x, y);
	}
}
