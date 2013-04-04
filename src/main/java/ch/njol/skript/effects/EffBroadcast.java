/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011-2013 Peter Güttinger
 * 
 */

package ch.njol.skript.effects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("serial")
@Name("Broadcast")
@Description("Broadcasts a message to the server.")
@Examples({"broadcast \"Welcome %player% to the server!\"",
		"broadcast \"Woah! It's a message!\""})
@Since("1.0")
public class EffBroadcast extends Effect {
	
	static {
		Skript.registerEffect(EffBroadcast.class, "broadcast %strings% [(to|in) %-worlds%]");
	}
	
	private Expression<String> messages;
	private Expression<World> worlds;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		messages = (Expression<String>) vars[0];
		worlds = (Expression<World>) vars[1];
		return true;
	}
	
	@Override
	public void execute(final Event e) {
		for (final String m : messages.getArray(e)) {
//			m = StringUtils.fixCapitalization(m);
			if (worlds == null) {
				Bukkit.broadcastMessage(m);
			} else {
				for (final World w : worlds.getArray(e)) {
					for (final Player p : w.getPlayers()) {
						p.sendMessage(m);
					}
				}
			}
		}
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "broadcast " + messages.toString(e, debug) + (worlds == null ? "" : " to " + worlds.toString(e, debug));
	}
	
}
