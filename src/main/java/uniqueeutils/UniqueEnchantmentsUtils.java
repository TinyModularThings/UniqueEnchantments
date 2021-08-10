package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.Climber;
import uniqueeutils.enchantments.DemetersBlessing;
import uniqueeutils.enchantments.FaminesOdium;
import uniqueeutils.enchantments.MountingAegis;
import uniqueeutils.enchantments.PhanesRegret;
import uniqueeutils.enchantments.PoseidonsSoul;
import uniqueeutils.enchantments.RocketMan;
import uniqueeutils.enchantments.SleipnirsGrace;
import uniqueeutils.enchantments.ThickPick;
import uniqueeutils.handler.UtilsHandler;

//@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
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
	public static Enchantment MOUNTING_AEGIS;
	public static Enchantment DETEMERS_BLESSING;
	
	public UniqueEnchantmentsUtils()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGrace());
		FAMINES_ODIUM = register(new FaminesOdium());
		THICK_PICK = register(new ThickPick());
		ROCKET_MAN = register(new RocketMan());
		CLIMBER = register(new Climber());
		PHANES_REGRET = register(new PhanesRegret());
		POSEIDONS_SOUL = register(new PoseidonsSoul());
		MOUNTING_AEGIS = register(new MountingAegis());
		DETEMERS_BLESSING = register(new DemetersBlessing());
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Utils.toml");
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueutil.stored.repair.name", ThickPick.TAG);
	}
}
