package top.ilovemyhome.peanotes.backend.common.db.dao.page;

public interface Page<T> extends Slice<T> {

	int getTotalPages();

	long getTotalElements();

    int FIRST_PAGE = 0;
    int DEFAULT_PAGE_SIZE = 20;
}
