package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMakerMixin {
	@Redirect(method="getStart()Lnet/minecraft/entity/ai/pathing/PathNode;", at=@At(value="INVOKE", target="Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	private boolean TreatMudAsWaterInGetStart(BlockState instance, Block block) {
		if (block == Blocks.WATER && instance.isOf(MuddyPigsMod.MUD_FLUID_BLOCK)) return true;
		return instance.isOf(block);
	}
}
