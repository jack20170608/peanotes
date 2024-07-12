package top.ilovemyhome.peanotes.backend.common.db.dao.common;

import java.io.Serializable;

//all are use the long as the primary key
public interface Persistable<Long> extends Serializable {

	/**
	 * Returns the id of the entity.
	 *
	 * @return the id
	 */
	Long getId();

	/**
	 * Returns if the {@code Persistable} is new or was persisted already.
	 *
	 * @return if the object is new
	 */
	boolean isNew();

}
