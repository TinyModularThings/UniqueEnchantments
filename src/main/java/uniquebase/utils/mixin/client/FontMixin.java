package uniquebase.utils.mixin.client;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(Font.class)
@OnlyIn(Dist.CLIENT)
public class FontMixin
{
	private static final ResourceLocation ID = new ResourceLocation("ue", "hacking");
	private final GlyphInfo info = new GlyphInfo() {
		@Override
		public float getAdvance() { return 9F; }
		@Override
		public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_231088_) { return EmptyGlyph.INSTANCE; }
	};
	private final FontSet font = new FontSet(null, null) {
		@Override
		public GlyphInfo getGlyphInfo(int p_243235_, boolean p_243251_) { return info; }
		@Override
		public BakedGlyph getGlyph(int p_95079_) { return EmptyGlyph.INSTANCE; }
		@Override
		public BakedGlyph getRandomGlyph(GlyphInfo p_95068_) { return EmptyGlyph.INSTANCE; }
	};
	
	@Inject(method="getFontSet", at=@At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void getFont(ResourceLocation location, CallbackInfoReturnable<FontSet> returnType)
	{
		if(location.equals(ID))
		{
			returnType.setReturnValue(font);
		}
		
	}
}
