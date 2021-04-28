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
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
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
	public static final List<List<PotionPlan>> EFFECTS = new ObjectArrayList<>();
	
	public EnchantmentAlchemistsGrace()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, 4, true, 20, 3, 18), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemTool || EnumEnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
		
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentWarriorsGrace || ench instanceof EnchantmentNaturesGrace ? false : super.canApplyTogether(ench);
	}
	
	public static void applyToEntity(Entity entity, boolean mining, float hitScalar)
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
					if(EFFECTS.size() > i)
					{
						for(PotionPlan plan : EFFECTS.get(i))
						{
							if(plan.isValid(mining)) {
								PotionEffect effect = plan.createEffect(level, hitScalar);
								if(potions.add(effect.getPotion())) base.addPotionEffect(effect);
							}
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
		String[] potions = config.getStringList("effects", getConfigName(), new String[]{"minecraft:regeneration;1;0;0.25;0.5;true;false", "minecraft:speed;1;0;1.0;3;true;true", "minecraft:haste;2;0;1.0;2;true;true", "minecraft:speed;2;1;0.5;3.5;true;true", "minecraft:resistance;3;0;1.0;1;true;false", "minecraft:haste;3;1;0.5;3;true;true", "minecraft:strength;4;0;1.0;1;true;false", "minecraft:resistance;4;1;0.25;1.25;true;false", "minecraft:fire_resistance;4;0;1.0;1.5;false;true", "minecraft:strength;5;1;0.2;1.5;true;false"}, "Which Potion Effects should be applied. Format: Potion;MinimumEnchantLevel;PotionBaseLevel;PotionLevelIncrease;BaseDuration");
		for(String s : potions)
		{
			String[] split = s.split(";");
			if(split.length == 7)
			{
				Potion p = Potion.REGISTRY.getObject(new ResourceLocation(split[0]));
				if(p != null)
				{
					try
					{
						PotionPlan plan = new PotionPlan(p, split);
						while(EFFECTS.size() <= plan.baseEnchantment) EFFECTS.add(new ObjectArrayList<>());
						EFFECTS.get(plan.baseEnchantment).add(plan);	
					}
					catch(Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	
	public static class PotionPlan
	{
		Potion potion;
		int baseEnchantment;
		int basePotionLevel;
		double PotionLevelIncrease;
		int baseDuration;
		boolean fighting;
		boolean mining;
		
		public PotionPlan(Potion potion, String[] data)
		{
			this.potion = potion;
			baseEnchantment = Integer.parseInt(data[1]);
			basePotionLevel = Integer.parseInt(data[2]);
			PotionLevelIncrease = Double.parseDouble(data[3]);
			baseDuration = Integer.parseInt(data[4]);
			fighting = Boolean.parseBoolean(data[5]);
			mining = Boolean.parseBoolean(data[6]);
		}
		
		public PotionEffect createEffect(int baseLevel, float hitScalar)
		{
			int diff = Math.max(0, baseLevel - baseEnchantment);
			return new PotionEffect(potion, (int)(baseDuration * baseLevel * Math.log(hitScalar+0.5F)) * 20, basePotionLevel + (int)(PotionLevelIncrease * diff));
		}
		
		public boolean isValid(boolean mining)
		{
			return mining ? this.mining : fighting;
		}
	}
}
