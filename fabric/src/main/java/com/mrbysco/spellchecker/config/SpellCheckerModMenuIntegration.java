package com.mrbysco.spellchecker.config;

import com.mrbysco.spellchecker.Constants;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class SpellCheckerModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (Screen screen) -> new ConfigurationScreen(Constants.MOD_ID, screen);
	}
}