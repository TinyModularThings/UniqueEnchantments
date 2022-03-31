package uniquebase.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import uniquebase.UEBase;

public class NameString {
	
	static Random rand = new Random();
	
	public static final Map<NameEnum, HashSet<NameString>> NAMES = new HashMap<NameEnum, HashSet<NameString>>();
	public static final List<NameString> entries = new ArrayList<>(); 
	
	String name;
	int minLevel;
	int maxLevel;
	Set<ItemType> items;
	
	public NameString(NameEnum part, String name, int minLevel, int maxLevel, Set<ItemType> items) {
		this.name = name;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.items = items;
		fill(part, this);
	}
	
	public NameString(NameEnum part, String name, int minLevel, int maxLevel) {
		this.name = name;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.items = ObjectSets.singleton(ItemType.ALL);
		fill(part, this);
	}
	
	public NameString(NameEnum part, String name) {
		this.name = name;
		this.minLevel = 0;
		this.maxLevel = UniqueRarity.values().length;
		this.items = ObjectSets.singleton(ItemType.ALL);
		fill(part, this);
	}

	public static NameString create(NameEnum part, String name, int minLevel, int maxLevel, Set<ItemType> items) {
		return new NameString(part, name, minLevel, maxLevel, items);
	}
	public static NameString create(NameEnum part, String name, int minLevel, int maxLevel) {
		return new NameString(part, name, minLevel, maxLevel);
	}
	public static NameString create(NameEnum part, String name) {
		return new NameString(part, name);
	}
	
	public static void fill(NameEnum part, NameString name) {
		NAMES.get(part).add(name);
	}
	
	public static void prefillMap() {
		for(NameEnum name : NameEnum.values()) {
			NAMES.putIfAbsent(name, new HashSet<NameString>());
		}
	}
	
	public static void fillMap(ConfigValue<List<? extends String>> values, NameEnum part) {
		if(NAMES.isEmpty()) {
			prefillMap();
		}
		for (String entry : values.get()) {
			String[] value = entry.split(";");
			if(value.length == 4) {
				Set<ItemType> list = new HashSet<>();
				for(String tool : value[3].split(",")) {
					list.add(ItemType.byId(tool));
				}
				create(part, value[0], Integer.decode(value[1]), Integer.decode(value[2]), list);
			}
			else {
				UEBase.LOGGER.info("Issue at " + entry);
			}
		}
	}
	
	public static void printer() {
		for(NameEnum partt:NameString.NAMES.keySet()) {
			UEBase.LOGGER.info(partt.name().toString());
			for(NameString name : NameString.NAMES.get(partt)) {
				UEBase.LOGGER.info(name.name.toString());
			}
		}
	}
	
	private static Set<NameString> getNameStrings(NameEnum part, UniqueRarity rarity, ItemType type) {
		Set<NameString> result = new HashSet<>();
		for(NameString name : NAMES.get(part)) {
			if(name.items.contains(type) && name.minLevel < rarity.index && name.maxLevel > rarity.index) {
					result.add(name);
			}
		}
		return result;
	}
	
	private static Set<NameString> getNameStrings(NameEnum part, UniqueRarity rarity, Iterable<ItemType> types) {
		Set<NameString> result = new HashSet<>();
		for(ItemType type : types) {
			for(NameString name : NAMES.get(part)) {
				if(name.items.contains(type) && name.minLevel <= rarity.index && name.maxLevel >= rarity.index) {
					result.add(name);
				}
			}
		}
		return result;
	}
	
	public static String getName(NameEnum part, UniqueRarity rarity, ItemType types) {
		Set<NameString> sns = getNameStrings(part, rarity, types);
		NameString[] names = sns.toArray(new NameString[sns.size()]);
		return names.length > 0 ? names[rand.nextInt(names.length)].name + " " : "";
	}
	
	public static String getName(NameEnum part, UniqueRarity rarity, Iterable<ItemType> types) {
		Set<NameString> sns = getNameStrings(part, rarity, types);
		NameString[] names = sns.toArray(new NameString[sns.size()]);
		return names.length > 0 ? names[rand.nextInt(names.length)].name + " " : "";
	}
	
	protected boolean isValid(ItemStack item) {
		for(ItemType type : items) {
			if(type.isValid(item)) return true;
		}
		return false;
	}
	
}
