package info.kgeorgiy.ja.antonov.arrayset;

import java.util.*;

//java -cp . -p . -m info.kgeorgiy.java.advanced.arrayset NavigableSet info.kgeorgiy.ja.antonov.arrayset.ArraySet

public class ArraySet<E> implements NavigableSet<E> {

    protected final List<E> list;

    protected boolean reversed;
    protected final Comparator<E> comparator;

    public ArraySet() {
        this.list = new ArrayList<E>();
        this.comparator = null;
    }


    public ArraySet(Collection<? extends E> collection) {
        TreeSet<E> buffSet = new TreeSet<>(collection);
        this.list = new ArrayList<>();
        list.addAll(buffSet);
        this.comparator = null;
    }

    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        TreeSet<E> buffSet = new TreeSet<>(comparator);
        buffSet.addAll(collection);
        this.list = new ArrayList<>();
        list.addAll(buffSet);
        this.comparator = comparator;
    }

    private ArraySet(ArraySet<E> set, int from, int to) {
        this.reversed = set.reversed;
        this.comparator = set.comparator;
        this.list = set.list.subList(from, to);
    }

    private ArraySet(ArraySet<E> set, boolean reversed) {
        this.reversed = reversed;
        this.comparator = set.comparator;
        this.list = set.list;
    }


    //strictly less
    @Override
    public E lower(E e) {
        chekNull(e);
        int index = lowerIndex(e);
        return chekBorders(index) ? list.get(index) : null;
    }

    @Override
    public E floor(E e) {
        chekNull(e);
        int index = lowerOrEqIndex(e);
        return chekBorders(index) ? list.get(index) : null;
    }

    @Override
    public E ceiling(E e) {
        chekNull(e);
        int index = greaterOrEqIndex(e);
        return chekBorders(index) ? list.get(index) : null;
    }

    @Override
    public E higher(E e) {
        chekNull(e);
        int index = greaterIndex(e);
        return chekBorders(index) ? list.get(index) : null;
    }

    //from - inclusive; to - exclusive
    //makes it as it
    private NavigableSet<E> makeSubSet(int from, int to) {

        if (from > to) {
            throw new IllegalArgumentException("FromIndex(" + from + ") greater then toIndex(" + to+ ")");
        }

        return new ArraySet<E>(this, from, to);
    }
    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        chekNull(toElement);

        int toIndex;
        int fromIndex;
        if(reversed){
            fromIndex = inclusive ? lowerOrEqIndex(toElement) :  lowerIndex(toElement);
            toIndex = size();
        } else {
            fromIndex = 0;
            toIndex = inclusive ? greaterIndex(toElement) : greaterOrEqIndex(toElement);
        }

        return makeSubSet(fromIndex, toIndex);

    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        chekNull(fromElement);

        int toIndex;
        int fromIndex;
        if(reversed){
            fromIndex = 0;
            toIndex = inclusive ? lowerIndex(fromElement) : lowerOrEqIndex(fromElement);
        } else {
            fromIndex = inclusive ? greaterOrEqIndex(fromElement) : greaterIndex(fromElement);
            toIndex = size();
        }

        return makeSubSet(fromIndex, toIndex);
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {

        if (reversed ? comparator.compare(fromElement, toElement) < 0 : comparator.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("FromElement is greater then toElement");
        }

        return tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }


    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(reversed ? size() - 1 : 0);
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(reversed ? 0 : size() - 1);
    }


    /*
    the index of the search key, if it is contained in the list; otherwise, (-(insertion point) - 1). The insertion
    point is defined as the point at which the key would be inserted into the list: the index of the first element
    greater than the key, or list.size() if all elements in the list are less than the specified key. Note that this
    guarantees that the return value will be >= 0 if and only if the key is found.
    */
    //greater or eq than the key
    protected int searchFor(E o) {
        return Collections.binarySearch(list, o, comparator);
    }

    protected int greaterIndex(E o) {
        int index = searchFor(o);
        if (index < 0) {
            index = -1 - index;
            index += reversed ? -1 : 0;
        } else {
            while (chekBorders(index) && equals(list.get(index), o)) {
                index += reversed ? -1 : 1;
            }
        }
        return index;
    }

    protected int greaterOrEqIndex(E o) {
        int index = searchFor(o);
        if (index < 0) {
            index = -1 - index;
            index += reversed ? -1 : 0;
        }
        return index;
    }

    protected int lowerIndex(E o) {
        int index = searchFor(o);
        if (index < 0) {
            index = -1 - index;
            index += reversed ? 0 : -1;
        } else {
            index += reversed ? 1 : -1;
        }

        while (chekBorders(index) && equals(list.get(index), o)) {
            index += reversed ? 1 : -1;
        }
        return index;
    }

    protected int lowerOrEqIndex(E o) {
        int index = searchFor(o);
        if (index < 0) {
            index = -1 - index;
            index += reversed ? 0 : -1;
        }
        return index;
    }

    protected boolean equals(E o1, E o2) {
        return comparator.compare(o1, o2) == 0;
    }

    private boolean chekBorders(int index) {
        return 0 <= index && index < size();
    }

    //=============done=============

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object i : c) {
            if (!contains(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<E>(this, !reversed);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ArraySetIterator<E>((ArraySet<E>) descendingSet());
    }

    @Override
    public boolean contains(Object o) {
        chekNull(o);
        if (isEmpty()) {
            return false;
        }
        //How corroctly cast? o to E
        E element = (E) o;
        int binIndex = searchFor(element);
        return binIndex >= 0;
    }

    @Override
    public Comparator<? super E> comparator() {
        return reversed ? comparator.reversed() : comparator;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator<E>(this);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    protected void chekNull(Object e) {
        if (e == null) {
            throw new NullPointerException("Set does not permit null elements");
        }
    }

    private static class ArraySetIterator<E> implements Iterator<E> {

        protected final ArraySet<E> set;
        protected int currentIndex;

        private ArraySetIterator(ArraySet<E> set) {
            this.set = set;
            this.currentIndex = set.reversed ? set.size() - 1 : 0;
        }

        @Override
        public boolean hasNext() {
            return set.reversed ? currentIndex > 0 : currentIndex < set.size() - 1;
        }

        @Override
        public E next() {
            E res = set.list.get(currentIndex);
            currentIndex = currentIndex + (set.reversed ? -1 : 1);
            return res;
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size())
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(toArray(), size(), a.getClass());
        System.arraycopy(toArray(), 0, a, 0, size());
        if (a.length > size()) {
            a[size()] = null;
        }
        return a;
    }

    @Override
    public Object[] toArray() {
        if (reversed) {
            Object[] res = new Object[size()];
            for (int i = 0; i < size(); i++) {
                res[i] = list.get(size() - i - 1);
            }
            return res;
        }
        return list.toArray();
    }


    //===============unsupported=========
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public String toString() {
        return "ArraySet{" +
                "list=" + list +
                ", reversed=" + reversed +
                ", comparator=" + comparator +
                '}';
    }


    public static void main(String[] args) {
        ArraySet<Integer> set = new ArraySet<>(List.of(10, 20, 30, 40, 50), Integer::compareUnsigned);
        NavigableSet<Integer> dSet = set.descendingSet();
        //System.out.println(dSet.headSet(30));
        System.out.println(dSet.tailSet(41));
        System.out.println(dSet);
    }
}
