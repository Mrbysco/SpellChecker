package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.neoforged.fml.config.ModConfig;

public class SpellCheckerFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, SpellCheckerConfig.clientSpec);

		CommonClass.init();
	}
}
