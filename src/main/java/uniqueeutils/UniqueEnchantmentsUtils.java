package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.FaminesOdiumEnchantment;
import uniqueeutils.enchantments.RocketManEnchantment;
import uniqueeutils.enchantments.SleipnirsGraceEnchantment;
import uniqueeutils.enchantments.ThickPickEnchantment;
import uniqueeutils.handler.UtilsHandler;

//@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
@Mod("uniqueutil")
public class UniqueEnchantmentsUtils extends BaseUEMod
{
	public static Enchantment SLEIPNIRS_GRACE;
	public static Enchantment FAMINES_ODIUM;
	public static Enchantment THICK_PICK;
	public static Enchantment ROCKET_MAN;
	
	public UniqueEnchantmentsUtils()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGraceEnchantment());
		FAMINES_ODIUM = register(new FaminesOdiumEnchantment());
		THICK_PICK = register(new ThickPickEnchantment());
		ROCKET_MAN = register(new RocketManEnchantment());	
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Utils.toml");
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPickEnchantment.VALIDATOR, ThickPickEnchantment.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueutil.stored.repair.name", ThickPickEnchantment.TAG);
	}
}
