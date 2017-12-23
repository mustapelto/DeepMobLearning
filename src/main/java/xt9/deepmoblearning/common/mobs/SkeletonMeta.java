package xt9.deepmoblearning.common.mobs;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;

/**
 * Created by xt9 on 2017-06-09.
 */
public class SkeletonMeta extends MobMetaData {
    private EntitySkeleton entity;
    private int interfaceScale = 38;
    private int interfaceOffsetX = 6;
    private int interfaceOffsetY = 10;
    private int numberOfHearts = 10;
    private String mobName = "The Skeleton";
    private String[] mobTrivia = {"A formidable archer, which seem to be running", "some sort of cheat engine", "A shield could prove useful"};

    public SkeletonMeta() {
        super();
        this.entity = new EntitySkeleton(this.world);
        this.entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW));
    }

    public String getMobName() {
        return this.mobName;
    }

    public String[] getMobTrivia() {
        return this.mobTrivia;
    }

    public int getNumberOfHearts() {
        return this.numberOfHearts;
    }

    public EntitySkeleton getEntity() {
        return this.entity;
    }

    public int getInterfaceScale() {
        return this.interfaceScale;
    }

    public int getInterfaceOffsetX() {
        return this.interfaceOffsetX;
    }

    public int getInterfaceOffsetY() {
        return this.interfaceOffsetY;
    }
}