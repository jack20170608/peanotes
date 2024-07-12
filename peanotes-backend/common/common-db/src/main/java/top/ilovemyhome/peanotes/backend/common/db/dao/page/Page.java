package top.ilovemyhome.peanotes.backend.common.db.dao.page;

public interface Page<T> extends Slice<T> {

	/**
	 * Returns the number of total pages.
	 *
	 * @return the number of toral pages
	 */
	int getTotalPages();

	/**
	 * Returns the total amount of elements.
	 *
	 * @return the total amount of elements
	 */
	long getTotalElements();
}
