package uniqueeutils.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
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
		super(new DefaultData("demeters_soul", Rarity.VERY_RARE, 3, true, 20, 10, 40), EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(DELAY, SCALING, CAP);
	}
	
	@Override
	public void loadData(Builder config) {}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
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
