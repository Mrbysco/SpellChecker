package com.mrbysco.spellchecker.platform;

import com.mrbysco.spellchecker.platform.services.IPlatformHelper;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}
}
