package uniqueebattle;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.handler.BattleHandler;


@Mod("uniquebattle")
public class UniqueEnchantmentsBattle extends BaseUEMod
{
	public static Enchantment LUNATIC_DESPAIR = new LunaticDespair();
	public static Enchantment CELESTIAL_BLESSING = new CelestialBlessing();
	public static Enchantment ARES_FRAGMENT = new AresFragment();
	
	public UniqueEnchantmentsBattle()
	{
		LUNATIC_DESPAIR = register(new LunaticDespair());
		CELESTIAL_BLESSING = register(new CelestialBlessing());
		ARES_FRAGMENT = register(new AresFragment());
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Battle.toml");
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
	}
}
