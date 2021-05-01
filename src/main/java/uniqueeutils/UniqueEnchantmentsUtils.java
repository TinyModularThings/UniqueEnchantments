package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.ClimberEnchantment;
import uniqueeutils.enchantments.FaminesOdiumEnchantment;
import uniqueeutils.enchantments.PhanesRegretEnchantment;
import uniqueeutils.enchantments.PoseidonsSoulEnchantment;
import uniqueeutils.enchantments.RocketManEnchantment;
import uniqueeutils.enchantments.SleipnirsGraceEnchantment;
import uniqueeutils.enchantments.ThickPickEnchantment;
import uniqueeutils.handler.UtilsHandler;

@Mod("uniqueutil")
public class UniqueEnchantmentsUtils extends BaseUEMod
{
	public static Enchantment SLEIPNIRS_GRACE;
	public static Enchantment FAMINES_ODIUM;
	public static Enchantment THICK_PICK;
	public static Enchantment ROCKET_MAN;
	public static Enchantment CLIMBER;
	public static Enchantment PHANES_REGRET;
	public static Enchantment POSEIDONS_SOUL;
	
	public UniqueEnchantmentsUtils()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGraceEnchantment());
		FAMINES_ODIUM = register(new FaminesOdiumEnchantment());
		THICK_PICK = register(new ThickPickEnchantment());
		ROCKET_MAN = register(new RocketManEnchantment());
		CLIMBER = register(new ClimberEnchantment());
		PHANES_REGRET = register(new PhanesRegretEnchantment());
		POSEIDONS_SOUL = register(new PoseidonsSoulEnchantment());
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Utils.toml");
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPickEnchantment.VALIDATOR, ThickPickEnchantment.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueutil.stored.repair.name", ThickPickEnchantment.TAG);
	}
}
