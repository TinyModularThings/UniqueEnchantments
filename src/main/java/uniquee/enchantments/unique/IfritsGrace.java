package uniquee.enchantments.unique;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class IfritsGrace extends UniqueEnchantment
{
	public static final Object2IntMap<Item> LAVA_ITEMS = new Object2IntOpenHashMap<Item>();
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return LAVA_ITEMS.getInt(value.getItem());
		}
	};
	public static String LAVA_COUNT = "lava_storage";
	public static final DoubleStat BASE_CONSUMTION = new DoubleStat(8, "base_consumtion");
	static ConfigValue<List<? extends String>> ITEMS;

	public IfritsGrace()
	{
		super(new DefaultData("ifrits_grace", Rarity.RARE, 3, true, true, 14, 4, 40), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(BASE_CONSUMTION);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.BLOCK_FORTUNE, Enchantments.SILK_TOUCH);
	}
	
	@Override
	public void loadData(Builder config)
	{
		LAVA_ITEMS.put(Items.LAVA_BUCKET, 250);
		LAVA_ITEMS.put(Items.MAGMA_CREAM, 20);
		ITEMS = config.defineList("lava_items", ObjectArrayList.wrap(new String[]{ForgeRegistries.ITEMS.getKey(Items.LAVA_BUCKET).toString()+";"+250, ForgeRegistries.ITEMS.getKey(Items.MAGMA_CREAM).toString()+";"+20}), (T) -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		LAVA_ITEMS.clear();
		LAVA_ITEMS.put(Items.LAVA_BUCKET, 250);
		LAVA_ITEMS.put(Items.MAGMA_CREAM, 20);
		List<? extends String> items = ITEMS.get();
		for(int i = 0,m=items.size();i<m;i++)
		{
			String[] values = items.get(i).split(";");
			if(values.length != 2)
			{
				continue;
			}
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(values[0]));
			if(item == null || item == Items.AIR)
			{
				continue;
			}
			try
			{
				LAVA_ITEMS.putIfAbsent(item, Integer.parseInt(values[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
