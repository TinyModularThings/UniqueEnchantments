package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquee.UniqueEnchantments;

public class EndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static final DoubleStat BONUS_DAMAGE_LEVEL = new DoubleStat(0.7D, "bonus_damage_level");
	public static final DoubleStat REAP_MULTIPLIER = new DoubleStat(0.005D, "reap_multiplier");
	public static final IdStat VALID_ENTITIES = new IdStat("valid_entities", ForgeRegistries.ENTITIES, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.SHULKER, EntityType.ELDER_GUARDIAN, EntityType.RAVAGER, EntityType.EVOKER);

	public EndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.VERY_RARE, 4, true, 30, 15, 20), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(BONUS_DAMAGE_LEVEL, REAP_MULTIPLIER, VALID_ENTITIES);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		VALID_ENTITIES.onConfigChanged();
	}
}