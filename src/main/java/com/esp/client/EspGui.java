package com.esp.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspGui extends Screen {

    public EspGui() { super(Text.literal("ESP Ayarları")); }

    @Override
    protected void init() {
        int cx = width/2, y = height/2 - 120;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("ESP: " + (EspMod.espEnabled ? "§2AÇIK" : "§cKAPALI")),
            btn -> {
                EspMod.espEnabled = !EspMod.espEnabled;
                if (!EspMod.espEnabled) EspMod.found.clear();
                btn.setMessage(Text.literal("ESP: " + (EspMod.espEnabled ? "§2AÇIK" : "§cKAPALI")));
            }).dimensions(cx-75, y, 150, 20).build());

        y += 28;
        String[] targets = {"diamond","ancient_debris","iron","gold","emerald","chest","netherite"};
        String[] labels  = {"💎 Elmas","🔥 Ancient Debris","⚙ Demir","✨ Altın","💚 Zümrüt","📦 Sandıklar","🖤 Netherite"};

        for (int i = 0; i < targets.length; i++) {
            final String t = targets[i];
            final String l = labels[i];
            boolean on = EspMod.activeTargets.contains(t);
            addDrawableChild(ButtonWidget.builder(
                Text.literal(l + ": " + (on ? "§2✔" : "§c✘")),
                btn -> {
                    if (EspMod.activeTargets.contains(t)) EspMod.activeTargets.remove(t);
                    else EspMod.activeTargets.add(t);
                    btn.setMessage(Text.literal(l + ": " + (EspMod.activeTargets.contains(t) ? "§2✔" : "§c✘")));
                }).dimensions(cx-75, y, 150, 18).build());
            y += 22;
        }

        y += 6;
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Yarıçap: " + EspMod.scanRadius),
            btn -> {
                EspMod.scanRadius = EspMod.scanRadius >= 64 ? 16 : EspMod.scanRadius + 16;
                btn.setMessage(Text.literal("Yarıçap: " + EspMod.scanRadius));
            }).dimensions(cx-75, y, 150, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Kapat"),
            btn -> close()).dimensions(cx-40, y, 80, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float dt) {
        renderBackground(ctx);
        ctx.drawCenteredTextWithShadow(textRenderer, "§6⬛ ESP Mod Ayarları", width/2, height/2-140, 0xFFFFFF);
        super.render(ctx, mx, my, dt);
    }

    @Override
    public boolean shouldPause() { return false; }
}
