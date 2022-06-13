package com.mrbysco.spellchecker.config;

import com.mrbysco.spellchecker.Constants;
import com.mrbysco.spellchecker.language.LanguageEnum;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class SpellCheckerConfig {

	public static class Client {
		public final EnumValue<LanguageEnum> language_to_check;
		public final IntValue checking_threshold;
		public final IntValue max_suggestions;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("LanguageEnum settings")
					.push("language");

			language_to_check = builder
					.translation("configgui.spellchecker.language_check.language.info")
					.comment("LanguageEnum locale the mod uses to check your chat messages. [default: EN_US]")
					.defineEnum("language_to_check", LanguageEnum.EN_US);

			builder.pop();
			builder.comment("Checking settings")
					.push("checking");

			checking_threshold = builder
					.translation("configgui.spellchecker.checking.threshold.info")
					.comment("The threshold the mod uses to check how close a word needs to be to the wrongly spelled word. [default: 0]")
					.defineInRange("checking_threshold", 0, 0, Integer.MAX_VALUE);

			builder.pop();
			builder.comment("General settings")
					.push("general");

			max_suggestions = builder
					.translation("configgui.spellchecker.general.suggestionsize.info")
					.comment("The maximum number of suggestions it will show you. [default: 4]")
					.defineInRange("max_suggestions", 0, 1, 20);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		Constants.LOGGER.debug("Loaded SpellChecker's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		Constants.LOGGER.fatal("SpellChecker's config just got changed on the file system!");
	}
}
