import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;


/**
 * A sorted set that uses an internal array to store its elements. It provides all the methods
 * declared within the SortedSet interface, and also methods to control how much the collection
 * can grow when it needs to be resized, how its elements are compared and direct access to them,
 * and 2 iterators to go through the set either forward or backward. It does not allow null elements.
 * By default, the hash code of the elements is used to sort them within the ArraySet.
 * 
 * @author Cristian Daniel Herrera Herrera
 * 
 * @version 17/02/2018
 * 
 */
public class ArraySet<E> implements SortedSet<E> {

    /** FIELDS */
    
    // Internal array to store the elements
    private Object[] data; 
    // Capacity of the set. This determines how much the set grows each time it has to be resized
    private int capacity = 10; 
    // Number of non-null elements in this set
    private int size = 0;
    // Comparator that performs the sortering job in this set
    private Comparator<Object> comparator = new HashComparator();
    
    /** END OF FIELDS */
    
    
    
    /**
     * Default constructor that initializes the ArraySet with capacity = 10
     * 
     * */
    public ArraySet() {
	data = new Object[capacity];
    }
    
    /**
     * Constructs a new ArraySet with the specified capacity. Capacity must be greater than zero
     * 
     * */
    public ArraySet(final int capacity) {
	setCapacity(capacity);
	data = new Object[capacity];
    }
    
    /**
     * Constructs a new ArraySet with the specified comparator.
     * 
     * */
    public ArraySet(Comparator<Object> comparator) {
	this();
	setComparator(comparator);
    }
    
    /**
     * Returns an ArraySet with all the elements of the given array.
     * 
     * */
    public ArraySet(final E[] data) {
	this(Arrays.asList(data));
    }
    
    /**
     * Returns an ArraySet with all the elements of the given collection
     * */
    public ArraySet(final Collection<? extends E> other) {
	setCapacity(other.size());
	data = new Object[capacity];
	addAll(other);
    }

    @Override
    public boolean add(E e) {
	
	// Only resize when it is really needed
	
	if(size == 0) {
	    if(size == data.length)
		resize(capacity);
	    data[size++] = e;
	    return true;
	}
	
	// Appends e to the end of this set
	if(comparator.compare(data[size-1], e) < 0) {
	    if(size == data.length)
		resize(capacity);
	    data[size++] = e;
	    return true;
	}
	
	// index at which e is within the set, or should be inserted
	int index = 0;
	
	if(comparator.compare(e, data[0]) >= 0) {
	
	    index = indexOf(e);
	
	    // e is already in this set
	    if(index >= 0) {
		return false; 
	    }
	
	    // now index is the position where e have to be
	    index = -(index+1);
	}
	
	if(size == data.length) 
	    resize(capacity);

	/* We have to increment the position of all the elements in range [index, size] */
	
	Object tmp = e;
	
	for(int i = index;i < size+1;i++) {
	    final Object tmp2 = data[i];
	    data[i] = tmp;
	    tmp = tmp2;
	}
	
	++size;
	
	return true;
    }
    
   /** Resizes the internal array. The new array length will be old_length + capacity.
    * This method is called automatically when more space is required. If an unusual number of elements
    * are going to be added and it is not wanted to change the capacity of the set for some reason, this method
    * could be used as a "preparation" for the ArraySet to perform the add operations without resizing 
    * frequently
    */
    public void resize(int capacity) {  
	if(capacity <= 0)
	    throw new IllegalArgumentException("Capacity must be > 0");
	data = Arrays.copyOf(data, data.length+capacity);
    }

    @Override
    public boolean addAll(Collection<? extends E> other) {
	boolean changed = false;
	
	for(E e : other) {
	    changed |= add(e);
	}
	
	return changed;
    }
    
    /**
     * This method sets the size to zero and set all its elements to null,
     * which basically means that this set has no elements.
     * However, the internal array will keep its length and the limit of this ArraySet
     * will not change. This will avoid the ArraySet to be resized in the future until it
     * reaches again this limit. If a complete reset of limit and internal array is desired,
     * consider using free method
     * 
     * */
    @Override
    public void clear() {
	Arrays.fill(data, 0, size, null);
	size = 0;
    }
    
    /**
     * This method clears the ArraySet and resets the internal array so that all its elements
     * are destroyed, and the limit is set to its capacity. This method is preferred to clear when 
     * free the memory is required or when this ArraySet will not grow as much as before
     */
    public void free() {
	clear();
	data = new Object[capacity];
    }
    
    @Override
    public int size() {
	return size;
    }
    
    /**
     * Returns the size at which this ArraySet will need to be resized again
     * 
     * @return current limit of this ArraySet
     * */
    public int limit() {
	return data.length;
    }
    
    /**
     * Returns the capacity of this ArraySet. The capacity is the length added to the internal
     * array when a resize is required
     * 
     * @return the capacity of this ArraySet
     * 
     * */
    public int capacity() {
	return capacity;
    }
    
    
    /**
     * Sets a new capacity for this ArraySet. The capacity is the length added to the internal
     * array when a resize is required
     * 
     * The capacity must be greater than zero
     * 
     * */
    public void setCapacity(int capacity) {
	if(capacity <= 0) {
	    throw new IllegalArgumentException("Capacity must be > 0");
	}
	this.capacity = capacity;
    }

    /**
     * Returns the index of the given object if it is present within the ArraySet. Otherwise, this method
     * will give the position + 1 at which this object should be if it were inside the set as a negative number
     * It uses the binary search method but before perform the searching it checks the range at which
     *  the object could be
     * 
     * @param {@link Object} the object in question
     * 
     * @return {@code int} the index of the object passed, or the
     * 
     * */
    public int indexOf(Object e) {
	    
	if(comparator.compare(e, data[size-1]) > 0) {
		return -size-1;
	}
	if(comparator.compare(e, data[0]) < 0) {
		return -1;
	}
	    
	int low = 0;
	int high = size >>> 1;
		
	if(comparator.compare(e, data[high]) > 0) {
	    low = high;
	    high = size;
	}
		
	return binarySearch(low, high, e);
    }
    
    private int binarySearch(int low, int high, Object e) {
	
	while(low <= high) {
	    
	    final int mid = (low+high)>>>1;
	    final Object midValue = data[mid];
	    
	    if(comparator.compare(midValue, e) < 0) {
		low = mid + 1;
	    } else if(comparator.compare(midValue, e) > 0) {
		high = mid - 1;
	    } else {
		return mid;
	    }
	    
	}
	
	return -low-1;
    }

    @Override
    public boolean contains(Object obj) {
	return indexOf(obj) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> other) {
	
	for(Object obj : other) {
	    if(!contains(obj))
		return false;
	}
	
	return true;
    }

    @Override
    public boolean isEmpty() {
	return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
	return new ForwardIterator();
    }
    
    /**
     * Returns an iterator that iterates this set in reversed order
     * 
     * @return a reverse iterator over the ArraySet
     * 
     * */
    public Iterator<E> reversedIterator() {
	return new BackwardIterator();
    }
    
    /**
     * Returns the element at the specified position
     * This is an advantage of this set, it allows direct access to its elements
     * 
     * @param index where the element is
     * 
     * @return the element at given position
     * 
     * @throws <code> ArrayIndexOutOfBoundException </code> if the position is out of range
     * 
     */
    public E get(int i) {
	
	if(i < 0 || i >= size)
	    throw new ArrayIndexOutOfBoundsException(i + " is out of range [0,"+size+")");
	
	return (E) data[i];
    }
    
    /**
     * Returns the element at the specified position and removes it from the ArraySet
     * This is an advantage of this set, it allows direct access to its elements
     * 
     * @param index where the element is
     * 
     * @return the element at given position
     * 
     * @throws <code> ArrayIndexOutOfBoundException </code> if the position is out of range
     * 
     */
    public E extract(int i) {
	final E element = get(i);
	remove(i);
	return element;
    }

    @Override
    public boolean remove(Object e) {
	
	if(size == 0)
	    return false;

	// If e is not in range, it is not stored in this set
	if(comparator.compare(e, data[0]) < 0 || comparator.compare(e, data[size-1]) > 0) {
	    return false;
	}
	
	
	final int index = indexOf(e);
	
	// e is not in this set
	if(index < 0) {
	    return false;
	}
	
	remove(index);
	
	return true;
    }
    
    /** 
    * To remove any element, e, we just have to decrement the position of the elements from last index to the
    * e's position, so that finally e will be substituted by its next element
    *
    * @param {@code int} the index of the element about to remove 
    * 
    * @throws {@code ArrayIndexOutOfBoundsException} if the index is out of range
    *
    */ 
    public void remove(final int index) {
	    
	if(index < 0 || index > size)
		throw new ArrayIndexOutOfBoundsException(index+" is out of range [0, "+size+"]");
	
	Object tmp = data[size-1];
	data[size-1] = null;
	
	for(int i = size-2;i >= index;i--) {
	    final Object tmp2 = data[i];
	    data[i] = tmp;
	    tmp = tmp2;
	}
	
	--size;
    }

    @Override
    public boolean removeAll(Collection<?> other) {

	boolean changed = false;
	
	for(Object obj : other) {
	    changed |= remove(obj);
	}
	
	return changed;
    }

    @Override
    public boolean retainAll(Collection<?> other) {
	boolean changed = false;
	
	for(Object obj : this) {
	    if(!other.contains(obj)) {
		remove(obj);
		changed = true;
	    }
		
	}
	
	return changed;
    }

    /**
     * Returns the first element that meets the condition
     * 
     * @param {@link Predicate} the condition to be tested
     * 
     *  @return {@code E} the first element that meets the condition, or null if any of them does
     *  
     * */
    public E getAny(Predicate<E> condition) {
	for(int i = 0;i < size;i++) {
	    final E e = (E) data[i];
	    if(condition.test(e))
		return e;
	}
	return null;
    }
    
    /**
     * Returns the last element that meets the condition
     * 
     * @param {@link Predicate} the condition to be tested
     * 
     *  @return {@code E} the last element that meets the condition, or null if any of them do
     *  
     * */
    public E getLast(Predicate<E> condition) {
	for(int i = size-1;i >= 0;i--) {
	    final E e = (E) data[i];
	    if(condition.test(e))
		return e;
	}
	return null;
    }
    
    /**
     * Returns an ArraySet that contains all the elements that meet the condition
     * 
     * @param {@link Predicate} the condition to be tested
     * 
     *  @return {@code ArraySet<E>} an ArraySet containing all the elements that meet the condition,
     *   or an empty ArraySet if any of them do
     *  
     * */
    public ArraySet<E> getAll(Predicate<E> condition) {
	
	ArraySet<E> set = new ArraySet<>(size);
	
	for(int i = 0;i < size;i++) {
	    final E e = (E) data[i];
	    if(condition.test(e))
		set.add(e);
	}
	
	set.trim();
	
	return set;
	
    }
    /**
     * Only retain those elements that meet the condition passed as argument. In other words, remove all elements
     * that do not meet the condition  
     * 
     * @param {@link Predicate} the condition to be tested
     * 
     * @return {@code boolean} true if this set has changed after this method, false otherwise
     * 
     * */
    public boolean retainIf(Predicate<E> condition) {
	
	boolean changed = false;
	
	for(int i = 0;i < size;i++) {
	    final E e = (E) data[i];
	    if(!condition.test(e)) {
		changed = true;
		remove(i);
	    }
		
	}
	return changed;
    }
    
    /**
     * Trims the ArraySet to the minimum size that it can hold all its elements, so that size = limit
     * 
     * */
    public void trim() {
	data = Arrays.copyOf(data,size);
    }

    @Override
    public Object[] toArray() {
	return Arrays.copyOf(data, size);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
	return (T[]) Arrays.copyOfRange(data, 0, size, a.getClass());
    }

    @Override
    public Comparator<? super E> comparator() {
	return comparator;
    }
   
    /**
     * Reverse the order of this ArraySet by reversing its comparator 
     * 
     * */
    public void reverse() {
	comparator = comparator.reversed();
	Arrays.sort(data, 0, size, comparator);
    }
    
    /**
     * Sets a new comparator for this ArraySet, and then sort its elements by its criteria.
     * 
     * @param {@link Comparator} the new comparator
     * 
     * @throws {@link NullPointerException} if the new comparator is null
     * 
     * */
    public void setComparator(Comparator<? super Object> comparator) {
	if(comparator == null)
	    throw new NullPointerException("Comparator cannot be null!");
	this.comparator = comparator;
	Arrays.sort(data, 0, size, comparator);
    }

    @Override
    public E first() {
	if(size == 0)
	    return null;
	return (E) data[0];
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
	int to = indexOf(toElement);
	
	if(to < 0 || to > size) {
	    throw new IllegalArgumentException(toElement + " is not an element of this set");
	}
	
	ArraySet<E> set = new ArraySet(Arrays.copyOfRange(data, 0, to));
	
	return set;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E last() {
	if(size == 0)
	    return null;
	return (E) data[size-1];
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
	
	int from = indexOf(fromElement);
	int to = indexOf(toElement);
	
	if(from > to || from < 0 || to < 0 || from > size || to > size) {
	    throw new IllegalArgumentException();
	}
	
	ArraySet<E> set = new ArraySet(Arrays.copyOfRange(data, from, to));
	
	return set;
	
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
	
	int from = indexOf(fromElement);
	
	if(from < 0 || from > size) {
	    throw new IllegalArgumentException();
	}
	
	ArraySet<E> set = new ArraySet(Arrays.copyOfRange(data, from, size));
	
	return set;
    }
    
    @Override
    public boolean equals(Object obj) {
	
	if(!(obj instanceof SortedSet<?>)) {
	    return false;
	}
	
	SortedSet<?> other = (SortedSet<?>) obj;
	
	if(other.size() != size)
	    return false;
	
	final Iterator<?> it = other.iterator();
	
	for(int i = 0;i < size;i++) {
	    if(!data[i].equals(it.next())) {
		return false;
	    }
	}
	
	return true;
    }
    
    @Override
    public int hashCode() {
	final int prime = 83;
	int result = 1;
	
	for(int i = 0;i < size;i++) {
	    result *= prime * data[i].hashCode();
	}
	return result;
    }
    
    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder("ArraySet[");
	
	builder.append("size="+size);
	builder.append(",capacity="+capacity);
	builder.append(",limit="+limit());
	builder.append("] => ");
	builder.append(Arrays.toString(toArray()));
	
	return builder.toString();
	
    }
    
    // Comparator that compare 2 objects comparing their hashcodes
    private static class HashComparator implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
	    return Integer.compare(o1.hashCode(), o2.hashCode());
	}
    }
    
   
    private class ForwardIterator implements Iterator<E> {
	
	private int index = 0;
	private Object current;
	
	@Override
	public boolean hasNext() {
	    return index < size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
	    current = data[index++];
	    return (E) current;
	}
	
	@Override
	public void remove() {
	    if(current == null) {
		throw new IllegalStateException("¡The iterator has no elements to remove!");
	    }
	    ArraySet.this.remove(current);
	    current = null;
	}
	
    }
    
    private class BackwardIterator implements Iterator<E> {
	
	private int index = size-1;
	private Object current;
	
	@Override
	public boolean hasNext() {
	    return index >= 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
	    current = data[index--];
	    return (E) current;
	}
	
	@Override
	public void remove() {
	    if(current == null) {
		throw new IllegalStateException("¡The iterator has no elements to remove!");
	    }
	    ArraySet.this.remove(current);
	    current = null;
	}
	
    }



}
