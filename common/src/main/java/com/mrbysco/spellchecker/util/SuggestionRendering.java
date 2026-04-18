package com.mrbysco.spellchecker.util;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.mixin.ChatScreenAccessor;
import com.mrbysco.spellchecker.mixin.EditBoxAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionRendering {
	/**
	 * Renders the suggestions in the chat box
	 *
	 * @param graphics    The GuiGraphicsExtractor instance used for rendering
	 * @param mouseX      The x position of the mouse
	 * @param mouseY      The y position of the mouse
	 * @param partialTick The partial tick time
	 * @param chat        The chat screen where the suggestions are rendered
	 */
	public static void extractSuggestions(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, ChatScreen chat) {
		final Font font = Minecraft.getInstance().font;
		if (!SuggestionUtil.keptSuggestions.isEmpty()) {
			for (SuggestionInfo info : SuggestionUtil.keptSuggestions) {
				int posX = info.posX();
				int posY = info.posY();
				List<String> suggestions = info.suggestions();

				drawInfoTooltip(graphics, font, suggestions, posX, posY);
			}
		}

		if (!SuggestionUtil.wronglySpelledWords.isEmpty() &&
				!SuggestionUtil.wordSuggestions.isEmpty() &&
				!SuggestionUtil.wordPosition.isEmpty()
		) {
			final boolean showSuggestionsLive = SpellCheckerConfig.CLIENT.show_suggestions_live.get();
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

								graphics.text(font, wrongSquigly.toString(), width + 4, chat.height - 4, ARGB.opaque(16733525), false);
								boolean hoveredFlag = SuggestionUtil.hoverBoolean(mouseX, mouseY, 2 + width, chat.height - 12, font.width(word), font.lineHeight);
								if (hoveredFlag || (showSuggestionsLive && isLastWord)) {
									drawInfoTooltip(graphics, font, suggestions, width - 6, chat.height - (6 + (suggestions.size() * 12)));
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

										graphics.text(font, wrongSquigly.toString(), width + 2, chat.height - 4, ARGB.opaque(16733525), false);
										boolean hoveredFlag = SuggestionUtil.hoverBoolean(mouseX, mouseY, 2 + width, chat.height - 12, font.width(word), font.lineHeight);
										if (hoveredFlag) {
											drawInfoTooltip(graphics, font, suggestions, width - 6, chat.height - (6 + (suggestions.size() * 12)));
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

	/**
	 * Draws a tooltip for the given word suggestions at the specified position.
	 *
	 * @param graphics  The GuiGraphicsExtractor instance used for rendering
	 * @param font      The font used for rendering the text
	 * @param textLines The list of text lines to display in the tooltip
	 * @param x         The x position where the tooltip should be drawn
	 * @param y         The y position where the tooltip should be drawn
	 */
	public static void drawInfoTooltip(GuiGraphicsExtractor graphics, Font font, List<String> textLines, int x, int y) {
		graphics.setTooltipForNextFrame(font, textLines.stream().map(text ->
				Component.literal(text).getVisualOrderText()).collect(Collectors.toList()), x, y);
	}
}
