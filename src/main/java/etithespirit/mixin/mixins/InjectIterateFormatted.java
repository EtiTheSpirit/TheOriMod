package etithespirit.mixin.mixins;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StringDecomposer.class)
public abstract class InjectIterateFormatted {
	
	// L99
	
	@SuppressWarnings("InvalidInjectorMethodSignature") // This is correct, due to the local capture
	@Inject(
		method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
		at = @At(value = "JUMP", ordinal = 1),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void interceptFormattingCode(String pText, int pSkip, Style pCurrentStyle, Style pDefaultStyle, FormattedCharSink pSink, CallbackInfoReturnable<Boolean> cir, int i, Style style, int j, char c0) {
		if (c0 == '^') {
		
		}
	}
	
}