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

package ch.njol.skript.conditions;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Comparator.Relation;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Comparators;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("serial")
@Name("Contains")
@Description("Checks whether an inventory contains the given item, a text contains another piece of text, or a list of objects (e.g. a {list variable::*}) contains another object.")
@Examples({"block contains 20 cobblestone",
		"player has 4 flint and 2 iron ingots"})
@Since("1.0")
public class CondContains extends Condition {
	
	static {
		Skript.registerCondition(CondContains.class,
				"%inventories% ha(s|ve) %itemtypes% [in [(the[ir]|his|her|its)] inventory]",
				"%inventories/strings/objects% contain[s] %itemtypes/strings/objects%",
				"%inventories% do[es](n't| not) have %itemtypes% [in [(the[ir]|his|her|its)] inventory]",
				"%inventories/strings/objects% do[es](n't| not) contain %itemtypes/strings/objects%");
	}
	
	private Expression<?> containers;
	private Expression<?> items;
	
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		containers = exprs[0];
		items = exprs[1];
		setNegated(matchedPattern >= 2);
		return true;
	}
	
	@Override
	public boolean check(final Event e) {
		return containers.check(e, new Checker<Object>() {
			@Override
			public boolean check(final Object container) {
				if (containers.isSingle()) {
					if (container instanceof Inventory) {
						final Inventory invi = (Inventory) container;
						return items.check(e, new Checker<Object>() {
							@Override
							public boolean check(final Object type) {
								return type instanceof ItemType && ((ItemType) type).isContainedIn(invi);
							}
						}, isNegated());
					} else if (container instanceof String) {
						final String s = ((String) container).toLowerCase();
						return items.check(e, new Checker<Object>() {
							@Override
							public boolean check(final Object type) {
								return type instanceof String && s.contains(((String) type).toLowerCase());
							}
						}, isNegated());
					}
					return false;
				} else {
					return items.check(e, new Checker<Object>() {
						@Override
						public boolean check(final Object item) {
							return Relation.EQUAL.is(Comparators.compare(container, item));
						}
					}, isNegated());
				}
			}
		});
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return containers.toString(e, debug) + (isNegated() ? " doesn't contain " : " contains ") + items.toString(e, debug);
	}
	
}
