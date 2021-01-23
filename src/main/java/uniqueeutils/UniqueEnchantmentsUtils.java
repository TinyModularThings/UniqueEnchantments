package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import uniquee.UniqueEnchantments;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.FaminesOdiumEnchantment;
import uniqueeutils.enchantments.RocketManEnchantment;
import uniqueeutils.enchantments.SleipnirsGraceEnchantment;
import uniqueeutils.enchantments.ThickPickEnchantment;
import uniqueeutils.handler.UtilsHandler;

//@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
@Mod("uniqueutil")
public class UniqueEnchantmentsUtils
{
	public static Enchantment SLEIPNIRS_GRACE = new SleipnirsGraceEnchantment();
	public static Enchantment FAMINES_ODIUM = new FaminesOdiumEnchantment();
	public static Enchantment THICK_PICK = new ThickPickEnchantment();
	public static Enchantment ROCKET_MAN = new RocketManEnchantment();
	
	public UniqueEnchantmentsUtils()
	{
		UniqueEnchantments.registerEnchantments(SLEIPNIRS_GRACE, FAMINES_ODIUM, THICK_PICK, ROCKET_MAN);		
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPickEnchantment.VALIDATOR, ThickPickEnchantment.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqeeutil.stored.repair.name", ThickPickEnchantment.TAG);
	}
}
