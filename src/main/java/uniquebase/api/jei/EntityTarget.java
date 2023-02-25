package uniquebase.api.jei;

import java.util.List;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.utils.IdStat;

public class EntityTarget extends EnchantmentTarget
{
	Predicate<EntityType<?>> filter;
	
	public EntityTarget(Component description, Enchantment ench, IdStat<EntityType<?>> filter)
	{
		this(description, ench, filter::contains);
	}
	
	public EntityTarget(Component description, Enchantment ench, Predicate<EntityType<?>> filter)
	{
		super(description, ench);
		this.filter = filter;
	}

	@Override
	public List<ItemStack> getItems(List<ItemStack> itemPool)
	{
		List<ItemStack> result = new ObjectArrayList<>();
		for(EntityType<?> type : ForgeRegistries.ENTITY_TYPES)
		{
			if(filter.test(type)) result.add(new ItemStack(ForgeSpawnEggItem.fromEntityType(type)));
		}
		return result;
	}
}
