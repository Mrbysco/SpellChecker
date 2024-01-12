package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.IExtensionPoint.DisplayTest;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class SpellCheckerForge {

	public SpellCheckerForge(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SpellCheckerConfig.clientSpec);
		eventBus.register(SpellCheckerConfig.class);

		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, () ->
				new IExtensionPoint.DisplayTest(() -> "Trans Rights Are Human Rights", (remoteVersionString, networkBool) -> networkBool));

		if (FMLEnvironment.dist.isClient()) {
			CommonClass.init();
		}
	}
}