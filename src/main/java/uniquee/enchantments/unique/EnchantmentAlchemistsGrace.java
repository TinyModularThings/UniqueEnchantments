package uniquee.enchantments.unique;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentAlchemistsGrace extends UniqueEnchantment
{
	public static Potion[] EFFECTS = new Potion[]{MobEffects.SPEED, MobEffects.HASTE, MobEffects.RESISTANCE, MobEffects.STRENGTH};
	public static int AMPLIFIER_CAP = 2;
	public static int SECONDS = 4;
	
	public EnchantmentAlchemistsGrace()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, true, 20, 3, 18), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 4;
	}
	
	@Override
	public boolean canApply(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || EnumEnchantmentType.BOW.canEnchantItem(stack.getItem()) ? true : super.canApply(stack);
	}
		
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentWarriorsGrace || ench instanceof EnchantmentNaturesGrace ? false : super.canApplyTogether(ench);
	}
	
	public static void applyToEntity(Entity entity)
	{
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			ItemStack stack = EnchantmentHelper.getEnchantedItem(UniqueEnchantments.ALCHEMISTS_GRACE, base);
			if(!stack.isEmpty())
			{
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.ALCHEMISTS_GRACE, stack);
				int amplifier = MathHelper.clamp(level - 1, 0, AMPLIFIER_CAP - 1);
				int duration = 3 * level * SECONDS * 20;
				int max = level == 4 ? EFFECTS.length : Math.min(EFFECTS.length, level);
				for(int i = 0;i<max;i++)
				{
					base.addPotionEffect(new PotionEffect(EFFECTS[i], duration, amplifier));
				}
			}
		}
	}

	@Override
	public void loadData(Configuration config)
	{
		String[] potions = config.getStringList("effects", getConfigName(), new String[]{MobEffects.SPEED.getRegistryName().toString(), MobEffects.HASTE.getRegistryName().toString(), MobEffects.RESISTANCE.getRegistryName().toString(), MobEffects.STRENGTH.getRegistryName().toString()}, "Which Potion Effects should be applied");
		SECONDS = config.get(getConfigName(), "duration_scalar", 4, "How much the duration should be multiplied with (in seconds)").getInt();
		AMPLIFIER_CAP = config.get(getConfigName(), "amplifier_cap", 2, "The Softcap of the Potion strenghts that get applied").getInt();
		ObjectList<Potion> potionList = new ObjectArrayList<Potion>();
		for(String s : potions)
		{
			Potion p = Potion.REGISTRY.getObject(new ResourceLocation(s));
			if(p != null)
			{
				potionList.add(p);
			}
		}
		EFFECTS = potionList.toArray(new Potion[0]);
	}
}
