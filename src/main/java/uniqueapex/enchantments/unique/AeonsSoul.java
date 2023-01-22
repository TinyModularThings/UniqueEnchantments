package uniqueapex.enchantments.unique;

import java.util.Comparator;

import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.ChunkPos;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.IntStat;

public class AeonsSoul extends ApexEnchantment
{
	public static final TicketType<ChunkPos> CUSTOM_TICKET = TicketType.create("aeons_soul", Comparator.comparingLong(ChunkPos::toLong), 24500);
	public static final String TICKET_STORAGE = "aeons_soul";
	public static final IntStat FACTOR = new IntStat(300, "xp_factor");
	
	public AeonsSoul()
	{
		super("aeons_soul", EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(FACTOR);
	}
}
