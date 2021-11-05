package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.LunaticDespair;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getWorld() instanceof ServerWorld)
		{
			int count = event.getPlayer().getPersistentData().getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT);
			if(count > 0)
			{
				TileEntity tile = event.getWorld().getTileEntity(event.getPos());
				if(tile == null) return;
				IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()).orElse(EmptyHandler.INSTANCE);
				if(handler.getSlots() < 9) return;
				LootTable table = ((ServerWorld)event.getWorld()).getServer().getLootTableManager().getLootTableFromLocation(IfritsJudgement.JUDGEMENT_LOOT);
				LootContext.Builder builder = (new LootContext.Builder((ServerWorld)event.getWorld())).withRandom(event.getWorld().getRandom()).withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(event.getPos())).withLuck(event.getPlayer().getLuck()).withParameter(LootParameters.THIS_ENTITY, event.getPlayer());
				table.recursiveGenerate(builder.build(LootParameterSets.CHEST), T -> ItemHandlerHelper.insertItem(handler, T, false));
				event.getPlayer().getPersistentData().putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, count-1);
				if(event.getPlayer().isSneaking()) event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
			int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof PlayerEntity)
			{
				PlayerEntity player = (PlayerEntity)source;
				int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
				int posRolls = 1+source.world.rand.nextInt(maxRolls);
				int negRolls = maxRolls - posRolls;
				LivingEntity enemy = event.getEntityLiving();
				double toughness = enemy.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue();
				double speed = player.getAttribute(Attributes.ATTACK_SPEED).getValue();
				float damageFactor = (float)(Math.log(1+Math.sqrt(player.experienceLevel*level*level)*(1+(enemy.getTotalArmorValue()+toughness*2.5)*AresFragment.ARMOR_PERCENTAGE.get())) / (100F*speed));
				event.setAmount(event.getAmount() * (1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls)));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil((Math.sqrt(Math.abs(posRolls-negRolls) / Math.pow(AresFragment.DURABILITY_REDUCTION_SCALING.get(), 1+(level/100D))) * (((posRolls * Math.pow(AresFragment.DURABILITY_DISTRIBUTION.get(), -1D))-(negRolls * AresFragment.DURABILITY_DISTRIBUTION.get())) / AresFragment.DURABILITY_ANTI_SCALING.get()))/speed), source, MiscUtil.get(EquipmentSlotType.MAINHAND));
			}
			if(event.getEntityLiving().isBurning())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UniqueEnchantmentsBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isBurning() ? 2 : 1)));
				}
			}
			EquipmentSlotType slot = null;
			if(event.getSource().isProjectile())
			{
				Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source);
				slot = found.getKey();
				level = found.getIntValue();
			}
			else
			{
				level = ench.getInt(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT);
				slot = level > 0 ? EquipmentSlotType.MAINHAND : null;
			}
			if(level > 0)
			{
				CompoundNBT entityNBT = event.getEntityLiving().getPersistentData();
				ListNBT list = entityNBT.getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = source.getItemStackFromSlot(slot).getItem().getRegistryName().toString();
				for(int i = 0,m=list.size();i<m;i++)
				{
					CompoundNBT data = list.getCompound(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					CompoundNBT data = new CompoundNBT();
					data.putString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.add(data);
					entityNBT.put(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}

		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				float value = MathCache.LOG_ADD.getFloat(level);
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat(value)));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.GENERIC, LunaticDespair.SELF_DAMAGE.getFloat(value));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.MAGIC, LunaticDespair.SELF_MAGIC_DAMAGE.getFloat(value));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				ListNBT list = event.getEntityLiving().getPersistentData().getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.size();i<m;i++)
				{
					max = Math.max(max, list.getCompound(i).getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
				}
				else if(max > IfritsJudgement.FIRE_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.IN_FIRE, IfritsJudgement.FIRE_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));					
				}
				else if(max > 0)
				{
					CompoundNBT compound = source.getPersistentData();
					compound.putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, compound.getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT)+1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AttributeModifierManager attribute = event.getEntityLiving().getAttributeManager();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.removeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.reapplyModifiers(mods);
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(ItemStack stack, EquipmentSlotType slot, World world)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : CelestialBlessing.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), Operation.ADDITION));
		}
		return mods;
	}
}
