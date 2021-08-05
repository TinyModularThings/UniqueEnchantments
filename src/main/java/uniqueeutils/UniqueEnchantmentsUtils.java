package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquee.UniqueEnchantments;
import uniquee.handler.EntityEvents;
import uniqueeutils.enchantments.Climber;
import uniqueeutils.enchantments.DemetersBlessing;
import uniqueeutils.enchantments.DemetersSoul;
import uniqueeutils.enchantments.FaminesOdium;
import uniqueeutils.enchantments.MountingAegis;
import uniqueeutils.enchantments.PhanesRegret;
import uniqueeutils.enchantments.RocketMan;
import uniqueeutils.enchantments.SleipnirsGrace;
import uniqueeutils.enchantments.ThickPick;
import uniqueeutils.handler.UtilsHandler;

@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquee@[1.9.0,);")
public class UniqueEnchantmentsUtils
{
	public static Enchantment SLEIPNIRS_GRACE = new SleipnirsGrace();
	public static Enchantment FAMINES_ODIUM = new FaminesOdium();
	public static Enchantment THICK_PICK = new ThickPick();
	public static Enchantment ROCKET_MAN = new RocketMan();
	public static Enchantment CLIMBER = new Climber();
	public static Enchantment PHANES_REGRET = new PhanesRegret();
	public static Enchantment MOUNTING_AEGIS = new MountingAegis();
	public static Enchantment DETEMERS_BLESSING = new DemetersBlessing();
	public static Enchantment DEMETERS_SOUL = new DemetersSoul();

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		UniqueEnchantments.registerEnchantments(SLEIPNIRS_GRACE, FAMINES_ODIUM, THICK_PICK, ROCKET_MAN, CLIMBER, PHANES_REGRET, MOUNTING_AEGIS, DETEMERS_BLESSING, DEMETERS_SOUL);
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		EntityEvents.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueeutil.stored.repair.name", ThickPick.TAG);
	}
	
}
