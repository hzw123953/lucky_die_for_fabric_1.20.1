package com.hwn.hwn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class Hwn implements ModInitializer {
    
    private static final long TICK_INTERVAL = 200; // 10 seconds = 200 ticks (20 ticks per second)
    private long tickCount = 0;

    @Override
    public void onInitialize() {
        // 服务器端每十秒检查一次
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer server) {
        tickCount++;
        
        if (tickCount >= TICK_INTERVAL) {
            tickCount = 0;
            
            // 检查是否要让某个玩家死亡（万分之一几率）
            if (Math.random() < 0.0001) { // 0.01% = 万分之一
                if (!server.getPlayerManager().getPlayerList().isEmpty()) {
                    // 随机选择一个玩家
                    var players = server.getPlayerManager().getPlayerList();
                    PlayerEntity target = players.get((int)(Math.random() * players.size()));
                    
                    // 让玩家死亡且不掉落物品
                    killPlayerWithoutDrop(target);
                    
                    // 发送消息给所有玩家
                    server.getPlayerManager().broadcast(Text.of("§6[幸运死亡] §e" + target.getName().getString() + " 被幸运之神选中了！"), false);
                }
            }
        }
    }

    private void killPlayerWithoutDrop(PlayerEntity player) {
        // 清除背包物品以防止掉落
        player.getInventory().clear();
        player.getEnderChestInventory().clear();
        
        // 清除经验
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.experienceLevel = 0;
        
        // 杀死玩家（不掉落物品）
        player.setHealth(0);
    }
}
