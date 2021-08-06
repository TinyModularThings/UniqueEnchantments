package uniqueeutils.enchantments.simple;

import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class ThickPick extends UniqueEnchantment
{
	public static final String TAG = "thick_pick";
	public static final DoubleLevelStats MINING_SPEED = new DoubleLevelStats("speed", 2.25D, 1.25D);
	static final Object2IntMap<Item> ITEMS = new Object2IntOpenHashMap<Item>();
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return ITEMS.getInt(value.getItem());
		}
	};
	
	public ThickPick()
	{
		super(new DefaultData("thick_pick", Rarity.RARE, 2, false, 26, 4, 75), EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setCategory("utils");
		addStats(MINING_SPEED);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		ITEMS.clear();
		ITEMS.put(Items.DIAMOND, 8);
		ITEMS.put(Items.EMERALD, 3);
		ITEMS.put(Items.QUARTZ, 1);
		String[] items = config.get(getConfigName(), "items", new String[]{Items.DIAMOND.getRegistryName().toString()+";"+5}).getStringList();
		for(int i = 0,m=items.length;i<m;i++)
		{
			String[] values = items[i].split(";");
			Item item = Item.getByNameOrId(values[0]);
			if(item == null || item == Items.AIR)
			{
				continue;
			}
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
