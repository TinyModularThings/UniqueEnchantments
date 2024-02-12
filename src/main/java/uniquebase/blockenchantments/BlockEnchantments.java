package uniquebase.blockenchantments;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockEnchantments {
	Object2IntMap<Enchantment> enchantments = new Object2IntLinkedOpenHashMap<>();
	
	public boolean addEnchantment(Enchantment ench, int level) {
		enchantments.put(ench, level);
		return true;
	}
	
	public boolean containsEnchantment(Enchantment ench) {
		return enchantments.containsKey(ench);
	}
	
	public int getEnchantmentLevel(Enchantment ench) {
		return enchantments.getInt(ench);
	}
	
	public int removeEnchantment(Enchantment ench) {
		return enchantments.removeInt(ench);
	}
	
	public Object2IntMap<Enchantment> getAllEnchantments() {
		return Object2IntMaps.unmodifiable(enchantments);
	}
	
	public void clearEnchantments() {
		enchantments.clear();
	}
	
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for(Entry<Enchantment> entry : enchantments.object2IntEntrySet()) {
			list.add(EnchantmentHelper.storeEnchantment(ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey()), entry.getIntValue()));
		}
		tag.put("Enchantments", list);
		return tag;
	}
	
	public void deserialize(CompoundTag tag) {
		enchantments.clear();
		ListTag list = tag.getList("Enchantments", 10);
		for(int i = 0,m=list.size();i<m;i++) {
			CompoundTag entry = list.getCompound(i);
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(entry));
			if(ench == null) continue;
			addEnchantment(ench, EnchantmentHelper.getEnchantmentLevel(entry));
		}
	}
}
