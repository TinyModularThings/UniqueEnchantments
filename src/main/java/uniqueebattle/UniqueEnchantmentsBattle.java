package uniqueebattle;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniqueebattle.enchantments.EnchantmentAresFragment;
import uniqueebattle.enchantments.EnchantmentCelestialBlessing;
import uniqueebattle.enchantments.EnchantmentLunaticDespair;
import uniqueebattle.handler.BattleHandler;


@Mod("uniquebattle")
public class UniqueEnchantmentsBattle extends BaseUEMod
{
	public static Enchantment LUNATIC_DESPAIR = new EnchantmentLunaticDespair();
	public static Enchantment CELESTIAL_BLESSING = new EnchantmentCelestialBlessing();
	public static Enchantment ARES_FRAGMENT = new EnchantmentAresFragment();
	
	public UniqueEnchantmentsBattle()
	{
		LUNATIC_DESPAIR = register(new EnchantmentLunaticDespair());
		CELESTIAL_BLESSING = register(new EnchantmentCelestialBlessing());
		ARES_FRAGMENT = register(new EnchantmentAresFragment());
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Battle.toml");
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
	}
}
