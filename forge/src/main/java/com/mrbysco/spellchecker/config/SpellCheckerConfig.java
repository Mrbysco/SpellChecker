package com.mrbysco.spellchecker.config;

import com.mrbysco.spellchecker.Constants;
import com.mrbysco.spellchecker.language.LanguageEnum;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class SpellCheckerConfig {

	public static class Client {
		public final ForgeConfigSpec.EnumValue<LanguageEnum> language_to_check;
		public final ForgeConfigSpec.IntValue checking_threshold;
		public final ForgeConfigSpec.IntValue max_suggestions;
		public final ForgeConfigSpec.BooleanValue show_suggestions_live;

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("LanguageEnum settings")
					.push("language");

			language_to_check = builder
					.translation("configgui.spellchecker.language_check.language_to_check")
					.comment("LanguageEnum locale the mod uses to check your chat messages. [default: EN_US]")
					.defineEnum("language_to_check", LanguageEnum.EN_US);

			builder.pop();
			builder.comment("Checking settings")
					.push("checking");

			checking_threshold = builder
					.translation("configgui.spellchecker.checking.checking_threshold")
					.comment("The threshold the mod uses to check how close a word needs to be to the wrongly spelled word. [default: 0]")
					.defineInRange("checking_threshold", 0, 0, Integer.MAX_VALUE);

			builder.pop();
			builder.comment("General settings")
					.push("general");

			max_suggestions = builder
					.translation("configgui.spellchecker.general.max_suggestions")
					.comment("The maximum number of suggestions it will show you. [default: 4]")
					.defineInRange("max_suggestions", 4, 1, 20);

			show_suggestions_live = builder
					.translation("configgui.spellchecker.general.show_suggestions_live")
					.comment("Show suggestions live while typing. [default: false]")
					.define("show_suggestions_live", false);

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
