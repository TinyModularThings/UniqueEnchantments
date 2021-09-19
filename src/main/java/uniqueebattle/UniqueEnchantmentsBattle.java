package uniqueebattle;

import java.io.File;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquebase.api.BaseUEMod;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.handler.BattleHandler;

@Mod(modid = "uniqueebattle", name = "Unique Battle Enchantments", version = "1.0.1", dependencies = "required-after:uniquebase@[1.0.0,);")
public class UniqueEnchantmentsBattle extends BaseUEMod
{
	public static Enchantment LUNATIC_DESPAIR;
	public static Enchantment CELESTIAL_BLESSING;
	public static Enchantment ARES_FRAGMENT;
	public static Enchantment IFRITS_BLESSING;
	public static Enchantment IFRITS_JUDGEMENT;
	public static Enchantment GOLEM_SOUL;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		init("uniqueebattle", new File(event.getModConfigurationDirectory(), "UniqueEnchantments_Battle.cfg"));
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
	}

	@Override
	protected void addEnchantments()
	{
		LUNATIC_DESPAIR = register(new LunaticDespair());
		CELESTIAL_BLESSING = register(new CelestialBlessing());
		ARES_FRAGMENT = register(new AresFragment());
		IFRITS_BLESSING = register(new IfritsBlessing());
		IFRITS_JUDGEMENT = register(new IfritsJudgement());
		GOLEM_SOUL = register(new GolemSoul());
	}
}
