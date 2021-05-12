package com.mrbysco.spellchecker.handlers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrbysco.spellchecker.SpellChecker;
import com.mrbysco.spellchecker.config.LanguageEnum;
import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.util.DictionaryUtil;
import com.mrbysco.spellchecker.util.LocationData;
import com.mrbysco.spellchecker.util.SuggestionInfo;
import com.swabunga.spell.engine.Word;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardKeyPressedEvent;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatSpellHandler{

    private static HashMap<String, ArrayList<String>> wordSuggestions;
    private static ArrayList<LocationData> wordPosition;
    private static ArrayList<String> wronglySpelledWords;
    private static String current_local;
    private static String chatText = "";

    private static ArrayList<SuggestionInfo> keptSuggestions = new ArrayList<>();

	public static void guiOpenEvent(GuiOpenEvent event) {
    	if(event.getGui() instanceof ChatScreen) {
			current_local = ((LanguageEnum) SpellCheckerConfig.CLIENT.language_to_check.get()).getLocale();

        	DictionaryUtil.addPersonalToLanguageMap();

    		chatText = "";
    	}
	}

	public static void guiScreenEvent(GuiScreenEvent.InitGuiEvent.Post event) {
    	if(event.getGui() instanceof ChatScreen) {
			ChatScreen chat = (ChatScreen)event.getGui();
    		refreshSuggestions(chat);
    	}
    }


	public static void chatEvent(KeyboardKeyPressedEvent.Pre event) {
		if (Minecraft.getInstance().ingameGUI.getChatGUI().getChatOpen()) {
			if(Minecraft.getInstance().currentScreen instanceof ChatScreen) {
				if(isKeyDown(GLFW.GLFW_KEY_SPACE) || isKeyDown(GLFW.GLFW_KEY_BACKSPACE) || isKeyDown(GLFW.GLFW_KEY_UP) || isKeyDown(GLFW.GLFW_KEY_DOWN) || isKeyDown(GLFW.GLFW_KEY_LEFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
					ChatScreen chat = (ChatScreen) Minecraft.getInstance().currentScreen;

					refreshSuggestions(chat);
		    	}

				if(isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) && isKeyDown(GLFW.GLFW_KEY_BACKSPACE)) {
					keptSuggestions = new ArrayList<>();
		    	}
			}
		}
    }

	private static boolean isKeyDown(int keyCode) {
		return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyCode);
	}

    public static String stripWord(String word) {
    	String strippedWord = word;
    	if(!word.isEmpty()) {
    		if(current_local != null) {
        		if(current_local.equals("nl_nl")) {
            		strippedWord = strippedWord.replace("'s", "");
            		strippedWord = strippedWord.replace("'tje", "");
        		}
    		}

        	strippedWord = strippedWord.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", "");
    	}

    	return strippedWord;
    }

    public static void guiDrawEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
    	if(event.getGui() instanceof ChatScreen) {
    		ChatScreen chat = (ChatScreen)event.getGui();
			FontRenderer fontRenderer = chat.font;

			if(!keptSuggestions.isEmpty()) {
				for(SuggestionInfo info : keptSuggestions) {
					int posX = info.getPosX();
					int posY = info.getPosY();
					String word = info.getWord();
					ArrayList<String> suggestions = info.getSuggestions();

					drawInfoTooltip(event.getMatrixStack(), chat, suggestions, posX, posY, fontRenderer);
				}
			}

			if(wronglySpelledWords != null && !wronglySpelledWords.isEmpty() && wordSuggestions != null && !wordSuggestions.isEmpty() && wordPosition != null && !wordPosition.isEmpty()) {
				for(int i = 0; i < wronglySpelledWords.size(); i++) {
					String word = wronglySpelledWords.get(i);
					ArrayList<String> suggestions = wordSuggestions.get(word);

					TextFieldWidget field = chat.inputField;
					int lineScrollOffset = field.lineScrollOffset;
					if(chatText.length() > lineScrollOffset) {
						String currentlyDisplayedText = chatText.substring(lineScrollOffset,chatText.length());

						for (LocationData data : wordPosition) {
						    String originalWord = data.getWord();
							String wordUntilTypo = data.getWordsUntil();

							if(originalWord.equals(word)) {
								if(currentlyDisplayedText.contains(wordUntilTypo)) {
									int width = fontRenderer.getStringWidth(wordUntilTypo);

									String wrongSquigly = "";
									for(int j = 0; j < word.length(); j++) {
										wrongSquigly = wrongSquigly + "~";
									}

									if(fontRenderer.getStringWidth(word) <= fontRenderer.getStringWidth(wrongSquigly)) {
										int left = fontRenderer.getStringWidth(wrongSquigly) - fontRenderer.getStringWidth(word);
										int removeCount = (int)Math.floor((double)left/(double)fontRenderer.getStringWidth("~"));
										wrongSquigly = wrongSquigly.substring(removeCount);
									}

									if(fontRenderer.getStringWidth(word) <= fontRenderer.getStringWidth(wrongSquigly)) {
										wrongSquigly = wrongSquigly.substring(1);
									}

									if(fontRenderer.getStringWidth(wrongSquigly) == 0 && fontRenderer.getStringWidth(word) > 0) {
										wrongSquigly = "~";
									}

									chat.drawString(event.getMatrixStack(), fontRenderer, wrongSquigly, width + 4, chat.height - 4, 16733525);
									boolean hoveredFlag = hoverBoolean(event.getMouseX(), event.getMouseY(), 2 + width, chat.height - 12, fontRenderer.getStringWidth(word), fontRenderer.FONT_HEIGHT);
									if(hoveredFlag) {
										drawInfoTooltip(event.getMatrixStack(), chat, suggestions, width -6, chat.height - (6 + (suggestions.size() * 12)), fontRenderer);
									}
								} else {
									String[] Words = currentlyDisplayedText.split(" ");
									if(Words.length > 0) {
										String firstWord = Words[0];
										if(!firstWord.isEmpty() && word.contains(firstWord)) {
											int width = fontRenderer.getStringWidth(firstWord);

											String wrongSquigly = "";
											for(int j = 0; j < firstWord.length(); j++) {
												wrongSquigly = wrongSquigly + "~";
											}

											if(fontRenderer.getStringWidth(word) <= fontRenderer.getStringWidth(wrongSquigly)) {
												int left = fontRenderer.getStringWidth(wrongSquigly) - fontRenderer.getStringWidth(word);
												int removeCount = (int)Math.floor((double)left/(double)fontRenderer.getStringWidth("~"));
												wrongSquigly = wrongSquigly.substring(removeCount);
											}

											if(fontRenderer.getStringWidth(word) <= fontRenderer.getStringWidth(wrongSquigly))
											{
												wrongSquigly = wrongSquigly.substring(1);
											}

											if(fontRenderer.getStringWidth(wrongSquigly) == 0 && fontRenderer.getStringWidth(word) > 0)
											{
												wrongSquigly = "~";
											}

											chat.drawString(event.getMatrixStack(), fontRenderer, wrongSquigly, width + 2 , chat.height - 4, 16733525);
											boolean hoveredFlag = hoverBoolean(event.getMouseX(), event.getMouseY(), 2 + width, chat.height - 12, fontRenderer.getStringWidth(word), fontRenderer.FONT_HEIGHT);
											if(hoveredFlag) {
												drawInfoTooltip(event.getMatrixStack(), chat, suggestions, width -6, chat.height - (6 + (suggestions.size() * 12)), fontRenderer);
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
    }

    public static void onMouseClick(GuiScreenEvent.MouseClickedEvent event) {
		if(event.getGui() instanceof ChatScreen && event.getButton() == 1) {
			final MatrixStack matrixStack = new MatrixStack();
			ChatScreen chat = (ChatScreen)event.getGui();
			FontRenderer fontRenderer = chat.font;

			if(wronglySpelledWords != null && !wronglySpelledWords.isEmpty() && wordSuggestions != null && !wordSuggestions.isEmpty() && wordPosition != null && !wordPosition.isEmpty()) {
				for(int i = 0; i < wronglySpelledWords.size(); i++) {
					String word = wronglySpelledWords.get(i);
					ArrayList<String> suggestions = wordSuggestions.get(word);

					TextFieldWidget field = chat.inputField;
					int lineScrollOffset = field.lineScrollOffset;
					if(chatText.length() > lineScrollOffset) {
						String currentlyDisplayedText = chatText.substring(lineScrollOffset);

						for (LocationData data : wordPosition) {
							String originalWord = data.getWord();
							String wordUntilTypo = data.getWordsUntil();

							if(originalWord.equals(word)) {
								if(currentlyDisplayedText.contains(wordUntilTypo)) {
									int width = fontRenderer.getStringWidth(wordUntilTypo);

									boolean hoveredFlag = hoverBoolean((int)event.getMouseX(), (int)event.getMouseY(), 2 + width, chat.height - 12, fontRenderer.getStringWidth(word), fontRenderer.FONT_HEIGHT);
									if(hoveredFlag) {
										drawInfoTooltip(matrixStack, chat, suggestions, width -6, chat.height - (6 + (suggestions.size() * 12)), fontRenderer);

										addToDictionary(chat, word);
										keepSuggestion(chat,width -6, chat.height - 12, word, suggestions);
									}
								} else {
									String[] Words = currentlyDisplayedText.split(" ");
									if(Words.length > 0) {
										String firstWord = Words[0];
										if(!firstWord.isEmpty() && word.contains(firstWord)) {
											int width = fontRenderer.getStringWidth(firstWord);

											boolean hoveredFlag = hoverBoolean((int)event.getMouseX(), (int)event.getMouseY(), 2 + width, chat.height - 12, fontRenderer.getStringWidth(word), fontRenderer.FONT_HEIGHT);
											if(hoveredFlag) {
												drawInfoTooltip(matrixStack, chat, suggestions, width -6, chat.height - (6 + (suggestions.size() * 12)), fontRenderer);

												addToDictionary(chat, word);
												keepSuggestion(chat,width -6, chat.height - 20, word, suggestions);
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
	}

    public static void addToDictionary(ChatScreen chat, String word) {
    	if(isKeyDown( GLFW.GLFW_KEY_LEFT_CONTROL)) {
			try {
				if(DictionaryUtil.personalDictionary == null) {
					DictionaryUtil.personalDictionary = new File(DictionaryUtil.personalFolder, "/dictionary.txt");
				}
				DictionaryUtil.AddToPersonal(word);
				refreshSuggestions(chat);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    public static void keepSuggestion(ChatScreen chat, int x, int y, String word, ArrayList<String> suggestions) {
		if(isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
    		if(keptSuggestions != null) {
    			SuggestionInfo sInfo = new SuggestionInfo(x, y, suggestions, word);
    			if(!keptSuggestions.contains(sInfo)) {
    				keptSuggestions.add(sInfo);
    			}
    		}
		}
    }

    public static void refreshSuggestions(ChatScreen chat) {
    	DictionaryUtil.addPersonalToLanguageMap();

    	wordSuggestions = new HashMap<>();
    	wordPosition = new ArrayList<>();
    	wronglySpelledWords = new ArrayList<>();
		chatText = chat.inputField.getText();

		TextFieldWidget field = chat.inputField;
		int lineScrollOffset = field.lineScrollOffset;
		String visibleString = chatText.substring(lineScrollOffset,chatText.length());

		String[] CurrentWords = visibleString.split(" ");
		if(CurrentWords.length > 0) {
			for(int i = 0; i < CurrentWords.length; i++) {
				String wordToCheck = CurrentWords[i];
				String strippedWord = wordToCheck;

				if(!strippedWord.isEmpty()) {
					if(!SpellChecker.dict.isCorrect(strippedWord)) {
						String extraStripped = stripWord(strippedWord);
						strippedWord = extraStripped;
						if(!strippedWord.isEmpty() && !SpellChecker.dict.isCorrect(strippedWord)) {
							String tillEndOfWord = "";
							for(int j = 0; j <= i; j++) {
								if(j != i) {
									tillEndOfWord = tillEndOfWord + CurrentWords[j];
									tillEndOfWord = tillEndOfWord + " ";
								}
							}
							ArrayList<String> suggestions = getSuggestions(strippedWord);
							LocationData locData = new LocationData(wordToCheck, tillEndOfWord);

							wordSuggestions.put(wordToCheck, suggestions);
							wronglySpelledWords.add(wordToCheck);
							wordPosition.add(locData);
						}
					}
				}
			}
		}
    }

    public static boolean hoverBoolean(int mouseX, int mouseY, int x, int y, int widthIn, int heigthIn) {
    	return mouseX >= x && mouseY >= y && mouseX < x + widthIn && mouseY < y + heigthIn;
    }

    public static ArrayList<String> getSuggestions(String misspelledWord) {
    	int threshold = SpellCheckerConfig.CLIENT.checking_threshold.get();
    	int maxSuggestions = SpellCheckerConfig.CLIENT.max_suggestions.get();

    	List<Word> words = new ArrayList<>();
    	if(!misspelledWord.isEmpty()) {
    		words = SpellChecker.dict.getSuggestions(misspelledWord, threshold);
    	}
        ArrayList<String> suggestions = new ArrayList<String>();

        if(!words.isEmpty()) {
            for (Word suggestion : words) {
            	if(suggestions.size() <= maxSuggestions)
            		suggestions.add(suggestion.getWord());
            }
        }

		return suggestions;
    }

	public static void drawInfoTooltip(MatrixStack matrixStack, ChatScreen screen, List<String> textLines, int x, int y, FontRenderer fontRenderer) {
        if (!textLines.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            int i = 0;

            for (String s : textLines) {
                int j = fontRenderer.getStringWidth(s);

                if (j > i)
                    i = j;
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > screen.width)
                l1 -= 28 + i;

            if (i2 + k + 6 > screen.height)
                i2 = screen.height - k - 6;

			screen.itemRenderer.zLevel = 300.0F;
			ItemRenderer re = screen.itemRenderer;
            re.zLevel = 300.0F;
			screen.itemRenderer = re;
            screen.fill(matrixStack, l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, 0xFFf9eed1);
            screen.fill(matrixStack, l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, 0xFF1c0f00);
            screen.fill(matrixStack, l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, 0xFF1c0f00);
            drawGradientRect(screen, l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, 0xFF75321e, 0xFF5b2312);
            screen.fill(matrixStack, l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, 0xFF1c0f00);
            screen.fill(matrixStack, l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, 0xFF1c0f00);
            screen.fill(matrixStack, l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 0xFF4c1a0b);
            screen.fill(matrixStack, l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 0xFF4c1a0b);
            screen.fill(matrixStack, l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 0xFF4c1a0b);
            screen.fill(matrixStack, l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 0xFF4c1a0b);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                fontRenderer.drawStringWithShadow(matrixStack, s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                    i2 += 2;

                i2 += 10;
            }
//            screen.zLevel = 0.0F;
			ItemRenderer re2 = screen.itemRenderer;
            re2.zLevel = 0.0F;
            screen.itemRenderer = re2;
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
            RenderHelper.enableStandardItemLighting();
            RenderSystem.enableRescaleNormal();
        }
    }

    //TODO: Goes unused somehow...
	public static void drawText(MatrixStack matrixStack, ChatScreen screen, String textLine, int x, int y, FontRenderer fontRenderer, int color) {
        if (!textLine.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
			RenderSystem.disableLighting();
			RenderSystem.disableDepthTest();
            int i = 0;

            int j = fontRenderer.getStringWidth(textLine);

            if (j > i)
                i = j;

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (l1 + i > screen.width)
                l1 -= 28 + i;

            if (i2 + k + 6 > screen.height)
                i2 = screen.height - k - 6;

//			screen.zLevel = 300.0F;
            ItemRenderer re = screen.itemRenderer;
            re.zLevel = 300.0F;
			screen.itemRenderer = re;
            String s1 = textLine;
            fontRenderer.drawStringWithShadow(matrixStack, s1, (float)l1, (float)i2, color);

            i2 += 2;
            i2 += 10;

//			screen.zLevel = 0.0F;
			ItemRenderer re2 = screen.itemRenderer;
            re2.zLevel = 0.0F;
			screen.itemRenderer = re2;
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
            RenderHelper.enableStandardItemLighting();
            RenderSystem.enableRescaleNormal();
        }
    }

    public static void drawGradientRect(ChatScreen screen, int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)screen.itemRenderer.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)screen.itemRenderer.zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)screen.itemRenderer.zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)screen.itemRenderer.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
}
