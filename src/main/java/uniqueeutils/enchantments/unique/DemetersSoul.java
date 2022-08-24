package uniqueeutils.enchantments.unique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.HarvestEntry;
import uniquebase.utils.IntStat;

public class DemetersSoul extends UniqueEnchantment
{
	public static final String ID = "crop_queue";
	public static final String QUEUE_INDEX = "index_queue";
	public static final IntStat DELAY = new IntStat(20, "delay");
	public static final DoubleStat SCALING = new DoubleStat(0.9D, "base_cap");
	public static final IntStat CAP = new IntStat(25, "crop_cap");
	
	public DemetersSoul()
	{
		super(new DefaultData("demeters_soul", Rarity.VERY_RARE, 3, true, false, 20, 10, 40), EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(DELAY, SCALING, CAP);
	}
	
	@Override
	public void loadData(Builder config) {}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
	}
	
	public static HarvestEntry getNextIndex(Player player)
	{
		CompoundTag entityData = player.getPersistentData();
		CompoundTag persistent = entityData.getCompound(Player.PERSISTED_NBT_TAG);
		ListTag list = persistent.getList(ID, 10);
		if(list.isEmpty())
		{
			return null;
		}
		int index = (persistent.getInt(QUEUE_INDEX) + 1) % list.size();
		persistent.putInt(QUEUE_INDEX, index);
		return new HarvestEntry(list.getCompound(index));
	}
	
	public static ListTag getCrops(Player player)
	{
		CompoundTag entityData = player.getPersistentData();
		CompoundTag persistent = entityData.getCompound(Player.PERSISTED_NBT_TAG);
		entityData.put(Player.PERSISTED_NBT_TAG, persistent);
		ListTag list = persistent.getList(ID, 10);
		persistent.put(ID, list);
		return list;
	}
}
