package uniqueeutils.enchantments.unique;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class SagesSoul extends UniqueEnchantment
{
	public static final UUID ATTACK_MOD = UUID.fromString("6ddc3f38-7e67-48ff-b2c2-23f760ece08d");
	public static final UUID ARMOR_MOD = UUID.fromString("ae1fae91-e112-4977-91ad-d77a2eb20b62");
	public static final UUID TOUGHNESS_MOD = UUID.fromString("f3f06e28-f3c1-4a73-a672-6252949164e7");
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
		super(new DefaultData("sages_soul", Rarity.VERY_RARE, 10, true, 50, 20, 5), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(DIVIDOR, MINING_SPEED, ATTACK_DIVIDOR, ATTACK_SPEED, DRAW_SPEED, ARMOR_SCALE, ARMOR_DIVIDOR, TOUGHNESS_SCALE, TOUGHNESS_DIVIDOR);
		setCategory("utils");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "all"), new ResourceLocation("uniquee", "spartanweapon"), new ResourceLocation("uniquee", "berserk"), new ResourceLocation("uniquee", "alchemistsgrace"), new ResourceLocation("uniquee", "ender_mending"));
		addIncompats(Enchantments.SHARPNESS, Enchantments.EFFICIENCY, Enchantments.PROTECTION, Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof PotionItem;
	}
	
	public static double getEnchantPower(ItemStack stack, int level)
	{
		return Math.log10(10+Math.pow(((level*level*0.5D)+stack.getEnchantmentTagList().size()), (1/DIVIDOR.get())));
	}
}
