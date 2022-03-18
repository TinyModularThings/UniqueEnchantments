package uniquebase.utils.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BeaconTileEntity.BeamSegment;

@Mixin(BeaconTileEntity.class)
public interface BeaconMixin
{
	@Accessor("beamSections")
	public List<BeamSegment> getBeaconSegments();
}
