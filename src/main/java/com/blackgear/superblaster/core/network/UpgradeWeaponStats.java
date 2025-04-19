package com.blackgear.superblaster.core.network;

import com.blackgear.superblaster.common.level.inventory.ScrapWorkbenchMenu;
import com.blackgear.superblaster.common.level.item.UpgradeableWeapon;
import com.blackgear.superblaster.core.SuperBlaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpgradeWeaponStats(int containerId, int firstStatValue, int secondStatValue, int thirdStatValue, int forthStatValue) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpgradeWeaponStats> TYPE = new Type<>(SuperBlaster.resource("update_weapon_stats"));
    public static final StreamCodec<ByteBuf, UpgradeWeaponStats> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, UpgradeWeaponStats::containerId,
        ByteBufCodecs.VAR_INT, UpgradeWeaponStats::firstStatValue,
        ByteBufCodecs.VAR_INT, UpgradeWeaponStats::secondStatValue,
        ByteBufCodecs.VAR_INT, UpgradeWeaponStats::thirdStatValue,
        ByteBufCodecs.VAR_INT, UpgradeWeaponStats::forthStatValue,
        UpgradeWeaponStats::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpgradeWeaponStats packet, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu.containerId == packet.containerId && menu instanceof ScrapWorkbenchMenu workbench) {
                ItemStack stack = workbench.getWeaponStack();
                if (workbench.isValidItem(stack) && stack.getItem() instanceof UpgradeableWeapon weapon) {
                    stack.set(weapon.getFirstStat().data(), packet.firstStatValue);
                    stack.set(weapon.getSecondStat().data(), packet.secondStatValue);
                    stack.set(weapon.getThirdStat().data(), packet.thirdStatValue);
                    stack.set(weapon.getForthStat().data(), packet.forthStatValue);
                }
            }
        });
    }
}