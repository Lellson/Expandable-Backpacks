package lellson.expandablebackpack.events;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.item.backpack.EnumColor;
import lellson.expandablebackpack.misc.models.ModelBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RenderEvents {
	
	public static final String TAGLAYER = "backpackLayerRenderer";
	private static LayerBackpack layerSteve = new LayerBackpack(Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default"));
	private static LayerBackpack layerAlex = new LayerBackpack(Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim"));

	public static void init() {	
		
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			EntityPlayer player = event.getEntityPlayer();
			ItemStack backpack = BackpackSlotInventory.getStackForSlot(player, 0);
			boolean hasLayer = player.getEntityData().getBoolean(TAGLAYER);
			
			if (!hasLayer && backpack != null) {
				
				AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
				String skin = clientPlayer.getSkinType();

				layerSteve.meta = backpack.getItemDamage();
				layerAlex.meta = backpack.getItemDamage();
				
				if (skin.equals("default")) {
					event.getRenderer().addLayer(layerSteve);
				} else {
					event.getRenderer().addLayer(layerAlex);
				}
				
				player.getEntityData().setBoolean(TAGLAYER, true);
				
			} else if ((backpack != null && layerSteve.meta != backpack.getItemDamage() && layerAlex.meta != backpack.getItemDamage()) || backpack == null) {
				
				AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
				String skin = clientPlayer.getSkinType();
				
				if (skin.equals("default")) {
					event.getRenderer().removeLayer(layerSteve);
				} else {
					event.getRenderer().removeLayer(layerAlex);
				}
				
				player.getEntityData().setBoolean(TAGLAYER, false);
			}
		}
	}
	
	
	public static class LayerBackpack implements LayerRenderer<EntityPlayer> {
		
		private final RenderPlayer renderer;
		private final ModelBackpack model;
		public int meta;
		
		public LayerBackpack(RenderPlayer renderer) {
			this.renderer = renderer;
			this.model = new ModelBackpack(5.0F);
			this.meta = 0;
		}

		@Override
		public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

			model.setModelAttributes(renderer.getMainModel());
			model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			model.bipedBody.showModel = true;
			
			EnumColor color = EnumColor.getEnumFromMeta(meta);
			renderer.bindTexture(new ResourceLocation(ExpandableBackpack.MODID + ":textures/models/armor/backpack_" + color.getName() + ".png"));
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.scale(1.0F, 1.0F, 1.0F);
			
			model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}

		@Override
		public boolean shouldCombineTextures() {

			return false;
		}
		
	}
}
