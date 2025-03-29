package com.unknown.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class IdentityHashSet<T> implements Set<T> {
	private Set<IdentityWrapper<T>> set = new HashSet<>();

	private static class IdentityWrapper<T> {
		private T t;

		public IdentityWrapper(T t) {
			this.t = t;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(t);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			return o != null && o instanceof IdentityWrapper && ((IdentityWrapper<T>) o).t == t;
		}
	}

	public boolean add(T e) {
		return set.add(new IdentityWrapper<>(e));
	}

	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for(T o : c) {
			changed |= add(o);
		}
		return changed;
	}

	public void clear() {
		set.clear();
	}

	public boolean contains(Object o) {
		return set.contains(new IdentityWrapper<>(o));
	}

	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public Iterator<T> iterator() {
		return new Iterator<>() {
			Iterator<IdentityWrapper<T>> i = set.iterator();

			public boolean hasNext() {
				return i.hasNext();
			}

			public T next() {
				IdentityWrapper<T> w = i.next();
				if(w != null) {
					return w.t;
				} else {
					return null;
				}
			}

		};
	}

	public boolean remove(Object o) {
		return set.remove(new IdentityWrapper<>(o));
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		set.retainAll(c.stream().map(IdentityWrapper::new).collect(Collectors.toList()));
		return false;
	}

	public int size() {
		return set.size();
	}

	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	public <E> E[] toArray(E[] a) {
		return set.stream().map((x) -> x.t).collect(Collectors.toList()).toArray(a);
	}

}
