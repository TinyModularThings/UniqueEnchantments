package uniquebase.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.utils.MiscUtil;

public abstract class EnchantedUpgrade
{
	private static final ObjectList<EnchantedUpgrade> ALL_EFFECTS = ObjectLists.synchronize(new ObjectArrayList<>());
	private static final Map<Enchantment, EnchantedUpgrade> BY_ENCHANTMENT = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
	
	String tag;
	String name;
	Enchantment ench;
	Supplier<Enchantment> enchSupplier;
	EnumSet<EquipmentSlot> validSlots = EnumSet.of(EquipmentSlot.MAINHAND);
	
	public EnchantedUpgrade(String mod, String path, String name, Supplier<Enchantment> enchSupplier)
	{
		this(new ResourceLocation(mod, path), name, enchSupplier);
	}
	
	public EnchantedUpgrade(ResourceLocation tag, String name, Supplier<Enchantment> enchSupplier)
	{
		this.tag = tag.toString();
		this.enchSupplier = enchSupplier;
		this.name = name;
		ALL_EFFECTS.add(this);
	}
	
	public static List<EnchantedUpgrade> getAllUpgrades()
	{
		return ObjectLists.unmodifiable(ALL_EFFECTS);
	}
	
	protected void setEquimentSlots(EnumSet<EquipmentSlot> validSlots)
	{
		this.validSlots = validSlots;
	}
	
	public void register()
	{
		Enchantment ench = getSource();
		if(ench == null) throw new IllegalStateException();
		if(BY_ENCHANTMENT.put(ench, this) != null) throw new IllegalStateException("Enchantment"+ForgeRegistries.ENCHANTMENTS.getKey(ench)+" already has a buff. Tag Name["+tag+"]");
	}
	
	public String getName()
	{
		return name;
	}
	
	public Enchantment getSource()
	{
		if(ench == null) ench = enchSupplier.get();
		return ench;
	}
	
	public abstract boolean isValid(ItemStack stack);
	protected abstract double getFormular(int inputPoints);
	
	public void addToolTip(List<Component> result, ItemStack input)
	{
		int points = getPoints(input);
		if(points > 0) result.add(Component.translatable(name).withStyle(ChatFormatting.GOLD).append(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getFormular(points))).append(" ["+points+"]"));
	}
	
	public boolean isValidSlot(EquipmentSlot slot)
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