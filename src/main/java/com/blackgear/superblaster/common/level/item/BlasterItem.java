package com.blackgear.superblaster.common.level.item;

import com.blackgear.superblaster.common.level.entity.BlasterProjectile;
import com.blackgear.superblaster.common.registries.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlasterItem extends Item implements UpgradeableWeapon {
    public BlasterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ALLAY_HURT, SoundSource.PLAYERS, 1.0F, -0.8F + level.getRandom().nextFloat() * 0.4F);

        if (!level.isClientSide) {
            BlasterProjectile projectile = new BlasterProjectile(level, player);
            projectile.setPos(player.getEyePosition().add(player.getLookAngle().scale(0.5)));
            projectile.setOwner(player);

            projectile.initializeWithWeaponStats(stack);

            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return super.use(level, player, hand);
    }

    @Override
    public StatHolder getFirstStat() {
        return new StatHolder(Component.literal("Projectile Damage"), ModDataComponents.PROJECTILE_DAMAGE);
    }

    @Override
    public StatHolder getSecondStat() {
        return new StatHolder(Component.literal("Explosion Size"), ModDataComponents.EXPLOSION_SIZE);
    }

    @Override
    public StatHolder getThirdStat() {
        return new StatHolder(Component.literal("Heal on Kill"), ModDataComponents.HEAL_ON_KILL);
    }

    @Override
    public StatHolder getForthStat() {
        return new StatHolder(Component.literal("Homing Speed"), ModDataComponents.HOMING_SPEED);
    }
}