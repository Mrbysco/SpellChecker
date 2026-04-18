package com.mrbysco.spellchecker.platform;

import com.mrbysco.spellchecker.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
