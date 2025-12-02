package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class MuddyPigs_LavaFluidMixin extends FlowableFluid {
	@Shadow protected abstract void playExtinguishEvent(WorldAccess world, BlockPos pos);
	@Inject(method="flow", at=@At("HEAD"), cancellable=true)
	private void LavaFluidMixin_flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci) {
		if (direction == Direction.DOWN) {
			FluidState fluidState2 = world.getFluidState(pos);
			//noinspection deprecation
			if (this.isIn(FluidTags.LAVA) && fluidState2.isIn(MuddyPigsMod.TAG_MUD)) {
				if (state.getBlock() instanceof FluidBlock) world.setBlockState(pos, Blocks.DIRT.getDefaultState(), Block.NOTIFY_ALL);
				this.playExtinguishEvent(world, pos);
				ci.cancel();
			}
		}
	}
}
