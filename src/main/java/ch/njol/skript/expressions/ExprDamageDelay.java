/**
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
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.expressions;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Damage Delay")
@Description("The delay before an entity can take damage again.")
@Examples({
	"on damage of player:",
		"\tplayer is holding a diamond named \"Damage saviour\"",
		"\tadd 2 seconds to the damage delay"
})
@Since("INSERT VERSION")
public class ExprDamageDelay extends SimplePropertyExpression<LivingEntity, Timespan> {
	
	static {
		Skript.registerExpression(ExprDamageDelay.class, Timespan.class, ExpressionType.PROPERTY,
				"[the] [max:max[imum]] (invulnerability|no damage) (tick[s]|delay) [of %livingentities%]",
				"%livingentities%'[s] [max:max[imum]] (invulnerability|no damage) (tick[s]|delay)"
		);
	}

	private boolean max;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		max = parseResult.hasTag("max");
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@Nullable
	public Timespan convert(LivingEntity entity) {
		if (max)
			return Timespan.fromTicks_i(entity.getMaximumNoDamageTicks());
		return Timespan.fromTicks_i(entity.getNoDamageTicks());
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return CollectionUtils.array(Timespan.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (mode != ChangeMode.RESET && mode != ChangeMode.DELETE && delta == null)
			return;

		int ticks = delta != null ? (int) ((Timespan) delta[0]).getTicks_i() : 0;
		LivingEntity[] entities = getExpr().getArray(event);
		switch (mode) {
			case RESET:
			case DELETE:
			case SET:
				for (LivingEntity entity : entities) {
					if (max) {
						entity.setMaximumNoDamageTicks(ticks);
					} else {
						entity.setNoDamageTicks(ticks);
					}
				}
				break;
			case REMOVE:
				for (LivingEntity entity : entities) {
					if (max) {
						entity.setMaximumNoDamageTicks(Math.max(entity.getMaximumNoDamageTicks() - ticks, 0));
					} else {
						entity.setNoDamageTicks(Math.max(entity.getNoDamageTicks() - ticks, 0));
					}
				}
				break;
			case ADD:
				for (LivingEntity entity : entities) {
					if (max) {
						entity.setMaximumNoDamageTicks(Math.max(entity.getMaximumNoDamageTicks() + ticks, 0));
					} else {
						entity.setNoDamageTicks(Math.max(entity.getNoDamageTicks() + ticks, 0));
					}
				}
				break;
			case REMOVE_ALL:
			default:
				break;
		}
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		return "damage delay";
	}

}
