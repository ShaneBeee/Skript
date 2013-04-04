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

package ch.njol.skript.events;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Utils;
import ch.njol.util.StringUtils;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings({"unchecked", "serial"})
public class EvtCommand extends SkriptEvent { // TODO condition to check whether a given command exists, & a conditon to check whether it's a custom skript command
	static {
		Skript.registerEvent("Command", EvtCommand.class, Utils.array(PlayerCommandPreprocessEvent.class, ServerCommandEvent.class), "command [%-string%]")
				.description("Called when a player enters a command (not neccessarily a Skript command).")
				.examples("on command", "on command \"/stop\"", "on command \"pm Njol \"")
				.since("2.0");
	}
	
	private String command = null;
	
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		if (args[0] != null) {
			command = ((Literal<String>) args[0]).getSingle();
			if (command.startsWith("/"))
				command = command.substring(1);
		}
		return true;
	}
	
	@Override
	public boolean check(final Event e) {
		if (command == null)
			return true;
		final String message;
		if (e instanceof PlayerCommandPreprocessEvent) {
			message = ((PlayerCommandPreprocessEvent) e).getMessage().substring(1);
		} else {
			message = ((ServerCommandEvent) e).getCommand();
		}
		return StringUtils.startsWithIgnoreCase(message, command)
				&& (command.contains(" ") || message.length() == command.length() || Character.isWhitespace(message.charAt(command.length())));
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "command" + (command == null ? "" : " /" + command);
	}
	
}
