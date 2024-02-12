package uniqueeutils.enchantments.simple;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;

public class Dreams extends UniqueEnchantment 
{
	public static final DoubleStat DURABILITY_FACTOR = new DoubleStat(1.0D, "durability_factor");
	public static final IdStat<EntityType<?>> VALID_TARGETS = new IdStat<>("valid_targets", ForgeRegistries.ENTITY_TYPES, EntityType.PHANTOM);
	
	public Dreams()
	{
		super(new DefaultData("dreams", Rarity.VERY_RARE, 3, false, false, 20, 5, 25), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		addStats(DURABILITY_FACTOR, VALID_TARGETS);
		setCategory("utils");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}
