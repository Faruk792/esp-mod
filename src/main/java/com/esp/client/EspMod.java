package com.esp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class EspMod implements ClientModInitializer {

    public static boolean espEnabled = false;
    public static boolean guiOpen = false;
    public static final List<EspBlock> found = Collections.synchronizedList(new ArrayList<>());

    public static int scanRadius = 32;
    public static final Set<String> activeTargets = new HashSet<>(Arrays.asList(
        "diamond", "ancient_debris", "chest", "iron", "gold", "emerald", "netherite"
    ));

    static KeyBinding toggleKey;
    static KeyBinding guiKey;
    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "ESP Aç/Kapat", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "ESP Mod"
        ));
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "ESP GUI", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "ESP Mod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (toggleKey.wasPressed()) {
                espEnabled = !espEnabled;
                if (!espEnabled) found.clear();
                client.player.sendMessage(
                    Text.literal("§aESP " + (espEnabled ? "§2AÇIK ✔" : "§cKAPALI ✘")), true
                );
            }

            if (guiKey.wasPressed()) {
                client.setScreen(new EspGui());
            }

            if (espEnabled) {
                tickCounter++;
                if (tickCounter >= 30) {
                    tickCounter = 0;
                    scan(client);
                }
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
            if (espEnabled && !found.isEmpty()) {
                EspRenderer.render(ctx, found);
            }
        });
    }

    public static void scan(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        found.clear();
        BlockPos center = mc.player.getBlockPos();

        for (int x = -scanRadius; x <= scanRadius; x++)
        for (int y = -scanRadius; y <= scanRadius; y++)
        for (int z = -scanRadius; z <= scanRadius; z++) {
            BlockPos pos = center.add(x, y, z);
            Block b = mc.world.getBlockState(pos).getBlock();
            EspBlock eb = classify(b, pos);
            if (eb != null) found.add(eb);
        }
    }

    static EspBlock classify(Block b, BlockPos pos) {
        if (activeTargets.contains("diamond") &&
            (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE))
            return new EspBlock(pos, "Elmas", 0x00FFFF);

        if (activeTargets.contains("ancient_debris") && b == Blocks.ANCIENT_DEBRIS)
            return new EspBlock(pos, "Ancient Debris", 0xFF6600);

        if (activeTargets.contains("iron") &&
            (b == Blocks.IRON_ORE || b == Blocks.DEEPSLATE_IRON_ORE))
            return new EspBlock(pos, "Demir", 0xCCAAAA);

        if (activeTargets.contains("gold") &&
            (b == Blocks.GOLD_ORE || b == Blocks.DEEPSLATE_GOLD_ORE || b == Blocks.NETHER_GOLD_ORE))
            return new EspBlock(pos, "Altın", 0xFFD700);

        if (activeTargets.contains("emerald") &&
            (b == Blocks.EMERALD_ORE || b == Blocks.DEEPSLATE_EMERALD_ORE))
            return new EspBlock(pos, "Zümrüt", 0x00FF44);

        if (activeTargets.contains("chest") &&
            (b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST ||
             b == Blocks.ENDER_CHEST || b == Blocks.BARREL ||
             b == Blocks.SHULKER_BOX))
            return new EspBlock(pos, "Sandık", 0xFF4444);

        if (activeTargets.contains("netherite") && b == Blocks.ANCIENT_DEBRIS)
            return new EspBlock(pos, "Netherite", 0x8B0000);

        return null;
    }
}
