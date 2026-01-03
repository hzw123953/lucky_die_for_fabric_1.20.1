package com.hwn.hwn.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class HwnClient implements ClientModInitializer {
    
    private static final long CLIENT_TICK_INTERVAL = 200; // 10 seconds = 200 ticks
    private long clientTickCount = 0;
    private boolean hasEnteredWorld = false;

    @Override
    public void onInitializeClient() {
        // 客户端每十秒检查一次
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        // 检查玩家是否已进入世界
        if (client.player != null && client.world != null) {
            hasEnteredWorld = true;
        }
        
        // 只有在玩家已进入世界后才开始计时
        if (!hasEnteredWorld) {
            return;
        }
        
        clientTickCount++;
        
        if (clientTickCount >= CLIENT_TICK_INTERVAL) {
            clientTickCount = 0;
            
            // 检查是否要让当前玩家死亡（万分之一几率）
            if (Math.random() < 0.0001) { // 0.01% = 万分之一
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    // 让玩家死亡且不掉落物品
                    killPlayerWithoutDropClient(player);
                    
                    // 在客户端显示消息
                    if (client.player != null) {
                        client.player.sendMessage(Text.of("§6[幸运死亡] §e你被幸运之神选中了！"), false);
                    }
                }
            }
        }
    }

    private void killPlayerWithoutDropClient(ClientPlayerEntity player) {
        // 清除背包物品以防止掉落
        player.getInventory().clear();
        
        // 清除经验
        player.totalExperience = 0;
        player.experienceProgress = 0;
        player.experienceLevel = 0;
        
        // 杀死玩家（不掉落物品）
        player.setHealth(0);
    }
}
