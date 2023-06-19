package com.mrbysco.spellchecker.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

	/**
	 * Gets the path to the mods directory
	 *
	 * @return the path to the mods directory
	 */
	Path getConfigDir();

	/**
	 * Gets the configured language to be used for spell checking
	 *
	 * @return get configured language
	 */
	String getConfiguredLocale();

	/**
	 * Gets the configured checking threshold
	 *
	 * @return get configured checking threshold
	 */
	int getCheckingThreshold();

	/**
	 * Gets the configured max suggestions
	 *
	 * @return get configured max suggestions
	 */
	int getMaxSuggestions();
}
