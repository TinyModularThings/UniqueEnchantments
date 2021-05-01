package uniqueeutils.enchantments;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntStat;

public class PoseidonsSoulEnchantment extends UniqueEnchantment
{
	public static final IOptionalNamedTag<Block> POSEIDONS_SOUL = BlockTags.createOptional(new ResourceLocation("uniquee", "poseidons_soul"));
	public static final IntStat BASE_CONSUMTION = new IntStat(16, "base_consumtion");
	
	public PoseidonsSoulEnchantment()
	{
		super(new DefaultData("poseidons_soul", Rarity.RARE, 3, true, 24, 4, 75), EnchantmentType.TRIDENT, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
		setCategory("utils");
	}

	@Override
	public void loadData(Builder config)
	{
		BASE_CONSUMTION.handleConfig(config);
	}
	
	public static boolean isValid(Block block)
	{
		return POSEIDONS_SOUL.isDefaulted() ? BlockTags.CORAL_BLOCKS.contains(block) || block == Blocks.LILY_PAD || block == Blocks.SEA_PICKLE : POSEIDONS_SOUL.contains(block);
	}
}
