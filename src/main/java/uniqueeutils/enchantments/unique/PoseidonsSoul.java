package uniqueeutils.enchantments.unique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntStat;

public class PoseidonsSoul extends UniqueEnchantment
{
	public static final TagKey<Block> POSEIDONS_SOUL = BlockTags.create(new ResourceLocation("uniquee", "poseidons_soul"));
	public static final IntStat BASE_CONSUMTION = new IntStat(16, "base_consumtion");
	
	public PoseidonsSoul()
	{
		super(new DefaultData("poseidons_soul", Rarity.RARE, 3, true, false, 24, 4, 75).setHardCap(40), EnchantmentCategory.TRIDENT, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(BASE_CONSUMTION);
		setCategory("utils");
	}
	
	public static boolean isValid(BlockState state)
	{
		return ForgeRegistries.BLOCKS.tags().getTag(POSEIDONS_SOUL).isEmpty() ? state.is(BlockTags.CORAL_BLOCKS) || state.getBlock() == Blocks.LILY_PAD || state.getBlock() == Blocks.SEA_PICKLE : state.is(POSEIDONS_SOUL);
	}
}
