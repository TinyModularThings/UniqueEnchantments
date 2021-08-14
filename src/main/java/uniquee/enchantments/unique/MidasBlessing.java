package uniquee.enchantments.unique;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IBlessingEnchantment;
import uniquebase.utils.DoubleStat;

public class MidasBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return Tags.Items.INGOTS_GOLD.contains(value.getItem()) ? 1 : 0;
		}
	};
	public static final Predicate<BlockState> IS_GEM = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return MIDIAS.contains(t.getBlock()) || Tags.Blocks.ORES_LAPIS.contains(t.getBlock()) || Tags.Blocks.ORES_DIAMOND.contains(t.getBlock()) || Tags.Blocks.ORES_EMERALD.contains(t.getBlock()) || Tags.Blocks.ORES_QUARTZ.contains(t.getBlock());
		}
	};
	public static INamedTag<Block> MIDIAS = BlockTags.createOptional(new ResourceLocation("uniquee", "midias_blessing"));
	public static String GOLD_COUNTER = "gold_storage";
	public static final DoubleStat GOLD_COST = new DoubleStat(1.5D, "gold_cost");
	
	public MidasBlessing()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, 3, true, false, 14, 6, 75), EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND);
		addStats(GOLD_COST);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.FORTUNE, Enchantments.SILK_TOUCH);
	}
}
