package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public class SpellCheckerFabric implements ClientModInitializer {
	private Thread watchThread = null;
	public static SpellCheckerConfig config;

	@Override
	public void onInitializeClient() {
		CommonClass.init();

		ConfigHolder<SpellCheckerConfig> holder = AutoConfig.register(SpellCheckerConfig.class, Toml4jConfigSerializer::new);
		config = holder.getConfig();
		try {
			var watchService = FileSystems.getDefault().newWatchService();
			Paths.get("config").register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			watchThread = new Thread(() -> {
				WatchKey key;
				try {
					while ((key = watchService.take()) != null) {
						if (Thread.currentThread().isInterrupted()) {
							watchService.close();
							break;
						}
						for (WatchEvent<?> event : key.pollEvents()) {
							if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
								continue;
							}
							if (((Path) event.context()).endsWith("spellchecker.toml")) {
								Constants.LOGGER.info("Reloading SpellChecker config");
								if (holder.load()) {
									config = holder.getConfig();
								}
							}
						}
						key.reset();
					}
				} catch (InterruptedException ignored) {
				} catch (IOException e) {
					Constants.LOGGER.error("Failed to close filesystem watcher", e);
				}
			}, "Spell Checker Config Watcher");
			watchThread.start();
		} catch (IOException e) {
			Constants.LOGGER.error("Failed to create filesystem watcher for configs", e);
		}
	}
}
