/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class VehicleTypeCatapultStandTurret extends VehicleTypeCatapult {

	/**
	 * @param typeNum
	 */
	public VehicleTypeCatapultStandTurret(int typeNum) {
		super(typeNum);
		this.configName = "catapult_stand_turret";
		this.width = 2.7f;
		this.height = 2.f;
		this.baseMissileVelocityMax = 32.f;
		this.turretVerticalOffset = 13 * 0.0625f;
		this.turretVerticalOffset = 0.4f;
		this.riderForwardsOffset = 1.2f;
		this.riderVerticalOffset = 0.7f;
		this.displayName = "item.vehicleSpawner.1";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.torsion");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.fixed");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.midturret");
		this.storageBaySize = 0;
		this.armorBaySize = 4;
		this.upgradeBaySize = 4;
		this.yawAdjustable = true;
		this.baseForwardSpeed = 0.f;
		this.baseStrafeSpeed = .5f;
		this.turretRotationMax = 45.f;
		this.drivable = true;
		this.riderSits = true;
		this.riderMovesWithTurret = true;
/* TODO vehicle recipe
		this.addNeededResearch(0, ResearchGoal.vehicleTurrets1);
		this.addNeededResearch(1, ResearchGoal.vehicleTurrets2);
		this.addNeededResearch(2, ResearchGoal.vehicleTurrets3);
		this.addNeededResearch(3, ResearchGoal.vehicleTurrets4);
		this.addNeededResearch(4, ResearchGoal.vehicleTurrets5);
		this.additionalMaterials.add(new ItemStackWrapperCrafting(ItemLoader.turretComponents, 1, false, false));
*/
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret1");
			case 1:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret2");
			case 2:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret3");
			case 3:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret4");
			case 4:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret5");
			default:
				return new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/catapultStandTurret1");
		}
	}
}
