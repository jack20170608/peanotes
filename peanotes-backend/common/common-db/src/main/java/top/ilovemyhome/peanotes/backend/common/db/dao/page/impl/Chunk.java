package top.ilovemyhome.peanotes.backend.common.db.dao.page.impl;

import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Slice;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Sort;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public abstract class Chunk<T> implements Slice<T>, Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

	private final List<T> content = new ArrayList<T>();
	private final Pageable pageable;

	public Chunk(List<T> content, Pageable pageable) {

		this.content.addAll(content);
		this.pageable = pageable;
	}


	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}


	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}


	public int getNumberOfElements() {
		return content.size();
	}


	public boolean hasPrevious() {
		return getNumber() > 0;
	}


	public boolean isFirst() {
		return !hasPrevious();
	}


	public boolean isLast() {
		return !hasNext();
	}


	public Pageable nextPageable() {
		return hasNext() ? pageable.next() : null;
	}


	public Pageable previousPageable() {
		if (hasPrevious()) {
			return pageable.previousOrFirst();
		}
		return null;
	}


	public boolean hasContent() {
		return !content.isEmpty();
	}

	public List<T> getContent() {
		return Collections.unmodifiableList(content);
	}

	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}

	public Iterator<T> iterator() {
		return content.iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Chunk<?>)) {
			return false;
		}
		Chunk<?> that = (Chunk<?>) obj;
		boolean contentEqual = this.content.equals(that.content);
		boolean pageableEqual = this.pageable == null ? that.pageable == null : this.pageable.equals(that.pageable);
		return contentEqual && pageableEqual;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * (pageable == null ? 0 : pageable.hashCode());
		result += 31 * content.hashCode();
		return result;
	}
}
