package com.esp.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import java.util.List;

public class EspRenderer {

    public static void render(WorldRenderContext ctx, List<EspBlock> blocks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        MatrixStack ms = ctx.matrixStack();
        Camera cam = ctx.camera();
        Vec3d camPos = cam.getPos();

        ms.push();
        ms.translate(-camPos.x, -camPos.y, -camPos.z);

        var buf = ctx.consumers();
        if (buf == null) { ms.pop(); return; }

        VertexConsumer vc = buf.getBuffer(RenderLayer.getLines());

        for (EspBlock eb : blocks) {
            float r = ((eb.color >> 16) & 0xFF) / 255f;
            float g = ((eb.color >> 8)  & 0xFF) / 255f;
            float bv =  (eb.color       & 0xFF) / 255f;

            drawBox(ms, vc, eb.pos, r, g, bv);
            drawLine(ms, vc, camPos, eb.pos, r, g, bv);
        }

        ms.pop();
    }

    static void drawBox(MatrixStack ms, VertexConsumer vc, BlockPos pos,
                        float r, float g, float b) {
        float x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
        float x2 = x1+1, y2 = y1+1, z2 = z1+1;

        // 12 kenar
        float[][] edges = {
            {x1,y1,z1, x2,y1,z1}, {x2,y1,z1, x2,y1,z2},
            {x2,y1,z2, x1,y1,z2}, {x1,y1,z2, x1,y1,z1},
            {x1,y2,z1, x2,y2,z1}, {x2,y2,z1, x2,y2,z2},
            {x2,y2,z2, x1,y2,z2}, {x1,y2,z2, x1,y2,z1},
            {x1,y1,z1, x1,y2,z1}, {x2,y1,z1, x2,y2,z1},
            {x2,y1,z2, x2,y2,z2}, {x1,y1,z2, x1,y2,z2}
        };

        Matrix4f mat = ms.peek().getPositionMatrix();
        for (float[] e : edges) {
            vc.vertex(mat, e[0],e[1],e[2]).color(r,g,b,1f).normal(ms.peek().getNormalMatrix(),0,1,0).next();
            vc.vertex(mat, e[3],e[4],e[5]).color(r,g,b,1f).normal(ms.peek().getNormalMatrix(),0,1,0).next();
        }
    }

    static void drawLine(MatrixStack ms, VertexConsumer vc, Vec3d cam,
                         BlockPos pos, float r, float g, float b) {
        float cx = (float)cam.x, cy = (float)cam.y, cz = (float)cam.z;
        float bx = pos.getX()+0.5f, by = pos.getY()+0.5f, bz = pos.getZ()+0.5f;
        Matrix4f mat = ms.peek().getPositionMatrix();
        vc.vertex(mat,cx,cy,cz).color(r,g,b,0.6f).normal(ms.peek().getNormalMatrix(),0,1,0).next();
        vc.vertex(mat,bx,by,bz).color(r,g,b,0.6f).normal(ms.peek().getNormalMatrix(),0,1,0).next();
    }
}
