package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class SpellCheckerNeoForge {

	public SpellCheckerNeoForge(Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, SpellCheckerConfig.clientSpec);

		CommonClass.init();

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}
}