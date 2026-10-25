package com.p1nero.tcr_bosses.entity.goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;

public class MobHateTracker {
    private static final long HATE_MEMORY_TICKS = 1200;
    private final Map<UUID, List<DamageEntry>> damageHistory = new HashMap<>();
    private final LongSupplier gameTime;
    private long lastCleanupTick = Long.MIN_VALUE;

    public MobHateTracker(LongSupplier gameTime) {
        this.gameTime = gameTime;
    }

    public void addHateData(UUID uuid, float damage) {
        if (!Float.isFinite(damage) || damage <= 0.0F) {
            return;
        }
        updateHateData();
        damageHistory.computeIfAbsent(uuid, key -> new ArrayList<>())
                .add(new DamageEntry(damage, gameTime.getAsLong()));
    }

    private void updateHateData() {
        long currentTime = gameTime.getAsLong();
        if (currentTime == lastCleanupTick) {
            return;
        }
        lastCleanupTick = currentTime;
        damageHistory.entrySet().removeIf(entry -> {
            entry.getValue().removeIf(damage -> currentTime - damage.tick() > HATE_MEMORY_TICKS);
            return entry.getValue().isEmpty();
        });
    }

    public double getTotalHate(UUID uuid) {
        updateHateData();
        List<DamageEntry> history = damageHistory.get(uuid);
        double totalHate = 0.0;
        if (history != null) {
            for (DamageEntry damage : history) {
                totalHate += damage.amount();
            }
        }
        return totalHate;
    }

    private record DamageEntry(float amount, long tick) {
    }
}
