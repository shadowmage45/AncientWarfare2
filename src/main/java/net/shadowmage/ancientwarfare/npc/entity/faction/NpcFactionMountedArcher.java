package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.RangeAttackHelper;

public abstract class NpcFactionMountedArcher extends NpcFactionMounted implements IRangedAttackMob {

    public NpcFactionMountedArcher(World par1World) {
        super(par1World);
//  this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
        IEntitySelector selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
//      if(!canEntityBeSeen(entity)){return false;}
                if (!isHostileTowards(entity)) {
                    return false;
                }
                if (hasHome()) {
                    ChunkCoordinates home = getHomePosition();
                    double dist = entity.getDistanceSq(home.posX + 0.5d, home.posY, home.posZ + 0.5d);
                    if (dist > 30 * 30) {
                        return false;
                    }
                }
                return true;
            }
        };

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIFactionRideHorse(this)));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIFactionArcherStayAtHome(this));
        this.tasks.addTask(3, new NpcAIFactionRangedAttack(this));
//  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f)); 
//  this.tasks.addTask(3, new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(1, new NpcAIHurt(this));
        this.targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float force) {
        RangeAttackHelper.doRangedAttack(this, target, force, 1.0f);
    }

}
