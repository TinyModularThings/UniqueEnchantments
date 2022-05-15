package uniqueebattle.enchantments;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;

public class ArtemisSoul extends UniqueEnchantment
{
	public static final ToIntFunction<ItemStack> VALID_ITEMS = T -> {
		if(T.getItem() == Items.ENDER_PEARL) return 840;
		if(T.getItem() == Items.ENDER_EYE) return 1440;
		return 0;
	};
	public static final String PERSISTEN_SOUL_COUNT = "p_souls";
	public static final String TEMPORARY_SOUL_COUNT = "t_souls";
	public static final String TIME_STAMP = "t_soul_date";
	public static final String ENDER_STORAGE = "ender_storage";
	private static final IdStat VALID_MOBS = new IdStat("valid_mobs", ForgeRegistries.ENTITIES, new ResourceLocation("minecraft:ender_dragon"), new ResourceLocation("minecraft:wither"), new ResourceLocation("minecraft:shulker"), new ResourceLocation("minecraft:elder_guardian"), new ResourceLocation("minecraft:ravager"), new ResourceLocation("minecraft:evoker"));
	public static final IntStat CAP_BASE = new IntStat(625, "base_limit");
	public static final IntStat CAP_FACTOR = new IntStat(625, "base_limit_expansion");
	public static final DoubleStat CAP_SCALE = new DoubleStat(1D, "base_limit_scale");
	public static final DoubleStat REAP_SCALE = new DoubleStat(1D, "reap_scale");
	public static final DoubleStat DROP_SOUL_SCALE = new DoubleStat(1D, "drop_scale");
	public static final DoubleStat TEMP_SOUL_SCALE = new DoubleStat(1D, "temp_soul_scale");
	public static final DoubleStat PERM_SOUL_SCALE = new DoubleStat(1D, "perm_soul_scale");
	public static final DoubleStat TRANSCENDED_REAP_MULTIPLIER = new DoubleStat(2.0D, "transcended_reap_scaling");
	
	
	public ArtemisSoul()
	{
		super(new DefaultData("artemis_soul", Rarity.VERY_RARE, 7, true, false, 15, 60, 45), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(VALID_MOBS, CAP_BASE, CAP_FACTOR, CAP_FACTOR, CAP_SCALE, REAP_SCALE, DROP_SOUL_SCALE, TEMP_SOUL_SCALE, PERM_SOUL_SCALE);
		setCategory("battle");
	}
	
	public static boolean isValidSpecialMob(Entity entity)
	{
		return VALID_MOBS.contains(entity.getType().getRegistryName());
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem && stack.getItem() instanceof ToolItem && stack.getItem() instanceof HoeItem;
	}
}