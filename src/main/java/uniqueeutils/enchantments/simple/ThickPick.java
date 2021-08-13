package uniqueeutils.enchantments.simple;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class ThickPick extends UniqueEnchantment
{
	public static final String TAG = "thick_pick";
	public static final DoubleLevelStats MINING_SPEED = new DoubleLevelStats("speed", 2.25D, 1.25D);
	static ConfigValue<List<? extends String>> ITEMS_CONFIG;
	static final Object2IntMap<Item> ITEMS = new Object2IntOpenHashMap<Item>();
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return ITEMS.getInt(value.getItem());
		}
	};
	
	public ThickPick()
	{
		super(new DefaultData("thick_pick", Rarity.RARE, 2, false, 26, 4, 75), EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		setCategory("utils");
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		MINING_SPEED.handleConfig(config);
		ITEMS_CONFIG = config.defineList("items", ObjectArrayList.wrap(new String[]{Items.DIAMOND.getRegistryName().toString()+";"+5}), T->true);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		ITEMS.clear();
		ITEMS.put(Items.DIAMOND, 8);
		ITEMS.put(Items.EMERALD, 3);
		ITEMS.put(Items.QUARTZ, 1);
		List<? extends String> items = ITEMS_CONFIG.get();
		for(int i = 0,m=items.size();i<m;i++)
		{
			String[] values = items.get(i).split(";");
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(values[0]));
			if(item != null && item != Items.AIR)
			{
				try
				{
					ITEMS.putIfAbsent(item, Integer.parseInt(values[1]));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}