package com.mrbysco.spellchecker.config;

import com.mrbysco.spellchecker.Constants;
import com.mrbysco.spellchecker.language.LanguageEnum;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Constants.MOD_ID)
public class SpellCheckerConfig implements ConfigData {
	@CollapsibleObject
	public Client client = new Client();

	public static class Client {
		//General
		@Comment("LanguageEnum locale the mod uses to check your chat messages. [default: EN_US]")
		public LanguageEnum language_to_check = LanguageEnum.EN_US;

		@Comment("The threshold the mod uses to check how close a word needs to be to the wrongly spelled word. [default: 0]")
		@BoundedDiscrete(min = 0, max = Integer.MAX_VALUE)
		public int checking_threshold = 0;

		@Comment("The maximum number of suggestions it will show you. [default: 4]")
		@BoundedDiscrete(min = 0, max = 20)
		public int max_suggestions = 4;
	}
}
