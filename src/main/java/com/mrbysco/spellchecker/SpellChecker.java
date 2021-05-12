package com.mrbysco.spellchecker;

import com.mrbysco.spellchecker.config.LanguageEnum;
import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.handlers.ChatSpellHandler;
import com.mrbysco.spellchecker.util.DictionaryUtil;
import com.swabunga.spell.engine.SpellDictionary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(Reference.MOD_ID)
public class SpellChecker {
	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

	public SpellChecker() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SpellCheckerConfig.clientSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(SpellCheckerConfig.class);

		LOGGER.info("Detecting / Creating Personal Dictionary Folder");
		DictionaryUtil.personalFolder = new File(FMLPaths.MODSDIR.get().toFile(), "/personaldictionary");
		if(!DictionaryUtil.personalFolder.exists()) {
			DictionaryUtil.personalFolder.mkdirs();
		}
		DictionaryUtil.personalDictionary = new File(DictionaryUtil.personalFolder, "/dictionary.txt");

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			// Register the setup method for modloading
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

			MinecraftForge.EVENT_BUS.addListener(ChatSpellHandler::guiOpenEvent);
			MinecraftForge.EVENT_BUS.addListener(ChatSpellHandler::guiScreenEvent);
			MinecraftForge.EVENT_BUS.addListener(ChatSpellHandler::chatEvent);
			MinecraftForge.EVENT_BUS.addListener(ChatSpellHandler::guiDrawEvent);
			MinecraftForge.EVENT_BUS.addListener(ChatSpellHandler::onMouseClick);
		});

		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(()-> "Trans rights are human rights", (remoteversionstring,networkbool)->networkbool));
	}

    public static SpellDictionary dict;

	private void setup(final FMLCommonSetupEvent event) {
		if(SpellChecker.dict == null) {
			DictionaryUtil.buildLanguageMap(((LanguageEnum)SpellCheckerConfig.CLIENT.language_to_check.get()).getLocale());
		}
	}
}