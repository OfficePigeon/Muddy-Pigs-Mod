package fun.wich.mixin;

import fun.wich.EntityTouchingMud;
import fun.wich.MuddyPig;
import fun.wich.MuddyPigsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityTouchingMud {
	@Shadow public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);
	@Unique private boolean isTouchingMud;
	public boolean EntityInMud_IsTouchingMud() { return this.isTouchingMud; }
	@Inject(method="updateWaterState", at=@At("HEAD"))
	private void MuddyPigs_UpdateWaterState(CallbackInfoReturnable<Boolean> cir) {
		isTouchingMud = this.updateMovementInFluid(MuddyPigsMod.TAG_MUD, this instanceof MuddyPig ? 0.003 : 0.014);
	}
	@Redirect(method="getVelocityMultiplier", at=@At(value= "INVOKE", target="Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	protected boolean AllowMudToBeTreatedAsWaterInGetVelocityMultiplier(BlockState instance, Block block) {
		if (block == Blocks.WATER && instance.isOf(MuddyPigsMod.MUD_FLUID_BLOCK)) return true;
		return instance.isOf(block);
	}
}
