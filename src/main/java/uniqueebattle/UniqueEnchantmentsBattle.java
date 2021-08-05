package uniqueebattle;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquee.UniqueEnchantments;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.handler.BattleHandler;

@Mod(modid = "uniqueebattle", name = "Unique Battle Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
public class UniqueEnchantmentsBattle
{
	public static Enchantment LUNATIC_DESPAIR = new LunaticDespair();
	public static Enchantment CELESTIAL_BLESSING = new CelestialBlessing();
	public static Enchantment ARES_FRAGMENT = new AresFragment();
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		UniqueEnchantments.registerEnchantments(LUNATIC_DESPAIR, CELESTIAL_BLESSING, ARES_FRAGMENT);
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
	}
}
