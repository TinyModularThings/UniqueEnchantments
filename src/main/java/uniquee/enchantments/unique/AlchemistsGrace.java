package uniquee.enchantments.unique;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.MiscUtil;
import uniquee.UE;

public class AlchemistsGrace extends UniqueEnchantment
{
	public static final List<List<PotionPlan>> EFFECTS = new ObjectArrayList<>();
	static ConfigValue<List<? extends String>> EFFECT_CONFIG;
	public static final DoubleStat TRANSCENDED_MULTIPLIER = new DoubleStat(0.25, "transcended_duration_multiplier");
	
	public AlchemistsGrace()
	{
		super(new DefaultData("alchemists_grace", Rarity.VERY_RARE, 4, true, false, 18, 6, 40).setTrancendenceLevel(200).setHardCap(33), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(TRANSCENDED_MULTIPLIER);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof DiggerItem || EnchantmentCategory.BOW.canEnchant(stack.getItem()) || stack.getItem() instanceof CrossbowItem;
	}
		
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.WARRIORS_GRACE, UE.NATURES_GRACE);
	}
	
	public static void applyToEntity(Entity entity, boolean mining, float hitScalar)
	{
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlot> slot = MiscUtil.getEnchantedItem(UE.ALCHEMISTS_GRACE, base);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				Set<MobEffect> potions = new ObjectOpenHashSet<MobEffect>();
				for(int i = level;i>=0;i--)
				{
					if(EFFECTS.size() <= i)
					{
						continue;
					}
					for(PotionPlan plan : EFFECTS.get(i))
					{
						if(plan.isValid(mining)) 
						{
							MobEffectInstance effect = plan.createEffect(level, hitScalar);
							if(potions.add(effect.getEffect())) base.addEffect(effect);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void loadData(Builder config)
	{
		config.comment("Which Potion Effects should be applied. Format: Potion;StartEnchantmentLvL;StartPotionLvL;PotionLvLPerEnchantLvL;BaseDuration;Fighting;Mining");
		EFFECT_CONFIG = config.defineList("effects", ObjectArrayList.wrap(new String[]{"minecraft:regeneration;1;0;0.25;10;true;false", "minecraft:speed;1;0;1.0;60;true;true", "minecraft:haste;2;0;1.0;40;true;true", "minecraft:speed;2;1;0.5;70;true;true", "minecraft:resistance;3;0;1.0;20;true;false", "minecraft:haste;3;1;0.5;60;true;true", "minecraft:strength;4;0;1.0;20;true;false", "minecraft:resistance;4;1;0.25;25;true;false", "minecraft:fire_resistance;4;0;1.0;30;false;true", "minecraft:strength;5;1;0.2;30;true;false"}), (T) -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		EFFECTS.clear();
		List<? extends String> list = EFFECT_CONFIG.get();
		for(int i = 0,m=list.size();i<m;i++)
		{
			String[] split = list.get(i).split(";");
			if(split.length == 7)
			{
				MobEffect p = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(split[0]));
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
		MobEffect potion;
		int baseEnchantment;
		int basePotionLevel;
		double PotionLevelIncrease;
		int baseDuration;
		boolean fighting;
		boolean mining;
		
		public PotionPlan(MobEffect potion, String[] data)
		{
			this.potion = potion;
			baseEnchantment = Integer.parseInt(data[1]);
			basePotionLevel = Integer.parseInt(data[2]);
			PotionLevelIncrease = Double.parseDouble(data[3]);
			baseDuration = Integer.parseInt(data[4]);
			fighting = Boolean.parseBoolean(data[5]);
			mining = Boolean.parseBoolean(data[6]);
		}
		
		public MobEffectInstance createEffect(int baseLevel, float hitScalar)
		{
			int diff = Math.max(0, baseLevel - baseEnchantment);
			return new MobEffectInstance(potion, Math.max(5, (int)(baseDuration * Math.log((hitScalar+0.5F)*baseLevel))), basePotionLevel + (int)(PotionLevelIncrease * diff));
		}
		
		public boolean isValid(boolean mining)
		{
			return mining ? this.mining : fighting;
		}
	}
}
