package uniqueeutils.enchantments.complex;

import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.StackUtils;

public class AlchemistsBlessing extends UniqueEnchantment
{
	public static final String STORED_REDSTONE = "conversion_buffer";
	public static final Map<Item, ConversionEntry> RECIPES = new Object2ObjectOpenHashMap<>();
	public static final DoubleStat CONSUMTION = new DoubleStat(1D, "consumption");
	public static final DoubleStat BASE_XP_USAGE = new DoubleStat(1D, "base_xp_usage");
	public static final DoubleStat LVL_XP_USAGE = new DoubleStat(1D, "lvl_xp_usage");
	static ConfigValue<List<? extends String>> EFFECT_CONFIG;
	public static final ToIntFunction<ItemStack> REDSTONE = new ToIntFunction<ItemStack>()
	{
		@Override
		public int applyAsInt(ItemStack value)
		{
			if(value.getItem() == Items.REDSTONE) return 1;
			if(value.getItem() == Blocks.REDSTONE_BLOCK.asItem()) return 10;
			return 0;
		}
	};
	
	public AlchemistsBlessing()
	{
		super(new DefaultData("alchemists_blessing", Rarity.VERY_RARE, 4, true, false, 26, 12, 10), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(CONSUMTION, BASE_XP_USAGE, LVL_XP_USAGE);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING);
		addIncompats(new ResourceLocation("uniquee", "alchemistsgrace"), new ResourceLocation("uniquee", "ender_mending"));
	}
	
	@Override
	public void loadData(Builder config)
	{
		config.comment("Conversion map that decides which Items convert into what. Format: \"InputItem, OutputItem, OutputAmount\"");
		EFFECT_CONFIG = config.defineList("conversionMap", ObjectArrayList.wrap(new String[]{"minecraft:iron_ingot, minecraft:gold_nugget, 1", "minecraft:sugar, minecraft:gunpowder, 1", "minecraft:wheat, minecraft:string, 2"}), (T) -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		RECIPES.clear();
		List<? extends String> list = EFFECT_CONFIG.get();
		for(int i = 0;i<list.size();i++)
		{
			try
			{
				ConversionEntry entry = new ConversionEntry(list.get(i).split(", "));
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
			input = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(data[0]));
			output = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(data[1]));
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
		
		public void generateOutput(RandomSource rand, int level, int extra, int multiplier, List<ItemStack> result)
		{
			StackUtils.growStack(new ItemStack(output), Math.max(rand.nextInt(amount + 1), 1) * (level + extra) * multiplier, result);
		}
	}
}
