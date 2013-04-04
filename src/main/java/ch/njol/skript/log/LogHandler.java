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
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.log;

/**
 * @author Peter Güttinger
 */
public abstract class LogHandler {
	
	/**
	 * @param entry
	 * @return Whether to print the specified entry or not.
	 */
	public abstract boolean log(LogEntry entry);
	
	/**
	 * Called just after the handler is removed from the active handlers stack.
	 */
	public void onStop() {}
	
	public final void stop() {
		SkriptLogger.removeHandler(this);
		onStop();
	}
	
}
