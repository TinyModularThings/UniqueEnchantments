package uniqueapex;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import uniqueapex.enchantments.simple.AbsoluteProtection;
import uniqueapex.enchantments.simple.BlessedBlade;
import uniqueapex.enchantments.simple.SecondLife;
import uniqueapex.enchantments.unique.AeonsFragment;
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
	public static final IRecipeType<FusionRecipe> FUSION = new IRecipeType<FusionRecipe>() {
		@Override
		public String toString() { return "fusion"; }
	}; 
	public static final IRecipeType<FusionUpgradeRecipe> FUSION_UPGRADE = new IRecipeType<FusionUpgradeRecipe>() {
		@Override
		public String toString() { return "fusion_upgrade"; }
	};
	public static final FusionRecipeSerializer FUSION_SERIALIZER = new FusionRecipeSerializer();
	public static final FusionUpgradeSerializer UPGRADE_SERIALIZER = new FusionUpgradeSerializer();
	
	//Simple
	public static Enchantment ABSOLUTE_PROTECTION;
	public static Enchantment BLESSED_BLADE;
	public static Enchantment SECOND_LIFE;
	
	//Unique
	public static Enchantment AEONS_FRAGMENT;
	
	public static final SoundEvent SECOND_LIFE_SOUND = new SoundEvent(new ResourceLocation("uniqueapex", "second_life"));
	
	public UEApex()
	{
		UEBase.NETWORKING.registerInternalPacket(this, SyncRecipePacket.class, SyncRecipePacket::new, 30);
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UEApex.toml");
		MinecraftForge.EVENT_BUS.register(FusionHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ApexHandler.INSTANCE);
		ForgeRegistries.RECIPE_SERIALIZERS.registerAll(FUSION_SERIALIZER.init(), UPGRADE_SERIALIZER.init());
		Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("uniqueapex", "fusion"), FUSION);
		Registry.register(Registry.RECIPE_TYPE, new ResourceLocation("uniqueapex", "fusion_upgrade"), FUSION_UPGRADE);
		ForgeRegistries.SOUND_EVENTS.register(SECOND_LIFE_SOUND.setRegistryName("second_life"));
	}
	
	@Override
	protected void loadEnchantments()
	{
		ABSOLUTE_PROTECTION = register(new AbsoluteProtection());
		BLESSED_BLADE = register(new BlessedBlade());
		SECOND_LIFE = register(new SecondLife());
		AEONS_FRAGMENT = register(new AeonsFragment());
	}
}
