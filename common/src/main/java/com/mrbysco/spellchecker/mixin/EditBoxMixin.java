package com.mrbysco.spellchecker.mixin;

import com.mrbysco.spellchecker.config.SpellCheckerConfig;
import com.mrbysco.spellchecker.util.DictionaryUtil;
import com.mrbysco.spellchecker.util.SuggestionRendering;
import com.mrbysco.spellchecker.util.SuggestionUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public class EditBoxMixin {

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/network/chat/Component;)V")
	private void spellchecker$init(CallbackInfo info) {
		EditBox editBox = (EditBox) (Object) this;
		SuggestionUtil.currentLocale = SpellCheckerConfig.CLIENT.language_to_check.get().getLocale();
		DictionaryUtil.addPersonalToLanguageMap();
		SuggestionUtil.refreshSuggestions(editBox);
	}

	@Inject(at = @At("TAIL"), method = "onClick(Lnet/minecraft/client/input/MouseButtonEvent;Z)V")
	public void spellchecker$onClick(MouseButtonEvent event, boolean doubleClick, CallbackInfo info) {
		EditBox editBox = (EditBox) (Object) this;
		SuggestionUtil.onMouseClicked(event, doubleClick, editBox);
	}

	@Inject(at = @At("TAIL"), method = "onValueChange(Ljava/lang/String;)V")
	public void spellchecker$onValueChange(String value, CallbackInfo info) {
		EditBox editBox = (EditBox) (Object) this;
		SuggestionUtil.refreshSuggestions(editBox);
	}

	@Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z")
	public void spellchecker$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		EditBox editBox = (EditBox) (Object) this;
		SuggestionUtil.onKeyPressed(event, editBox);
	}

	@Inject(at = @At("TAIL"), method = "extractWidgetRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V")
	public void spellchecker$extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo info) {
		EditBox editBox = (EditBox) (Object) this;
		SuggestionRendering.extractSuggestions(graphics, mouseX, mouseY, a, editBox);
	}
}
