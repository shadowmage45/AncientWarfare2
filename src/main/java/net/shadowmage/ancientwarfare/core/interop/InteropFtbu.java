package net.shadowmage.ancientwarfare.core.interop;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import ftb.utils.api.FriendsAPI;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interop.InteropFtbuChunkData.ChunkLocation;
import net.shadowmage.ancientwarfare.core.interop.InteropFtbuChunkData.TownHallOwner;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

/**
 * TODO:
 * FTBU supports notifications. Change the log output messages here to use Notifications instead!
 * @author CosmicDan
 *
 */
public class InteropFtbu implements InteropFtbuInterface {
    @Override
    public boolean areFriends(String player1, String player2) {
        if (FriendsAPI.areFriends(player1, player2))
            return true;
        return false;
    }

    @Override
    public void claimChunks(World world, EntityLivingBase placer, int posX, int posY, int posZ) {
        if (placer == null) {
            try {
                AncientWarfareCore.log.error("A non-player entity placed a Town Hall - I don't know how to handle this for land claims! Please report this error!");
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        claimChunks(world, placer.getCommandSenderName(), posX, posY, posZ);
    }
    
    @Override
    public void claimChunks(World world, String ownerName, int posX, int posY, int posZ) {
        LMPlayerServer p = LMWorldServer.inst.getPlayer(ownerName);
        if (p != null) {
            Chunk origin = world.getChunkFromBlockCoords(posX, posZ);
            AncientWarfareCore.log.info("Registering TownHall owner for BlockPos: " + posX + "x" + posY + "x" + posZ);
            for (int chunkX = origin.xPosition - AWNPCStatics.townChunkClaimRadius; chunkX <= origin.xPosition + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                for (int chunkZ = origin.zPosition - AWNPCStatics.townChunkClaimRadius; chunkZ <= origin.zPosition + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                    // Chunk was claimed successfully (or already was), build the ChunkLocation key
                    ChunkLocation thisChunk = new ChunkLocation(chunkX, chunkZ, world.provider.dimensionId);
                    // check if this key already exists
                    List<TownHallOwner> townHallOwners = InteropFtbuChunkData.INSTANCE.chunkClaims.get(thisChunk);
                    if (townHallOwners == null) { //unclaimed chunk, make a new TownHallInfo list
                        townHallOwners = new ArrayList<TownHallOwner>();
                        AncientWarfareCore.log.info("Claiming new chunk at BlockPos: " + chunkX*16 + "x" + chunkZ*16);
                    } else {
                        AncientWarfareCore.log.info("Already claimed chunk at BlockPos: " + chunkX*16 + "x" + chunkZ*16);
                    }
                    // add this townhall to the chunkclaim entry
                    townHallOwners.add(new TownHallOwner(ownerName, posX, posY, posZ));
                    InteropFtbuChunkData.INSTANCE.chunkClaims.put(thisChunk, townHallOwners);
                    // attempt chunk claim regardless; if already owned by a different player it will silently fail
                    p.claimChunk(world.provider.dimensionId, chunkX, chunkZ);
                }
            }
            InteropFtbuChunkData.INSTANCE.markDirty();
        } else {
            // ?
        }
    }
    
    

    @Override
    public void unclaimChunks(World world, String ownerName, int posX, int posY, int posZ) {
        LMPlayerServer p = LMWorldServer.inst.getPlayer(ownerName);
        Chunk origin = world.getChunkFromBlockCoords(posX, posZ);
        AncientWarfareCore.log.info("Removing TownHall owner for BlockPos: " + posX + "x" + posY + "x" + posZ);
        for (int chunkX = origin.xPosition - AWNPCStatics.townChunkClaimRadius; chunkX <= origin.xPosition + AWNPCStatics.townChunkClaimRadius; chunkX++) {
            for (int chunkZ = origin.zPosition - AWNPCStatics.townChunkClaimRadius; chunkZ <= origin.zPosition + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                AncientWarfareCore.log.info("Checking chunk at BlockPos for unclaiming: " + chunkX*16 + "x" + chunkZ*16);
                // check if this chunk is claimed
                ChunkLocation thisChunk = new ChunkLocation(chunkX, chunkZ, world.provider.dimensionId);
                List<TownHallOwner> townHallOwners = InteropFtbuChunkData.INSTANCE.chunkClaims.get(thisChunk);
                if (townHallOwners == null) {
                    // shouldn't happen! Or maybe it can? I don't know lol
                    AncientWarfareCore.log.info(" - Chunk was claimed but had no Town Hall owner? Meh, unclaim it and just return");
                    p.unclaimChunk(world.provider.dimensionId, chunkX, chunkZ);
                    return;
                }

                // first remove the destroyed town hall from the chunkClaims
                TownHallOwner destroyedTownHall = new TownHallOwner(ownerName, posX, posY, posZ);
                Iterator<TownHallOwner> townHallOwnersIterator = townHallOwners.iterator();
                int townHallIndex = 0;
                boolean removedOwner = false;
                while (townHallOwnersIterator.hasNext()) {
                    TownHallOwner townHallOwner = townHallOwnersIterator.next();
                    if (townHallOwner.equals(destroyedTownHall)) {
                        if (townHallIndex == 0) {
                            AncientWarfareCore.log.info(" - Removed a destroyed town hall that owned and was controlling this chunk, possible territory loss...");
                            removedOwner = true;
                        } else
                            AncientWarfareCore.log.info(" - Removed a destroyed town hall that wasn't controlling the chunk. Territory unchanged.");
                        townHallOwnersIterator.remove();
                    }
                    townHallIndex++;
                }
                
                if (!removedOwner)
                    continue; // we only removed a Town Hall with a secondary stake so we can skip the rest
                
                // check if this chunk is still owned by the same owner
                if (townHallOwners.size() > 0) {
                    boolean chunkIsStillOwned = false;
                    for (TownHallOwner townHallOwner : townHallOwners) {
                        if (townHallOwner.getOwnerName().equals(ownerName)) {
                            AncientWarfareCore.log.info(" ... found an existing Town Hall for the same owner. This chunk claim is unchanged.");
                            chunkIsStillOwned = true;
                        }
                    }
                    if (!chunkIsStillOwned) {
                        // Original player lost the chunk but another player has a stake on it
                        AncientWarfareCore.log.info(" ... territory lost to a nearby player: " + townHallOwners.get(0).getOwnerName());
                        p.unclaimChunk(world.provider.dimensionId, chunkX, chunkZ);
                        LMPlayerServer pNew = LMWorldServer.inst.getPlayer(townHallOwners.get(0).getOwnerName());
                        pNew.claimChunk(world.provider.dimensionId, chunkX, chunkZ);
                    }
                } else {
                    // there is no owner of the chunk left at all
                    AncientWarfareCore.log.info(" ... no owner left, territory relinquished to the wilderness.");
                    p.unclaimChunk(world.provider.dimensionId, chunkX, chunkZ);
                    InteropFtbuChunkData.INSTANCE.chunkClaims.remove(thisChunk);
                }
                
            }
        }
        InteropFtbuChunkData.INSTANCE.markDirty();
    }

    
}