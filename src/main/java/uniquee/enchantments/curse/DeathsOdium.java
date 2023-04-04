package uniquee.enchantments.curse;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.SlotUUID;
import uniquebase.utils.StackUtils;
import uniquee.UE;

public class DeathsOdium extends UniqueEnchantment
{
	public static final String CURSE_DISABLED = "curse_disabled";
	public static final String CURSE_STORAGE = "curse_storage";
	public static final String CURSE_COUNTER = "curse_counter";
	public static final String CURSE_TIMER = "curse_regain_timer";
	public static final String CURSE_DAMAGE = "curse_regain_damage";
	public static final String CURSE_RESET = "curse_reset";
	public static final UUID REMOVE_UUID = UUID.fromString("1a74e7ff-3914-4e57-8f59-aed5c17c04a0");
	public static final SlotUUID GENERAL_MOD = new SlotUUID(
			"fbd9ead3-fdd8-45c8-a029-644b9a5c72cf", "6d64591f-abda-431f-87da-0622ba33b665", "7155868f-c452-482b-9c35-20d3082cf766",
			"b84f1f28-af51-41b3-a820-5b0d5943deb2", "7feca783-39ef-474a-829f-27b004847d8f", "ab4e455c-9fd1-4601-8da0-d23ffc212815");
	
	public static final IntStat DELAY = new IntStat(200, "collector_time");
	public static final IntStat DAMAGE_FACTOR = new IntStat(30, "damage_factor");
	public static final DoubleStat BASE_LOSS = new DoubleStat(1, "base_loss");
	
	public DeathsOdium()
	{
		super(new DefaultData("deaths_odium", Rarity.UNCOMMON, 2, false, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		addStats(DELAY, DAMAGE_FACTOR, BASE_LOSS);
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() == Items.COOKIE;
	}
	
	public static boolean applyStackBonus(LivingEntity entity)
	{
		int maxLevel = MiscUtil.getCombinedEnchantmentLevel(UE.DEATHS_ODIUM, entity);
		if(maxLevel > 0)
		{
			int lowest = Integer.MAX_VALUE;
			EquipmentSlot lowestSlot = null;
			int max = maxLevel+10;
			for(EquipmentSlot slot : EquipmentSlot.values())
			{
				ItemStack stack = entity.getItemBySlot(slot);
				if(MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, stack) > 0)
				{
					int value = StackUtils.getInt(stack, DeathsOdium.CURSE_COUNTER, 0);
					int newValue = Math.min(value + 1, max);
					if(value == newValue) continue;
					if(lowest > value)
					{
						lowest = value;
						lowestSlot = slot;
					}
				}
			}
			if(lowestSlot != null) {
				ItemStack stack = entity.getItemBySlot(lowestSlot);
				StackUtils.setInt(stack, DeathsOdium.CURSE_COUNTER, lowest+1);
				StackUtils.setFloat(stack, DeathsOdium.CURSE_STORAGE, StackUtils.getFloat(stack, DeathsOdium.CURSE_STORAGE, 0F) + ((float)Math.sqrt(entity.getMaxHealth()) * 0.3F * entity.getRandom().nextFloat()));
			}
			return true;
		}
		return false;
	}
}