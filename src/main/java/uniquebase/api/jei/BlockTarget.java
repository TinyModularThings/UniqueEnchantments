package uniquebase.api.jei;

import java.util.List;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockTarget extends EnchantmentTarget
{
	Predicate<BlockState> filter;
	
	public BlockTarget(Component description, Enchantment ench, Predicate<BlockState> filter)
	{
		super(description, ench);
		this.filter = filter;
	}
	
	@Override
	public List<ItemStack> getItems(List<ItemStack> itemPool)
	{
		List<ItemStack> result = new ObjectArrayList<>();
		for(Block block : ForgeRegistries.BLOCKS) {
			if(filter.test(block.defaultBlockState())) {
				try {
					Item item = block.asItem();
					if(item == null || item == Items.AIR) continue;
					result.add(new ItemStack(item));
				}
				catch(Exception e) {}
			}
		}
		return result;
	}
	
}
