package fun.wich.mixin.client;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
	@ModifyVariable(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;applyStartEndModifier(Lnet/minecraft/client/render/fog/FogData;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/world/ClientWorld;FLnet/minecraft/client/render/RenderTickCounter;)V", shift = At.Shift.AFTER))
	private FogData MuddyPigs_ModifyFogData(FogData fogData, Camera camera, int viewDistance, boolean thick, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world) {
		if (world.getFluidState(camera.getBlockPos()).isIn(MuddyPigsMod.TAG_MUD) && camera.getSubmersionType() == CameraSubmersionType.WATER) {
			fogData.renderDistanceStart = 0.25F;
			fogData.renderDistanceEnd = 1.0F;
		}
		return fogData;
	}
	@Unique	private static final Vector4f FOG_COLOR = new Vector4f(71 / 255f, 40 / 255f, 4 / 255f, 1f);
	@Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
	private void MuddyPigs_ModifyFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, boolean thick, CallbackInfoReturnable<Vector4f> cir) {
		if (world.getFluidState(camera.getBlockPos()).isIn(MuddyPigsMod.TAG_MUD) && camera.getSubmersionType() == CameraSubmersionType.WATER) {
			cir.setReturnValue(FOG_COLOR);
		}
	}
}
