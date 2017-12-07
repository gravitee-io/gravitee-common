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
public class ObservableSet<T> implements Set<T> {

    private final Set<T> collections;

    private final List<ChangeListener<T>> listeners = new ArrayList<>();

    public ObservableSet(final Set<T> collections) {
        Objects.requireNonNull(collections, "Observed collection can not be null");

        this.collections = collections;
    }

    public void addListener(ChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public int size() {
        return collections.size();
    }

    @Override
    public boolean isEmpty() {
        return collections.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return collections.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return collections.iterator();
    }

    @Override
    public Object[] toArray() {
        return collections.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return collections.toArray(a);
    }

    @Override
    public boolean add(T t) {
        listeners.forEach(listener -> listener.preAdd(t));
        boolean added = collections.add(t);
        if (added) {
            listeners.forEach(listener -> listener.postAdd(t));
        }

        return added;
    }

    @Override
    public boolean remove(Object o) {
        listeners.forEach(listener -> listener.preRemove((T) o));
        boolean removed = collections.remove(o);
        if (removed) {
            listeners.forEach(listener -> listener.postRemove((T) o));
        }

        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return collections.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return collections.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return collections.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return collections.removeAll(c);
    }

    @Override
    public void clear() {
        collections.clear();
    }

    @Override
    public boolean equals(Object o) {
        return collections.equals(o);
    }

    @Override
    public int hashCode() {
        return collections.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return collections.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return collections.removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return collections.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return collections.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        collections.forEach(action);
    }
}
