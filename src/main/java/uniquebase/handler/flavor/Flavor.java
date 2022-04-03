package uniquebase.handler.flavor;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import uniquebase.UEBase;

public class Flavor 
{
	private static final Map<FlavorTarget, List<Flavor>> FLAVORS = new EnumMap<>(FlavorTarget.class);
	
	String name;
	int minLevel;
	int maxLevel;
	Set<ItemType> items;
	
	public Flavor(FlavorTarget part, String name, int minLevel, int maxLevel, Set<ItemType> items) {
		this.name = name;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.items = items;
	}
	
	public boolean isValid(ItemStack item) {
		for(ItemType type : items) {
			if(type.isValid(item)) return true;
		}
		return false;
	}
	
	public boolean containsType(Collection<ItemType> types) {
		for(ItemType type : types)
		{
			if(items.contains(type)) return true;
		}
		return false;
	}
	
	public String getUnlocalizedName()
	{
		return name;
	}
	
	public IFormattableTextComponent getName()
	{
		return new TranslationTextComponent(name);
	}
	
	public int getMinLevel()
	{
		return minLevel;
	}
	
	public int getMaxLevel()
	{
		return maxLevel;
	}
		
	public static void fillMap(ConfigValue<List<? extends String>> values, FlavorTarget target) {
		List<Flavor> result = FLAVORS.compute(target, (T, O) -> new ObjectArrayList<>());
		for (String entry : values.get()) {
			String[] value = entry.split(";");
			if(value.length != 4) {
				UEBase.LOGGER.info("Issue at " + entry);
				continue;
			}
			Set<ItemType> list = new ObjectOpenHashSet<>();
			for(String tool : value[3].split(",")) {
				list.add(ItemType.byId(tool));
			}
			result.add(new Flavor(target, value[0], Integer.decode(value[1]), Integer.decode(value[2]), list));
		}
	}
	
	public static void printer() {
		for(FlavorTarget partt:Flavor.FLAVORS.keySet()) {
			UEBase.LOGGER.info(partt.name().toString());
			for(Flavor name : Flavor.FLAVORS.get(partt)) {
				UEBase.LOGGER.info(name.name.toString());
			}
		}
	}
	
	private static List<Flavor> getFlavors(FlavorTarget target, FlavorRarity rarity, ItemType type) {
		List<Flavor> result = new ObjectArrayList<>();
		for(Flavor name : FLAVORS.get(target)) {
			if(name.minLevel <= rarity.index && name.maxLevel >= rarity.index && name.items.contains(type)) result.add(name);
		}
		return result;
	}
	
	private static List<Flavor> getFlavors(FlavorTarget target, FlavorRarity rarity, Collection<ItemType> types) {
		List<Flavor> result = new ObjectArrayList<>();
		for(Flavor name : FLAVORS.get(target)) {
			if(name.minLevel <= rarity.index && name.maxLevel >= rarity.index && name.containsType(types)) result.add(name);
		}
		return result;
	}
	
	public static ITextComponent getFlavor(FlavorTarget target, FlavorRarity rarity, ItemType types, Random rand) {
		List<Flavor> values = getFlavors(target, rarity, types);
		return values.size() > 0 ? values.get(rand.nextInt(values.size())).getName().append(" ") : StringTextComponent.EMPTY;
	}
	
	public static ITextComponent getFlavor(FlavorTarget target, FlavorRarity rarity, Collection<ItemType> types, Random rand) {
		List<Flavor> values = getFlavors(target, rarity, types);
		return values.size() > 0 ? values.get(rand.nextInt(values.size())).getName().append(" ") : StringTextComponent.EMPTY;
	}
}
