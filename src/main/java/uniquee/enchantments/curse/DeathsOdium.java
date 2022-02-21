package uniquee.enchantments.curse;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.SlotUUID;

public class DeathsOdium extends UniqueEnchantment
{
	public static final String CURSE_STORAGE = "curse_storage";
	public static final String CURSE_TIMER = "curse_regain_timer";
	public static final String CURSE_DAMAGE = "curse_regain_damage";
	public static final String CURSE_RESET = "curse_reset";
	public static final UUID REMOVE_UUID = UUID.fromString("1a74e7ff-3914-4e57-8f59-aed5c17c04a0");
	public static final SlotUUID GENERAL_MOD = new SlotUUID(
			"fbd9ead3-fdd8-45c8-a029-644b9a5c72cf", "6d64591f-abda-431f-87da-0622ba33b665", "7155868f-c452-482b-9c35-20d3082cf766",
			"b84f1f28-af51-41b3-a820-5b0d5943deb2", "7feca783-39ef-474a-829f-27b004847d8f", "ab4e455c-9fd1-4601-8da0-d23ffc212815");
	
	public static final IntStat DELAY = new IntStat(200, "collector_time");
	public static final IntStat MAX_STORAGE = new IntStat(1, "curse_storage_cap");
	public static final IntStat DAMAGE_FACTOR = new IntStat(30, "damage_factor");
	public static final DoubleStat BASE_LOSS = new DoubleStat(1, "base_loss");
	
	public DeathsOdium()
	{
		super(new DefaultData("deaths_odium", Rarity.UNCOMMON, 2, false, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		addStats(DELAY, MAX_STORAGE, DAMAGE_FACTOR, BASE_LOSS);
		setCurse();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() == Items.COOKIE;
	}
	
}
