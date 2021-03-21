package uniquee.enchantments.unique;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.Tags;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.IntStat;

public class MidasBlessingEnchantment extends UniqueEnchantment implements IBlessingEnchantment
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
	public static IntStat LEVEL_SCALAR = new IntStat(6, "level_scalar");
	public static IntStat BASE_COST = new IntStat(2, "base_cost");
	
	public MidasBlessingEnchantment()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, 3, true, 22, 2, 75), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof LootBonusEnchantment || ench instanceof SilkTouchEnchantment || ench instanceof IfritsGraceEnchantment ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		LEVEL_SCALAR.handleConfig(config);
		BASE_COST.handleConfig(config);
	}
	
	
}
