package com.mrbysco.spellchecker.platform;

import com.mrbysco.spellchecker.SpellCheckerFabric;
import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.platform.services.IPlatformHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public String getConfiguredLocale() {
		if (SpellCheckerFabric.config == null)
			SpellCheckerFabric.config = AutoConfig.getConfigHolder(SpellCheckerConfig.class).getConfig();
		return SpellCheckerFabric.config.client.language_to_check.getLocale();
	}

	@Override
	public int getCheckingThreshold() {
		if (SpellCheckerFabric.config == null)
			SpellCheckerFabric.config = AutoConfig.getConfigHolder(SpellCheckerConfig.class).getConfig();
		return SpellCheckerFabric.config.client.checking_threshold;
	}

	@Override
	public int getMaxSuggestions() {
		if (SpellCheckerFabric.config == null)
			SpellCheckerFabric.config = AutoConfig.getConfigHolder(SpellCheckerConfig.class).getConfig();
		return SpellCheckerFabric.config.client.max_suggestions;
	}
}
