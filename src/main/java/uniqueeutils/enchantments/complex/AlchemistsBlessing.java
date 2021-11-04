package uniqueeutils.enchantments.complex;

import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AlchemistsBlessing extends UniqueEnchantment
{
	public static final String STORED_REDSTONE = "conversion_buffer";
	public static final Map<Item, ConversionEntry> RECIPES = new Object2ObjectOpenHashMap<>();
	public static final DoubleStat CONSUMTION = new DoubleStat(1D, "consumption");
	public static final DoubleStat BASE_XP_USAGE = new DoubleStat(1D, "base_xp_usage");
	public static final DoubleStat LVL_XP_USAGE = new DoubleStat(1D, "lvl_xp_usage");
	public static final ToIntFunction<ItemStack> REDSTONE = new ToIntFunction<ItemStack>()
	{
		@Override
		public int applyAsInt(ItemStack value)
		{
			if(value.getItem() == Items.REDSTONE) return 1;
			if(value.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) return 10;
			return 0;
		}
	};
	
	public AlchemistsBlessing()
	{
		super(new DefaultData("alchemists_blessing", Rarity.VERY_RARE, 4, true, 26, 12, 10), EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(CONSUMTION, BASE_XP_USAGE, LVL_XP_USAGE);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING);
		addIncompats(new ResourceLocation("uniquee", "alchemistsgrace"), new ResourceLocation("uniquee", "ender_mending"));
	}
	
	@Override
	public void loadData(Configuration config)
	{
		RECIPES.clear();
		String[] recipes = config.getStringList("conversionMap", getConfigName(), new String[]{"minecraft:iron_ingot, minecraft:gold_nugget, 1", "minecraft:sugar, minecraft:gunpowder, 1", "minecraft:wheat, minecraft:string, 2"}, "Conversion map that decides which Items convert into what. Format: \"InputItem, OutputItem, OutputAmount\"");
		for(int i = 0;i<recipes.length;i++)
		{
			try
			{
				ConversionEntry entry = new ConversionEntry(recipes[i].split(", "));
				if(!entry.isValid()) continue;
				RECIPES.put(entry.input, entry);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static class ConversionEntry
	{
		Item input;
		Item output;
		int amount;
		
		public ConversionEntry(String[] data)
		{
			input = Item.getByNameOrId(data[0]);
			output = Item.getByNameOrId(data[1]);
			amount = Integer.parseInt(data[2]);
		}
		
		public boolean isValid()
		{
			return input != Items.AIR && output != Items.AIR && amount > 0;
		}
		
		public Item getInput()
		{
			return input;
		}
		
		public ItemStack generateOutput(Random rand, int min, int extra)
		{
			return new ItemStack(output, Math.max(rand.nextInt(Math.max(1, amount + extra)), min));
		}
	}
}
