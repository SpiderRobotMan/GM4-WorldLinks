/**
 * WorldLink - Multi-Dimensional Survival Server
 * Copyright (C) 2017, 18  Gamemode 4
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.gm4.worldlink.modules.display;

import co.gm4.worldlink.WorldLink;
import co.gm4.worldlink.objects.Link;
import co.gm4.worldlink.objects.LinkPlayer;
import co.gm4.worldlink.objects.LinkWorld;
import co.gm4.worldlink.utils.Config;
import co.gm4.worldlink.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.stream.Collectors;

/**
 * Created by MatrixTunnel on 9/20/2017.
 */
public class Filter {

    public static boolean canDisplay(LinkPlayer linkPlayer, LinkWorld linkWorld) {
        Config config = WorldLink.get().getPluginConfig();
        boolean canDisplay = true;

        // If it's not in the config, return
        if (!config.getLinks().stream().map(Link::getName).collect(Collectors.toList()).contains(linkWorld.getName())) return false;

        for (Link link : config.getLinks()) {
            if (link.getName().equals(linkWorld.getName())) {
                if (!link.getDisplayAdvancements().isEmpty() && !(link.getDisplayAdvancements().size() == 1 && link.getDisplayAdvancements().get(0).equals("name_space:with/name_here"))) {
                    canDisplay = link.getDisplayAdvancements().stream().allMatch(s -> PlayerUtils.hasAdvancement(Bukkit.getPlayer(linkPlayer.getUuid()), s));
                }

                if (!link.getDisplayLocation().isEmpty() && (link.getDisplayLocation().contains("-") || link.getDisplayLocation().contains(">") || link.getDisplayLocation().contains("<"))) {
                    int found = isInLocation(link.getDisplayLocation().replace(" ", "").split(","), linkPlayer.getPlayer().getLocation());
                    canDisplay = (found == 5 || found == 3);
                }

                if (!link.getOffhandItem().isEmpty()) {
                    canDisplay = linkPlayer.getPlayer().getInventory().getItemInOffHand().getType().name().equals(link.getOffhandItem());
                }
            }
        }

        return canDisplay;
    }

    private static int isInLocation(String[] location, Location loc) {
        int found = 0;

        if (location.length >= 3) {
            String x = location[0];
            String y = location[1];
            String z = location[2];

            if (x.contains(">")) {
                if (loc.getX() > Double.valueOf(x.replace(">", ""))) found++;
            } else if (x.contains("<")) {
                if (loc.getX() < Double.valueOf(x.replace("<", ""))) found++;
            } else if (x.contains("-")) {
                // highest-lowest
                String[] doubleX = x.split("-");

                if (loc.getX() <= Double.valueOf(doubleX[0]) &&
                        loc.getX() >= Double.valueOf(doubleX[1])) found++;
            } else if (x.equals("~")) {
                found++;
            }

            if (y.contains(">")) {
                if (loc.getY() > Double.valueOf(y.replace(">", ""))) found++;
            } else if (y.contains("<")) {
                if (loc.getY() < Double.valueOf(y.replace("<", ""))) found++;
            } else if (y.contains("-")) {
                // highest-lowest
                String[] doubleY = y.split("-");

                if (loc.getY() <= Double.valueOf(doubleY[0]) &&
                        loc.getY() >= Double.valueOf(doubleY[1])) found++;
            } else if (y.equals("~")) {
                found++;
            }

            if (z.contains(">")) {
                if (loc.getZ() > Double.valueOf(z.replace(">", ""))) found++;
            } else if (z.contains("<")) {
                if (loc.getZ() < Double.valueOf(z.replace("<", ""))) found++;
            } else if (z.contains("-")) {
                // highest-lowest
                String[] doubleZ = z.split("-");

                if (loc.getZ() <= Double.valueOf(doubleZ[0]) &&
                        loc.getZ() >= Double.valueOf(doubleZ[1])) found++;
            } else if (z.equals("~")) {
                found++;
            }

            if (location.length == 5) {
                String yaw = location[3];
                String pitch = location[4];

                if (yaw.contains(">")) {
                    if (loc.getYaw() > Double.valueOf(yaw.replace(">", ""))) found++;
                } else if (yaw.contains("<")) {
                    if (loc.getYaw() < Double.valueOf(yaw.replace("<", ""))) found++;
                } else if (yaw.contains("-")) {
                    // highest-lowest
                    String[] doubleYaw = yaw.split("-");

                    if (loc.getYaw() <= Double.valueOf(doubleYaw[0]) &&
                            loc.getYaw() >= Double.valueOf(doubleYaw[1])) found++;
                } else if (pitch.equals("~")) {
                    found++;
                }

                if (pitch.contains(">")) {
                    if (loc.getPitch() > Double.valueOf(pitch.replace(">", ""))) found++;
                } else if (pitch.contains("<")) {
                    if (loc.getPitch() < Double.valueOf(pitch.replace("<", ""))) found++;
                } else if (pitch.contains("-")) {
                    // highest-lowest
                    String[] doublePitch = pitch.split("-");

                    if (loc.getPitch() <= Double.valueOf(doublePitch[0]) &&
                            loc.getPitch() >= Double.valueOf(doublePitch[1])) found++;
                } else if (pitch.equals("~")) {
                    found++;
                }
            }
        }

        return found;
    }

}
