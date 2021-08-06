package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquebase.handler.BaseHandler;
import uniquee.UniqueEnchantments;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.DemetersBlessing;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.PhanesRegret;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.ThickPick;
import uniqueeutils.enchantments.unique.DemetersSoul;
import uniqueeutils.enchantments.unique.MountingAegis;
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
		BaseHandler.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueeutil.stored.repair.name", ThickPick.TAG);
	}
	
}
