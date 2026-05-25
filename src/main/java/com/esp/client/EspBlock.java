package com.esp.client;

import net.minecraft.util.math.BlockPos;

public class EspBlock {
    public final BlockPos pos;
    public final String label;
    public final int color;

    public EspBlock(BlockPos pos, String label, int color) {
        this.pos = pos;
        this.label = label;
        this.color = color;
    }
}
