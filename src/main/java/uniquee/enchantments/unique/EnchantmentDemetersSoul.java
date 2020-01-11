package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.HarvestEntry;

public class EnchantmentDemetersSoul extends UniqueEnchantment
{
	public static final String ID = "crop_queue";
	public static final String QUEUE_INDEX = "index_queue";
	
	public EnchantmentDemetersSoul()
	{
		super(new DefaultData("demeters_soul", Rarity.VERY_RARE, true, 20, 6, 40), EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}

	@Override
	public void loadData(Configuration config)
	{
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
		if(list.hasNoTags())
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
