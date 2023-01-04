package uniquebase.utils;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;

public class BannerUtils {
	
	static final Map<ResourceKey<BannerPattern>, String> banners = new Object2ObjectOpenHashMap<>();
	public static Map<ResourceKey<BannerPattern>, String> getBanners() {
		return banners;
	}

	public static ResourceKey<BannerPattern> createBanner(String modName, String bannerName, String id, DeferredRegister<Item> reg, Item.Properties props) {
		ResourceKey<BannerPattern> rk = ResourceKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(modName+ ":" +bannerName));
		banners.put(rk, id);
		TagKey<BannerPattern> tag = TagKey.create(Registry.BANNER_PATTERN_REGISTRY, new ResourceLocation(modName + ":pattern_item/" + bannerName));
		reg.register(bannerName+"_banner_pattern", () -> new BannerPatternItem(tag, props));
		return rk;
	}
}
