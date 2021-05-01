package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;

public class EcologicalEnchantment extends UniqueEnchantment
{
	public static final INamedTag<Block> ECHOLOGICAL = BlockTags.createOptional(new ResourceLocation("uniquee", "ecological"));
	public static Predicate<BlockState> STATES = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return BlockTags.LOGS.contains(t.getBlock()) || BlockTags.LEAVES.contains(t.getBlock()) || ECHOLOGICAL.contains(t.getBlock());
		}
	};
	public static IntStat SPEED = new IntStat(220, "speed");
	public static DoubleStat SCALE = new DoubleStat(1.85D, "scale");
	
	public EcologicalEnchantment()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, true, 22, 2, 40), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ENDERMARKSMEN, Enchantments.MENDING, Enchantments.INFINITY);
	}

	@Override
	public void loadData(Builder config)
	{
		SPEED.handleConfig(config);
		SCALE.handleConfig(config);
	}
	
	
}
