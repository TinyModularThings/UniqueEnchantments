package uniqueeutils.enchantments.unique;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntStat;

public class PoseidonsSoul extends UniqueEnchantment
{
	public static final Tag<Block> POSEIDONS_SOUL = new BlockTags.Wrapper(new ResourceLocation("uniquee", "poseidons_soul"));
	public static final IntStat BASE_CONSUMTION = new IntStat(16, "base_consumtion");
	
	public PoseidonsSoul()
	{
		super(new DefaultData("poseidons_soul", Rarity.RARE, 3, true, 24, 4, 75), EnchantmentType.TRIDENT, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(BASE_CONSUMTION);
		setCategory("utils");
	}
	
	public static boolean isValid(Block block)
	{
		return POSEIDONS_SOUL.getAllElements().isEmpty() ? BlockTags.CORAL_BLOCKS.contains(block) || block == Blocks.LILY_PAD || block == Blocks.SEA_PICKLE : POSEIDONS_SOUL.contains(block);
	}
}
