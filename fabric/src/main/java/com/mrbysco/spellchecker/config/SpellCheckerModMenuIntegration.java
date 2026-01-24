package com.mrbysco.spellchecker.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;

public class SpellCheckerModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfigClient.getConfigScreen(SpellCheckerConfig.class, parent).get();
	}
}