package uniqueeutils.enchantments.unique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.SlotUUID;

public class SagesSoul extends UniqueEnchantment
{
	public static final SlotUUID ATTACK_MOD = new SlotUUID(
			"6ddc3f38-7e67-48ff-b2c2-23f760ece08d", "04a8822e-1a00-4481-bf83-15ac36e0bd1d", "322a1a40-c275-498a-9022-d8e8eff1d6af", 
			"2655cab3-3157-4ebf-af31-4fe3cb90c366", "c355905c-16b8-437e-8667-d8ec141a80d4", "ee3bd867-3a1d-42ba-abe6-f009f90287eb");
	public static final SlotUUID ARMOR_MOD = new SlotUUID(
			"ae1fae91-e112-4977-91ad-d77a2eb20b62", "0f8ef7d5-07f2-44cb-90c0-a2aa7c8135e3", "7d1d69f8-94e4-4d25-9f09-7873e31034f6", 
			"0e2ec732-001e-45db-80c6-018c9be7db00", "8c16e295-5be3-4f9b-8ddf-4e5ec6516a6b", "4b0fa86a-c936-4c38-9e42-f439408414b0");
	public static final SlotUUID TOUGHNESS_MOD = new SlotUUID(
			"f3f06e28-f3c1-4a73-a672-6252949164e7", "ed8ca9b9-8a33-46bd-abf3-2d25b331ac12", "c7117846-d274-47b1-b81e-68c729221dad", 
			"3163a457-3b60-45a5-90fa-180143a0ab38", "4ba0d357-da5c-4c94-b1b2-01691f8626cc", "442170ec-b2c4-4eb1-b576-cdf17f031c70");
	public static final String STORED_XP = "sage_xp";
	public static final IntStat DIVIDOR = new IntStat(4, "enchantment_devidor");
	public static final DoubleStat MINING_SPEED = new DoubleStat(1D, "mining_speed");
	public static final DoubleStat ATTACK_SPEED = new DoubleStat(1D, "attack_speed");
	public static final IntStat ATTACK_DIVIDOR = new IntStat(128, "attack_dividor");
	public static final DoubleStat DRAW_SPEED = new DoubleStat(1D, "draw_speed");
	public static final DoubleStat ARMOR_SCALE = new DoubleStat(1D, "armor_scale");
	public static final IntStat ARMOR_DIVIDOR = new IntStat(128, "armor_dividor");
	public static final DoubleStat TOUGHNESS_SCALE = new DoubleStat(1D, "toughness_scale");
	public static final IntStat TOUGHNESS_DIVIDOR = new IntStat(128, "armor_dividor");
	
	public SagesSoul()
	{
		super(new DefaultData("sages_soul", Rarity.VERY_RARE, 10, true, false, 50, 20, 5), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		addStats(DIVIDOR, MINING_SPEED, ATTACK_DIVIDOR, ATTACK_SPEED, DRAW_SPEED, ARMOR_SCALE, ARMOR_DIVIDOR, TOUGHNESS_SCALE, TOUGHNESS_DIVIDOR);
		setCategory("utils");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "ameliorated_sharpness"), new ResourceLocation("uniquee", "spartan_weapon"), new ResourceLocation("uniquee", "berserker"), new ResourceLocation("uniquee", "alchemists_grace"), new ResourceLocation("uniquee", "ender_mending"));
		addIncompats(Enchantments.SHARPNESS, Enchantments.BLOCK_EFFICIENCY, Enchantments.ALL_DAMAGE_PROTECTION, Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof PotionItem;
	}
	
	public static double getEnchantPower(ItemStack stack, int level)
	{
		return Math.log10(10+Math.pow(((level*level*0.5D)+stack.getEnchantmentTags().size()), (1/DIVIDOR.get())));
	}
}
