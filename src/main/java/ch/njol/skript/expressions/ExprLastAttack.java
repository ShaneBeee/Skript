/**
 * This file is part of Skript.
 *
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.expressions;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Last Attacker/Damage/Cause")
@Description({
	"The last damage, final damage, damage cause or entity attacker that attacked an entity last.",
	"Can return nothing which means that the entity was not attacked recently, same applies when using changers."
})
@Examples("send \"%last attacker of event-entity%\"")
@RequiredPlugins("The settable changers require 1.14+")
@Since("2.5.1, INSERT VERSION (Changers, damage cause and damage)")
public class ExprLastAttack extends SimplePropertyExpression<Entity, Object> {

	private final static boolean SETTABLE = Skript.methodExists(Entity.class, "setLastDamageCause", EntityDamageEvent.class);

	static {
		register(ExprLastAttack.class, Object.class, "last [known] ([:final] damage [amount]|:attacker|cause:damage cause)", "entity");
	}

	private boolean attacker, cause, finalDamage;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Entity>) exprs[0]);
		finalDamage = parseResult.hasTag("final");
		attacker = parseResult.hasTag("attacker");
		cause = parseResult.hasTag("cause");
		return true;
	}

	@Override
	@Nullable
	public Object convert(Entity entity) {
		if (attacker)
			return ExprAttacker.getAttacker(entity.getLastDamageCause());
		if (finalDamage)
			return entity.getLastDamageCause().getFinalDamage();
		if (cause)
			return entity.getLastDamageCause().getCause();
		return entity.getLastDamageCause().getDamage();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (!SETTABLE || mode != ChangeMode.SET)
			return null;
		if (attacker)
			return CollectionUtils.array(Entity.class);
		if (cause)
			return CollectionUtils.array(DamageCause.class);
		return CollectionUtils.array(Number.class);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null)
			return;
		if (attacker) {
			Entity damager = (Entity) delta[0];
			for (Entity entity : getExpr().getArray(event)) {
				EntityDamageEvent damageEvent = entity.getLastDamageCause();
				// Entity has not been harmed.
				if (damageEvent == null) {
					entity.setLastDamageCause(new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, 0D));
					continue;
				}
				entity.setLastDamageCause(new EntityDamageByEntityEvent(damager, entity, damageEvent.getCause(), damageEvent.getDamage()));
			}
		} else if (cause) {
			DamageCause cause = (DamageCause) delta[0];
			for (Entity entity : getExpr().getArray(event)) {
				EntityDamageEvent damageEvent = entity.getLastDamageCause();
				// We have no entity, the entity cannot be null in the EntityDamageEvent constructor.
				if (damageEvent == null)
					continue;
				// Future proof
				if (damageEvent instanceof EntityDamageByBlockEvent) {
					entity.setLastDamageCause(new EntityDamageByBlockEvent(((EntityDamageByBlockEvent)damageEvent).getDamager(), entity, cause, damageEvent.getDamage()));
					continue;
				}
				entity.setLastDamageCause(new EntityDamageByEntityEvent(damageEvent.getEntity(), entity, cause, damageEvent.getDamage()));
			}
		} else {
			double damage = ((Number) delta[0]).doubleValue();
			for (Entity entity : getExpr().getArray(event)) {
				EntityDamageEvent damageEvent = entity.getLastDamageCause();
				// We have no entity, the entity cannot be null in the EntityDamageEvent constructor.
				if (damageEvent == null)
					continue;
				// Future proof
				if (damageEvent instanceof EntityDamageByBlockEvent) {
					entity.setLastDamageCause(new EntityDamageByBlockEvent(((EntityDamageByBlockEvent)damageEvent).getDamager(), entity, damageEvent.getCause(), damage));
					continue;
				}
				entity.setLastDamageCause(new EntityDamageByEntityEvent(damageEvent.getEntity(), entity, damageEvent.getCause(), damage));
			}
		}
	}

	@Override
	public Class<?> getReturnType() {
		return attacker ? Entity.class : cause ? DamageCause.class : Double.class;
	}

	@Override
	protected String getPropertyName() {
		return "last " + (attacker ? "attacker" : (finalDamage ? "final " : "") + "damage") + (cause ? " cause" : "");
	}

}
