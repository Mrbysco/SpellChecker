package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class SpellCheckerNeoForge {

	public SpellCheckerNeoForge(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.CLIENT, SpellCheckerConfig.clientSpec);
		eventBus.register(SpellCheckerConfig.class);

		CommonClass.init();
	}
}