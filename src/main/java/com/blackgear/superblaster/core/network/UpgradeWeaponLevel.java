package com.blackgear.superblaster.core.network;

import com.blackgear.superblaster.common.level.inventory.ScrapWorkbenchMenu;
import com.blackgear.superblaster.common.registries.ModDataComponents;
import com.blackgear.superblaster.core.SuperBlaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpgradeWeaponLevel(int containerId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpgradeWeaponLevel> TYPE = new Type<>(SuperBlaster.resource("upgrade_weapon"));
    public static final StreamCodec<ByteBuf, UpgradeWeaponLevel> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        UpgradeWeaponLevel::containerId,
        UpgradeWeaponLevel::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpgradeWeaponLevel packet, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu.containerId == packet.containerId && menu instanceof ScrapWorkbenchMenu workbench) {
                ItemStack stack = workbench.getWeaponStack();

                if (!stack.isEmpty() && workbench.isValidItem(stack)) {
                    int level = stack.getOrDefault(ModDataComponents.WEAPON_LEVEL, 0);
                    if (level >= 4) return;

                    if (player.getInventory().contains(new ItemStack(Items.DIAMOND))) {
                        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack invStack = player.getInventory().getItem(i);
                            if (invStack.is(Items.DIAMOND)) {
                                invStack.shrink(1);
                                stack.set(ModDataComponents.WEAPON_LEVEL, level + 1);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}