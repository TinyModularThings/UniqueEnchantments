package uniqueeutils.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.HarvestEntry;
import uniquebase.utils.IntLevelStats;
import uniquebase.utils.IntStat;

public class DemetersSoul extends UniqueEnchantment
{
	public static final String ID = "crop_queue";
	public static final String QUEUE_INDEX = "index_queue";
	public static final IntStat DELAY = new IntStat(20, "delay");
	public static final DoubleStat SCALING = new DoubleStat(0.9D, "base_cap");
	public static final IntLevelStats CAP = new IntLevelStats("crop_cap", 25, 10);
	
	public DemetersSoul()
	{
		super(new DefaultData("demeters_soul", Rarity.VERY_RARE, 3, true, 20, 10, 40), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		setCategory("utils");
		addStats(DELAY, SCALING, CAP);
	}
		
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemHoe;
	}
	
	public static HarvestEntry getNextIndex(EntityPlayer player)
	{
		NBTTagCompound entityData = player.getEntityData();
		NBTTagCompound persistent = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		NBTTagList list = persistent.getTagList(ID, 10);
		if(list.isEmpty())
		{
			return null;
		}
		int index = (persistent.getInteger(QUEUE_INDEX) + 1) % list.tagCount();
		persistent.setInteger(QUEUE_INDEX, index);
		return new HarvestEntry(list.getCompoundTagAt(index));
	}
	
	public static NBTTagList getCrops(EntityPlayer player)
	{
		NBTTagCompound entityData = player.getEntityData();
		NBTTagCompound persistent = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistent);
		NBTTagList list = persistent.getTagList(ID, 10);
		persistent.setTag(ID, list);
		return list;
	}
}