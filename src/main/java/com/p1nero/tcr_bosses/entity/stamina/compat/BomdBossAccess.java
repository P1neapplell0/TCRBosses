package com.p1nero.tcr_bosses.entity.stamina.compat;

public interface BomdBossAccess {
    int tcr_bosses$getActionSerial();

    boolean tcr_bosses$isActing();

    void tcr_bosses$recordAction(byte status);

    void tcr_bosses$clearActions();
}
