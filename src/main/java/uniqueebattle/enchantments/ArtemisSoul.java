package uniqueebattle.enchantments;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
	
	
	public ArtemisSoul()
	{
		super(new DefaultData("artemis_soul", Rarity.VERY_RARE, 7, true, 15, 60, 45), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(VALID_MOBS, CAP_BASE, CAP_FACTOR, CAP_FACTOR, CAP_SCALE, REAP_SCALE, DROP_SOUL_SCALE, TEMP_SOUL_SCALE, PERM_SOUL_SCALE);
		setCategory("battle");
	}
	
	public static boolean isValidSpecialMob(Entity entity)
	{
		return VALID_MOBS.contains(EntityList.getKey(entity.getClass()));
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemBow && stack.getItem() instanceof ItemTool && stack.getItem() instanceof ItemHoe;
	}
}