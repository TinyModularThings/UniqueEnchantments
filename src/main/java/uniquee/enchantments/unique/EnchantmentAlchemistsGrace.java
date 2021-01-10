package uniquee.enchantments.unique;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.MiscUtil;

public class EnchantmentAlchemistsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static List<List<PotionEffect>> EFFECTS = new ObjectArrayList<List<PotionEffect>>();
	public static int SECONDS = 4;
	
	public EnchantmentAlchemistsGrace()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, 4, true, 20, 3, 18), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe || EnumEnchantmentType.BOW.canEnchantItem(stack.getItem());
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
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ALCHEMISTS_GRACE, base);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				Set<Potion> potions = new ObjectOpenHashSet<Potion>();
				for(int i = level;i>=0;i--)
				{
					if(EFFECTS.size() <= i)
					{
						continue;
					}
					for(PotionEffect effect : EFFECTS.get(i))
					{
						if(potions.add(effect.getPotion()))
						{
							base.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration() * level * (SECONDS * 20), effect.getAmplifier()));
						}
					}
				}
			}
		}
	}

	@Override
	public void loadData(Configuration config)
	{
		EFFECTS.clear();
		String[] potions = config.getStringList("effects", getConfigName(), new String[]{"1;minecraft:speed;1;4", "2;minecraft:haste;1;6", "2;minecraft:speed;2;6", "3;minecraft:resistance;1;8", "3;minecraft:haste;2;8", "4;minecraft:strength;2;10", "4;minecraft:resistance;2;10"}, "Which Potion Effects should be applied. Format: MinimumEnchantLevel;Potion;PotionLevel;BaseDuration");
		SECONDS = config.get(getConfigName(), "duration_scalar", 4, "How much the duration should be multiplied with (in seconds)").getInt();
		for(String s : potions)
		{
			String[] split = s.split(";");
			if(split.length < 4)
			{
				continue;
			}
			Potion p = Potion.REGISTRY.getObject(new ResourceLocation(split[1]));
			if(p != null)
			{
				try
				{
					int index = Integer.parseInt(split[0]);
					while(EFFECTS.size() <= index)
					{
						EFFECTS.add(new ObjectArrayList<PotionEffect>());
					}
					EFFECTS.get(index).add(new PotionEffect(p, Integer.parseInt(split[3]), Integer.parseInt(split[2])));	
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
