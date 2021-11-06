package uniquebase.utils.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.storage.MapBanner;
import net.minecraft.world.storage.MapData;

@Mixin(MapData.class)
public interface MapDataMixin
{
	@Accessor("bannerMarkers")
	Map<String, MapBanner> getBanners();
}
