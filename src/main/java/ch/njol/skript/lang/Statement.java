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

package ch.njol.skript.lang;

import java.util.Iterator;

import ch.njol.skript.Skript;

/**
 * Supertype of conditions and effects
 * 
 * @author Peter Güttinger
 * @see Condition
 * @see Effect
 */
@SuppressWarnings("serial")
public abstract class Statement extends TriggerItem implements SyntaxElement {
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Statement parse(final String s, final String defaultError) {
		return (Statement) SkriptParser.parse(s, (Iterator) Skript.getStatements().iterator(), defaultError);
	}
	
}
