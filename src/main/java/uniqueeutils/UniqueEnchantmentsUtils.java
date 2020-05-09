package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquee.UniqueEnchantments;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.EnchantmentFaminesOdium;
import uniqueeutils.enchantments.EnchantmentRocketMan;
import uniqueeutils.enchantments.EnchantmentSleipnirsGrace;
import uniqueeutils.enchantments.EnchantmentThickPick;
import uniqueeutils.handler.UtilsHandler;

@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
public class UniqueEnchantmentsUtils
{
	public static Enchantment SLEIPNIRS_GRACE = new EnchantmentSleipnirsGrace();
	public static Enchantment FAMINES_ODIUM = new EnchantmentFaminesOdium();
	public static Enchantment THICK_PICK = new EnchantmentThickPick();
	public static Enchantment ROCKET_MAN = new EnchantmentRocketMan();
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		UniqueEnchantments.registerEnchantments(SLEIPNIRS_GRACE, FAMINES_ODIUM, THICK_PICK, ROCKET_MAN);
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, EnchantmentThickPick.VALIDATOR, EnchantmentThickPick.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqeeutil.stored.repair.name", EnchantmentThickPick.TAG);
	}
	
}
