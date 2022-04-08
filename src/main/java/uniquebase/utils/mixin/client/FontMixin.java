package uniquebase.utils.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(FontRenderer.class)
@OnlyIn(Dist.CLIENT)
public class FontMixin
{
	private static final ResourceLocation ID = new ResourceLocation("ue", "hacking");
	private final Font font = new Font(null, null) {
		public IGlyph getGlyphInfo(int p_238557_1_) {
			return () -> 9F;
		};
	};
	
	@Inject(method="getFontSet", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void getFont(ResourceLocation location, CallbackInfoReturnable<Font> returnType)
	{
		if(location.equals(ID))
		{
			returnType.setReturnValue(font);
		}
		
	}
}
