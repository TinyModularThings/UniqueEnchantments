package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IdStat;

public class EndestReapEnchantment extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static DoubleStat BONUS_DAMAGE_LEVEL = new DoubleStat(0.7D, "bonus_damage_level");
	public static DoubleStat REAP_MULTIPLIER = new DoubleStat(0.005D, "reap_multiplier");
	public static IdStat VALID_ENTITIES = new IdStat("valid_entities", ForgeRegistries.ENTITIES, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.SHULKER, EntityType.ELDER_GUARDIAN, EntityType.RAVAGER, EntityType.EVOKER);

	public EndestReapEnchantment()
	{
		super(new DefaultData("endest_reap", Rarity.RARE, 4, true, 30, 6, 20), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}

	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}

	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		BONUS_DAMAGE_LEVEL.handleConfig(config);
		REAP_MULTIPLIER.handleConfig(config);
		VALID_ENTITIES.handleConfig(config);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		VALID_ENTITIES.onConfigChanged();
	}
}