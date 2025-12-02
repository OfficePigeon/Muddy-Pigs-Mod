package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.MobNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobNavigation.class)
public class MuddyPigs_MobNavigationMixin {
	@Redirect(method="getPathfindingY", at=@At(value="INVOKE", target="Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	private boolean TreatMudAsWaterForSwimming(BlockState instance, Block block) {
		if (block == Blocks.WATER && instance.isOf(MuddyPigsMod.MUD_FLUID_BLOCK)) return true;
		return instance.isOf(block);
	}
}
