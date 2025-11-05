package com.mrbysco.spellchecker.mixin;

import com.mrbysco.spellchecker.platform.Services;
import com.mrbysco.spellchecker.util.DictionaryUtil;
import com.mrbysco.spellchecker.util.SuggestionRendering;
import com.mrbysco.spellchecker.util.SuggestionUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

	@Shadow
	protected EditBox input;

	@Inject(at = @At("TAIL"), method = "init()V")
	private void spellchecker_init(CallbackInfo info) {
		SuggestionUtil.currentLocale = Services.PLATFORM.getConfiguredLocale();
		DictionaryUtil.addPersonalToLanguageMap();
		SuggestionUtil.refreshSuggestions(input);
	}

	@Inject(at = @At("HEAD"), method = "onEdited(Ljava/lang/String;)V")
	private void spellchecker_onEdited(CallbackInfo info) {
		SuggestionUtil.refreshSuggestions(input);
	}

	@Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z")
	public void spellchecker_keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		SuggestionUtil.onKeyPressed(event, input);
	}

	@Inject(at = @At("TAIL"), method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z")
	public void spellchecker_mouseClicked(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
		ChatScreen screen = (ChatScreen) (Object) this;
		SuggestionUtil.onMouseClicked(event, isDoubleClick, screen);
	}

	@Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
	public void spellchecker_render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo info) {
		ChatScreen screen = (ChatScreen) (Object) this;
		SuggestionRendering.renderSuggestions(guiGraphics, mouseX, mouseY, partialTick, screen);
	}
}