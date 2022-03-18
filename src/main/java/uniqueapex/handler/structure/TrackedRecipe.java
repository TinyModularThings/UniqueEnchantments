package uniqueapex.handler.structure;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import uniqueapex.handler.FusionHandler;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.FusionRecipe;
import uniqueapex.utils.AnimationUtils;

public class TrackedRecipe
{
	World world;
	BlockPos pos;
	FusionRecipe recipe;
	FusionContext context;
	int beaconSize;
	int tick;
	List<EnderCrystalEntity> endCrystals;
	List<Vector3d> basePositions;
	float rotation;
	
	public TrackedRecipe(World world, BlockPos pos, FusionRecipe recipe, FusionContext context, int beaconSize, List<EnderCrystalEntity> endCrystals)
	{
		this.world = world;
		this.pos = pos;
		this.recipe = recipe;
		this.context = context;
		this.beaconSize = beaconSize;
		this.endCrystals = endCrystals;
		basePositions = new ObjectArrayList<>();
		for(int i = 1;i<5;i++)
		{
			basePositions.add(endCrystals.get(i).position());
		}
	}
	
	public TrackedRecipe(World world, BlockPos pos, FusionRecipe recipe, FusionContext context, int beaconSize, int currentTick, List<EnderCrystalEntity> endCrystals, List<Vector3d> basePositions)
	{
		this.world = world;
		this.pos = pos;
		this.recipe = recipe;
		this.context = context;
		this.beaconSize = beaconSize;
		this.tick = currentTick;
		this.endCrystals = endCrystals;
		for(int i = 0,m=tick-40;i<m;i++) {
			rotation += (float)Math.toRadians(AnimationUtils.getRotation(i+40));			
		}
	}
	
	public void tick()
	{
		tick++;
		if(tick < 40) return;
		rotation += (float)Math.toRadians(AnimationUtils.getRotation(tick));
		float closer = AnimationUtils.getCloser(tick);
		float yOffset = AnimationUtils.getYOffset(tick);
		float masterY = AnimationUtils.getMasterY(tick);
		for(int i = 0;i<4;i++)
		{
			Direction dir = Direction.from2DDataValue(i).getOpposite();
			EnderCrystalEntity entity = endCrystals.get(i+1);
			Vector3d base = basePositions.get(i).add(dir.getStepX() * closer, yOffset, dir.getStepZ() * closer).subtract(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vector3d nextPos = base.yRot(rotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.setPosAndOldPos(nextPos.x(), nextPos.y(), nextPos.z());
		}
		EnderCrystalEntity entity = endCrystals.get(0);
		entity.setPosAndOldPos(entity.getX(), pos.getY() + 3.5D + masterY, entity.getZ());
	}
	
	public Chunk getChunk() {
		return world.getChunkAt(getPos());
	}
	
	public int getTick()
	{
		return tick;
	}
	
	public BlockPos getPos()
	{
		return pos;
	}
	
	public List<Vector3d> getBasePosition()
	{
		return basePositions;
	}
	
	public boolean isDone()
	{
		return tick >= 255;
	}
	
	public void finishRecipe()
	{
		if(context == null) {
			context = FusionHandler.INSTANCE.context(world, pos);
			if(context == null) return;
		}
		if(!recipe.matches(context, world)) return;
		recipe.assembleEnchantment(context);
	}
	
	public void remove()
	{
		for(EnderCrystalEntity entity : endCrystals)
		{
			entity.remove();
		}
	}
	
	public CompoundNBT save()
	{
		CompoundNBT data = new CompoundNBT();
		data.putString("world", world.dimension().location().toString());
		data.putLong("pos", pos.asLong());
		data.putString("recipe", recipe.getId().toString());
		data.putInt("tick", tick);
		data.putInt("size", beaconSize);
		ListNBT list = new ListNBT();
		for(EnderCrystalEntity crystal : endCrystals)
		{
			list.add(NBTUtil.createUUID(crystal.getUUID()));
		}
		data.put("crystals", list);
		list = new ListNBT();
		for(Vector3d pos : basePositions)
		{
			CompoundNBT nbt = new CompoundNBT();
			nbt.putDouble("x", pos.x());
			nbt.putDouble("y", pos.y());
			nbt.putDouble("z", pos.z());
			list.add(nbt);
		}
		data.put("base", list);
		return data;
	}
	
	public static TrackedRecipe loadRecipe(CompoundNBT data)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		ServerWorld world = server.getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation.tryParse(data.getString("world"))));
		BlockPos pos = BlockPos.of(data.getLong("long"));
		int tick = data.getInt("tick");
		int size = data.getInt("size");
		FusionRecipe recipe = (FusionRecipe)server.getRecipeManager().byKey(ResourceLocation.tryParse(data.getString("recipe"))).orElse(null);
		List<EnderCrystalEntity> endCrystal = new ObjectArrayList<>();
		for(INBT nbt : data.getList("crystals", 11))
		{
			Entity entity = world.getEntity(NBTUtil.loadUUID(nbt));
			if(entity instanceof EnderCrystalEntity)
			{
				endCrystal.add((EnderCrystalEntity)entity);
			}
		}
		List<Vector3d> basePositions = new ObjectArrayList<>();
		for(INBT entry : data.getList("base", 10))
		{
			CompoundNBT nbt = (CompoundNBT)entry;
			basePositions.add(new Vector3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")));
		}
		if(endCrystal.size() != 5 || recipe == null)
		{
			for(EnderCrystalEntity crystal : endCrystal)
			{
				crystal.remove();
			}
			return null;
		}
		return new TrackedRecipe(world, pos, recipe, null, size, tick, endCrystal, basePositions);
	}
	
	public int[] getEntities()
	{
		int[] entities = new int[5];
		for(int i = 0;i<5;i++) {
			entities[i] = endCrystals.get(i).getId();
		}
		return entities;
	}
	
	public List<EnderCrystalEntity> getEntitites(World world)
	{
		List<EnderCrystalEntity> newList = new ObjectArrayList<>();
		for(EnderCrystalEntity entity : endCrystals)
		{
			newList.add((EnderCrystalEntity)world.getEntity(entity.getId()));
		}
		return newList;
	}
}