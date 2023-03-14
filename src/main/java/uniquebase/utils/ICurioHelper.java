package uniquebase.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface ICurioHelper
{
	public Object2IntMap.Entry<ItemStack> findEnchantment(LivingEntity entity, Enchantment ench, String identifier);
	
	public static ICurioHelper dummy() {
		return DummyHelper.DUMMY;
	}
	
	public static class DummyHelper implements ICurioHelper
	{
		private static final ICurioHelper DUMMY = new DummyHelper();
		
		@Override
		public Object2IntMap.Entry<ItemStack> findEnchantment(LivingEntity entity, Enchantment ench, String identifier)
		{
			return MiscUtil.NO_ENCH_FOUND;
		}
		
	}
	
	public static enum CurioSlot
	{
		BACK("back", EquipmentSlot.CHEST),
		HAND("charm", EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		
		EquipmentSlot[] slot;
		String curio;
		
		private CurioSlot(String curio, EquipmentSlot... slot)
		{
			this.slot = slot;
			this.curio = curio;
		}
		
		public String getCurio()
		{
			return curio;
		}
		
		public EquipmentSlot[] getSlot()
		{
			return slot;
		}
	}
}
