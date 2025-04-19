package com.blackgear.superblaster.common.level.item;

public interface UpgradeableWeapon {
    StatHolder getFirstStat();

    StatHolder getSecondStat();

    StatHolder getThirdStat();

    StatHolder getForthStat();
}