package uniquebase.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import uniquebase.utils.MiscUtil;

public abstract class EnchantedUpgrade
{
	private static final List<EnchantedUpgrade> ALL_EFFECTS = ObjectLists.synchronize(new ObjectArrayList<>());
	private static final Map<Enchantment, EnchantedUpgrade> BY_ENCHANTMENT = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
	
	String tag;
	Enchantment ench;
	Supplier<Enchantment> enchSupplier;
	EnumSet<EquipmentSlotType> validSlots = EnumSet.of(EquipmentSlotType.MAINHAND);
	
	public EnchantedUpgrade(String mod, String path, Supplier<Enchantment> enchSupplier)
	{
		this(new ResourceLocation(mod, path), enchSupplier);
	}
	
	public EnchantedUpgrade(ResourceLocation tag, Supplier<Enchantment> enchSupplier)
	{
		this.tag = tag.toString();
		this.enchSupplier = enchSupplier;
		ALL_EFFECTS.add(this);
	}
	
	protected void setEquimentSlots(EnumSet<EquipmentSlotType> validSlots)
	{
		this.validSlots = validSlots;
	}
	
	public void register()
	{
		Enchantment ench = getSource();
		if(ench == null) throw new IllegalStateException();
		if(BY_ENCHANTMENT.put(ench, this) != null) throw new IllegalStateException("Enchantment"+ench.getRegistryName()+" already has a buff. Tag Name["+tag+"]");
	}
	
	public Enchantment getSource()
	{
		if(ench == null) ench = enchSupplier.get();
		return ench;
	}
	
	public abstract boolean isValid(ItemStack stack);
	
	public boolean isValidSlot(EquipmentSlotType slot)
	{
		return validSlots.contains(slot);
	}
	
	public int getPoints(ItemStack stack)
	{
		return MiscUtil.getStoredPoints(stack, tag);
	}
	
	public int getCombinedPoints(LivingEntity entity)
	{
		return MiscUtil.getCombinedPoints(entity, tag);
	}
	
	public void storePoints(ItemStack stack, int amount)
	{
		MiscUtil.storePoints(stack, tag, amount);
	}
}