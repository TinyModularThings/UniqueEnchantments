package uniquebase.utils.mixin.common.tile;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;

@Mixin(BeaconBlockEntity.class)
public interface BeaconMixin
{
	@Accessor("beamSections")
	public List<BeaconBeamSection> getBeaconSegments();
	
	@Accessor("levels")
	public int getBeaconLevel();
}
