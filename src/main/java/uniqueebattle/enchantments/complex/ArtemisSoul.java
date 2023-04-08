package uniqueebattle.enchantments.complex;

import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.StackUtils;

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
	private static final IdStat<EntityType<?>> VALID_MOBS = new IdStat<>("valid_mobs", ForgeRegistries.ENTITY_TYPES, new ResourceLocation("minecraft:ender_dragon"), new ResourceLocation("minecraft:wither"), new ResourceLocation("minecraft:shulker"), new ResourceLocation("minecraft:elder_guardian"), new ResourceLocation("minecraft:ravager"), new ResourceLocation("minecraft:evoker"));
	public static final IntStat CAP_BASE = new IntStat(625, "base_limit");
	public static final IntStat CAP_FACTOR = new IntStat(625, "base_limit_expansion");
	public static final DoubleStat CAP_SCALE = new DoubleStat(1D, "base_limit_scale");
	public static final DoubleStat REAP_SCALE = new DoubleStat(1D, "reap_scale");
	public static final DoubleStat DROP_SOUL_SCALE = new DoubleStat(1D, "drop_scale");
	public static final DoubleStat TEMP_SOUL_SCALE = new DoubleStat(1D, "temp_soul_scale");
	public static final DoubleStat PERM_SOUL_SCALE = new DoubleStat(1D, "perm_soul_scale");
	public static final String TRANSCENDED_MOD = "m_soul";
	public static final DoubleStat TRANSCENDED_REAP_MULTIPLIER = new DoubleStat(0.01D, "transcended_reap_multiplier");
	
	
	public ArtemisSoul()
	{
		super(new DefaultData("artemis_soul", Rarity.VERY_RARE, 7, true, false, 15, 60, 45).setTrancendenceLevel(500), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(VALID_MOBS, CAP_BASE, CAP_FACTOR, CAP_FACTOR, CAP_SCALE, REAP_SCALE, DROP_SOUL_SCALE, TEMP_SOUL_SCALE, PERM_SOUL_SCALE, TRANSCENDED_REAP_MULTIPLIER);
		setCategory("battle");
	}
	
	public static boolean isValidSpecialMob(Entity entity)
	{
		return VALID_MOBS.contains(entity.getType());
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem && stack.getItem() instanceof DiggerItem && stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
		return StackUtils.getFloat(enchantedItem, TRANSCENDED_MOD, 0);
	}
}