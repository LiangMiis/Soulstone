package org.LiangMi.soulstone.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;

public class AnchorManager {
    private int anchorLevel = 100;
    public void add(int achor){
        this.anchorLevel = Math.min(achor + this.anchorLevel,100);
    }
    public void readNbt(NbtCompound tag){
        if(tag.contains("AnchorLevel",99)){
            this.anchorLevel = tag.getInt("AnchorLevel");
        }
    }
    public void writeNbt(NbtCompound tag){
        tag.putInt("AnchorLevel", this.anchorLevel);
    }
    public int getAnchorLevel(){
        return this.anchorLevel;
    }
    public boolean isNotFull(){
        return this.anchorLevel < 100;
    }
    public void setAnchorLevel(int anchorLevel){
        this.anchorLevel = anchorLevel;
    }
}
