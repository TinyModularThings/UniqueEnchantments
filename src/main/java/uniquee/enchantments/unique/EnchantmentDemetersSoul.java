package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.HarvestEntry;

public class EnchantmentDemetersSoul extends UniqueEnchantment
{
	public static final String ID = "crop_queue";
	public static final String QUEUE_INDEX = "index_queue";
	
	public EnchantmentDemetersSoul()
	{
		super(new DefaultData("demeters_soul", Rarity.VERY_RARE, true, 20, 6, 40), EnchantmentType.BREAKABLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}

	@Override
	public void loadData(Builder config) {}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof HoeItem;
	}
	
	public static HarvestEntry getNextIndex(PlayerEntity player)
	{
		CompoundNBT entityData = player.getPersistentData();
		CompoundNBT persistent = entityData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
		ListNBT list = persistent.getList(ID, 10);
		if(list.isEmpty())
		{
			return null;
		}
		int index = (persistent.getInt(QUEUE_INDEX) + 1) % list.size();
		persistent.putInt(QUEUE_INDEX, index);
		return new HarvestEntry(list.getCompound(index));
	}
	
	public static ListNBT getCrops(PlayerEntity player)
	{
		CompoundNBT entityData = player.getPersistentData();
		CompoundNBT persistent = entityData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
		entityData.put(PlayerEntity.PERSISTED_NBT_TAG, persistent);
		ListNBT list = persistent.getList(ID, 10);
		persistent.put(ID, list);
		return list;
	}
}
