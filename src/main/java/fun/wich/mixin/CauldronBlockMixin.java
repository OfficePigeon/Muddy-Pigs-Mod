package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin {
	@Inject(method="fillFromDripstone", at=@At("HEAD"), cancellable=true)
	private void FillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid, CallbackInfo ci) {
		if (fluid != MuddyPigsMod.STILL_MUD_FLUID) return;
		world.setBlockState(pos, MuddyPigsMod.MUD_CAULDRON.getDefaultState());
		world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
		world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
		ci.cancel();
	}
}
