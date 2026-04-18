package com.mrbysco.spellchecker.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

	/**
	 * Gets the path to the mods directory
	 *
	 * @return the path to the mods directory
	 */
	Path getConfigDir();
}
