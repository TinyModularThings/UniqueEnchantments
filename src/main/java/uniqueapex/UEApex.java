package uniqueapex;

import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.Pickaxe404;
import uniqueapex.enchantments.simple.SecondLife;
import uniqueapex.enchantments.unique.AeonsFragment;
import uniqueapex.enchantments.unique.GaiasFragment;
import uniqueapex.handler.ApexHandler;
import uniqueapex.handler.FusionHandler;
import uniqueapex.handler.recipe.fusion.FusionRecipe;
import uniqueapex.handler.recipe.fusion.FusionRecipeSerializer;
import uniqueapex.handler.recipe.upgrade.FusionUpgradeRecipe;
import uniqueapex.handler.recipe.upgrade.FusionUpgradeSerializer;
import uniqueapex.network.SyncRecipePacket;
import uniquebase.UEBase;
import uniquebase.api.BaseUEMod;

@Mod("uniqueapex")
public class UEApex extends BaseUEMod
{
	public static final RecipeType<FusionRecipe> FUSION = new RecipeType<FusionRecipe>() {
		@Override
		public String toString() { return "fusion"; }
	}; 
	public static final RecipeType<FusionUpgradeRecipe> FUSION_UPGRADE = new RecipeType<FusionUpgradeRecipe>() {
		@Override
		public String toString() { return "fusion_upgrade"; }
	};
	public static final FusionRecipeSerializer FUSION_SERIALIZER = new FusionRecipeSerializer();
	public static final FusionUpgradeSerializer UPGRADE_SERIALIZER = new FusionUpgradeSerializer();
	
	//Simple
	public static Enchantment ABSOLUTE_PROTECTION;
	public static Enchantment BLESSED_BLADE;
	public static Enchantment SECOND_LIFE;
	public static Enchantment PICKAXE_404;
	
	//Unique
	public static Enchantment AEONS_FRAGMENT;
	public static Enchantment GAIAS_FRAGMENT;
	
	public static final Object2FloatMap<Enchantment> UPGRADE_MULTIPLIERS = new Object2FloatOpenHashMap<>();
	public static ConfigValue<List<? extends String>> ENCHANTMENT_UPGRADE_CONFIGS;
	
	public static final SoundEvent SECOND_LIFE_SOUND = new SoundEvent(new ResourceLocation("uniqueapex", "second_life"));
	
	public UEApex()
	{
		UEBase.NETWORKING.registerInternalPacket(this, SyncRecipePacket.class, SyncRecipePacket::new, 30);
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UEApex.toml");
		MinecraftForge.EVENT_BUS.register(FusionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ApexHandler.INSTANCE);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerContent);

		UPGRADE_MULTIPLIERS.defaultReturnValue(1F);
	}
	
	public void registerContent(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_TYPES))
		{
			event.getForgeRegistry().register(new ResourceLocation("uniqueapex", "fusion"), FUSION);
			event.getForgeRegistry().register(new ResourceLocation("uniqueapex", "fusion_upgrade"), FUSION_UPGRADE);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
		{
			event.getForgeRegistry().register(new ResourceLocation("uniqueapex", "fusion"), FUSION_SERIALIZER);
			event.getForgeRegistry().register(new ResourceLocation("uniqueapex", "fusion_upgrade"), UPGRADE_SERIALIZER);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS))
		{
			event.getForgeRegistry().register("second_life", SECOND_LIFE_SOUND);
		}
	}
	
	@Override
	protected void addConfig(Builder builder)
	{
		builder.comment("Multipliers of Enchantment Upgrades, format: minecraft:sharpness;5.2312");
		ENCHANTMENT_UPGRADE_CONFIGS = builder.defineList("upgrade_multipliers", Collections.emptyList(), T -> true);
	}
	
	@Override
	protected void reloadConfig()
	{
		super.reloadConfig();
		UPGRADE_MULTIPLIERS.clear();
		List<? extends String> list = ENCHANTMENT_UPGRADE_CONFIGS.get();
		for(int i = 0;i<list.size();i++)
		{
			String s = list.get(i);
			String[] split = s.split(";");
			if(split.length == 2)
			{
				try
				{
					ResourceLocation location = ResourceLocation.tryParse(split[0]);
					if(location != null)
					{
						UPGRADE_MULTIPLIERS.put(ForgeRegistries.ENCHANTMENTS.getValue(location), Float.parseFloat(split[1]));
						continue;
					}
				}
				catch(Exception e)
				{
				}
				UEBase.LOGGER.info("Parsing of Enchantment Multipliers failed. Entry: "+s);
			}
		}
	}
	
	@Override
	protected void loadEnchantments()
	{
		ABSOLUTE_PROTECTION = register(new AbsoluteProtection());
		BLESSED_BLADE = register(new BlessedBlade());
		SECOND_LIFE = register(new SecondLife());
		PICKAXE_404 = register(new Pickaxe404());
		
		AEONS_FRAGMENT = register(new AeonsFragment());
		GAIAS_FRAGMENT = register(new GaiasFragment());
	}
}
