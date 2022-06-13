package com.mrbysco.spellchecker;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SpellCheckerForge {

	public SpellCheckerForge() {
		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, () ->
				new IExtensionPoint.DisplayTest(() -> "Trans Rights Are Human Rights", (remoteVersionString, networkBool) -> networkBool));

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			CommonClass.init();
		});
	}
}