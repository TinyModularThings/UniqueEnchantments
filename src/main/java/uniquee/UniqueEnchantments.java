package uniquee;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.common.Mod;
import uniquee.enchantments.complex.EnderMendingEnchantment;
import uniquee.enchantments.complex.MomentumEnchantment;
import uniquee.enchantments.complex.PerpetualStrikeEnchantment;
import uniquee.enchantments.complex.SmartAssEnchantment;
import uniquee.enchantments.complex.SpartanWeaponEnchantment;
import uniquee.enchantments.complex.SwiftBladeEnchantment;
import uniquee.enchantments.curse.EnchantmentDeathsOdium;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;
import uniquee.enchantments.simple.AdvancedDamageEnchantment;
import uniquee.enchantments.simple.BerserkEnchantment;
import uniquee.enchantments.simple.BoneCrusherEnchantment;
import uniquee.enchantments.simple.EnderEyesEnchantment;
import uniquee.enchantments.simple.FocusImpactEnchantment;
import uniquee.enchantments.simple.RangeEnchantment;
import uniquee.enchantments.simple.SagesBlessingEnchantment;
import uniquee.enchantments.simple.SwiftEnchantment;
import uniquee.enchantments.simple.TreasurersEyesEnchantment;
import uniquee.enchantments.simple.VitaeEnchantment;
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentClimateTranquility;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentDemetersSoul;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderLibrarian;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentIcarusAegis;
import uniquee.enchantments.unique.EnchantmentIfritsGrace;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentPhoenixBlessing;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.handler.potion.PotionPestilencesOdium;

@Mod("uniquee")
public class UniqueEnchantments
{
	public static Enchantment BERSERKER = new BerserkEnchantment();
	public static Enchantment ADV_SHARPNESS = new AdvancedDamageEnchantment(0);
	public static Enchantment ADV_SMITE = new AdvancedDamageEnchantment(1);
	public static Enchantment ADV_BANE_OF_ARTHROPODS = new AdvancedDamageEnchantment(2);
	public static Enchantment VITAE = new VitaeEnchantment();
	public static Enchantment SWIFT = new SwiftEnchantment();
	public static Enchantment SAGES_BLESSING = new SagesBlessingEnchantment();
	public static Enchantment ENDER_EYES = new EnderEyesEnchantment();
	public static Enchantment FOCUS_IMPACT = new FocusImpactEnchantment();
	public static Enchantment BONE_CRUSH = new BoneCrusherEnchantment();
	public static Enchantment RANGE = new RangeEnchantment();
	public static Enchantment TREASURERS_EYES = new TreasurersEyesEnchantment();
	
	//Complex
	public static Enchantment SWIFT_BLADE = new SwiftBladeEnchantment();
	public static Enchantment SPARTAN_WEAPON = new SpartanWeaponEnchantment();
	public static Enchantment PERPETUAL_STRIKE = new PerpetualStrikeEnchantment();
	public static Enchantment CLIMATE_TRANQUILITY = new EnchantmentClimateTranquility();
	public static Enchantment MOMENTUM = new MomentumEnchantment();
	public static Enchantment ENDER_MENDING = new EnderMendingEnchantment();
	public static Enchantment SMART_ASS = new SmartAssEnchantment();
	
	//Unique
	public static Enchantment WARRIORS_GRACE = new EnchantmentWarriorsGrace();
	public static Enchantment ENDERMARKSMEN = new EnchantmentEnderMarksmen();
	public static Enchantment ARES_BLESSING = new EnchantmentAresBlessing();
	public static Enchantment ALCHEMISTS_GRACE = new EnchantmentAlchemistsGrace();
	public static Enchantment CLOUD_WALKER = new EnchantmentCloudwalker();
	public static Enchantment FAST_FOOD = new EnchantmentFastFood();
	public static Enchantment NATURES_GRACE = new EnchantmentNaturesGrace();
	public static Enchantment ECOLOGICAL = new EnchantmentEcological();
	public static Enchantment PHOENIX_BLESSING = new EnchantmentPhoenixBlessing();
	public static Enchantment MIDAS_BLESSING = new EnchantmentMidasBlessing();
	public static Enchantment IFRIDS_GRACE = new EnchantmentIfritsGrace();
	public static Enchantment ICARUS_AEGIS = new EnchantmentIcarusAegis();
	public static Enchantment ENDER_LIBRARIAN = new EnchantmentEnderLibrarian();
	public static Enchantment DEMETERS_SOUL = new EnchantmentDemetersSoul();
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM = new EnchantmentPestilencesOdium();
	public static Enchantment DEATHS_ODIUM = new EnchantmentDeathsOdium();
	
	//Potions
	public static Effect PESTILENCES_ODIUM_POTION = new PotionPestilencesOdium();
	
	/**
	 * TODO's
	 * FIX: EndermenAI for the EnderEyes Enchantments
	 */
	
	public UniqueEnchantments()
	{
	}
}
