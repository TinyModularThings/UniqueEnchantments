package uniquebase.utils.mixin.common.item;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@Mixin(MapItemSavedData.class)
public interface MapDataMixin
{
	@Accessor("bannerMarkers")
	Map<String, MapBanner> getBanners();
}
