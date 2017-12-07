/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ObservableCollection<T> implements Collection<T> {

    private final Collection<T> wrapped;
    private final List<ChangeListener<T>> listeners = new ArrayList<>();

    public ObservableCollection(final Collection<T> wrapped) {
        Objects.requireNonNull(wrapped, "Observed collection can not be null");

        this.wrapped = wrapped;
    }

    public void addListener(ChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean add(T t) {
        listeners.forEach(listener -> listener.preAdd(t));
        boolean added = wrapped.add(t);
        if (added) {
            listeners.forEach(listener -> listener.postAdd(t));
        }

        return added;
    }

    @Override
    public boolean remove(Object o) {
        listeners.forEach(listener -> listener.preRemove((T) o));
        boolean removed = wrapped.remove(o);
        if (removed) {
            listeners.forEach(listener -> listener.postRemove((T) o));
        }

        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return wrapped.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrapped.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return wrapped.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrapped.retainAll(c);
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return wrapped.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return wrapped.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return wrapped.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        wrapped.forEach(action);
    }
}
