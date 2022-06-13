package com.mrbysco.spellchecker.platform;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.platform.services.IPlatformHelper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public String getConfiguredLocale() {
		return SpellCheckerConfig.CLIENT.language_to_check.get().getLocale();
	}

	@Override
	public int getCheckingThreshold() {
		return SpellCheckerConfig.CLIENT.checking_threshold.get();
	}

	@Override
	public int getMaxSuggestions() {
		return SpellCheckerConfig.CLIENT.max_suggestions.get();
	}
}
