
package com.aizistral.nochatreports.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@Mixin(Options.class)
public class MixinOptions {
	private OptionInstance<Boolean> alternativeOption;

	/**
	 * @reason Disable secure chat option, since it will not work anyways.
	 * @author Aizistral
	 */

	@Inject(method = "onlyShowSecureChat", at = @At("RETURN"), cancellable = true)
	private void onlyShowSecureChat(CallbackInfoReturnable<OptionInstance<Boolean>> cir) {
		if (this.alternativeOption == null) {
			this.alternativeOption = new OptionInstance<>("options.onlyShowSecureChat",
					OptionInstance.cachedConstantTooltip(Component.translatable("gui.nochatreport.secureChat")),
					(component, value) -> value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
							new OptionInstance.Enum<>(ImmutableList.of(Boolean.FALSE), Codec.BOOL), false, (value) -> {});
		}

		cir.setReturnValue(this.alternativeOption);
	}

}