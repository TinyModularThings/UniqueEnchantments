package uniqueeutils;

import java.io.File;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.handler.BaseHandler;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.BouncyDudes;
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
import uniqueeutils.potion.SaturationPotion;

@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.0", dependencies = "required-after:uniquebase@[1.0.0,);")
public class UniqueEnchantmentsUtils extends BaseUEMod
{
	public static Enchantment SLEIPNIRS_GRACE;
	public static Enchantment FAMINES_ODIUM;
	public static Enchantment THICK_PICK;
	public static Enchantment ROCKET_MAN;
	public static Enchantment CLIMBER;
	public static Enchantment PHANES_REGRET;
	public static Enchantment MOUNTING_AEGIS;
	public static Enchantment DETEMERS_BLESSING;
	public static Enchantment DEMETERS_SOUL;
	public static Enchantment AMBROSIA;
	public static Enchantment BOUNCY_DUDES;
	
	public static Potion SATURATION = new SaturationPotion();

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ForgeRegistries.POTIONS.register(SATURATION.setRegistryName("saturation"));
		init("uniqueeutil", new File(event.getModConfigurationDirectory(), "UniqueEnchantments_Utils.cfg"));
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		BaseHandler.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueeutil.stored.repair.name", ThickPick.TAG);
	}

	@Override
	protected void addEnchantments()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGrace());
		FAMINES_ODIUM = register(new FaminesOdium());
		THICK_PICK = register(new ThickPick());
		ROCKET_MAN = register(new RocketMan());
		CLIMBER = register(new Climber());
		PHANES_REGRET = register(new PhanesRegret());
		MOUNTING_AEGIS = register(new MountingAegis());
		DETEMERS_BLESSING = register(new DemetersBlessing());
		DEMETERS_SOUL = register(new DemetersSoul());
		AMBROSIA = register(new Ambrosia());
		BOUNCY_DUDES = register(new BouncyDudes());
	}
}