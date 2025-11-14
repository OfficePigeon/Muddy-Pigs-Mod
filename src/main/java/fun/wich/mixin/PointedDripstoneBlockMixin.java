package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
	@Shadow private static Fluid getDripFluid(World world, Fluid fluid) { return null; }
	@Shadow @Final private static double DOWN_TIP_Y;
	@Inject(method="createParticle(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/Fluid;)V", at=@At("HEAD"), cancellable=true)
	private static void MuddyPigs_CreateParticle(World world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfo ci){
		Fluid fluid2 = getDripFluid(world, fluid);
		//noinspection deprecation
		if (fluid2 != null && fluid2.isIn(MuddyPigsMod.TAG_MUD)) {
			Vec3d vec3d = state.getModelOffset(pos);
			world.addParticleClient(MuddyPigsMod.DRIPPING_DRIPSTONE_MUD, pos.getX() + 0.5 + vec3d.x, pos.getY() + DOWN_TIP_Y - 0.0625, pos.getZ() + 0.5 + vec3d.z, 0.0, 0.0, 0.0);
			ci.cancel();
		}
	}
}
