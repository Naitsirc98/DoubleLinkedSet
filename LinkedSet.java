import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;



/**
 * A LinkedSet object implements the functionalities of the Set interface
 * 
 * It does not allow repeated elements nor null values, and does not have order
 * 
 * @author Cristian Herrera^2
 * @version 16/2/2018 
 */
public class LinkedSet<E> implements Set<E> {
    
    private Node front;
    private Node rear;
    private int size;
    
    public LinkedSet() {
	front = rear = null;
	size = 0;
    }
    
    public LinkedSet(Collection<? extends E> other) {
	this();
	addAll(other);
    }
 
    @Override
    public boolean add(E e) {
	
	final boolean found = !contains(e);
        
        if(found) {
            if(size == 0) {
        	front = new Node(e, null);
        	rear = front;
            } else {
        	final Node node = new Node(e, front);
                front = node;
            }
            size++;
        }
        return found;
    }

    @Override
    public boolean addAll(Collection<? extends E> other) {
	
	boolean changed = false;
	
	for(E e : other) {
	    changed |= add(e);
	}
	return changed;
    }

    @Override
    public void clear() {
	front = rear = null;
	size = 0;
    }

    @Override
    public boolean contains(Object obj) {
	
	Node a = front;
	Node z = rear;
	
	final int limit = (size & 1) == 0 ? size >>> 1 : (size+1) >>> 1;
	 
	for(int i = 0;i < limit;i++,a=a.next,z=z.prev) {
	    
	    if(a.equals(obj) || z.equals(obj)) {
		return true;
	    }
	}
	
	return false;
    }

    @Override
    public boolean containsAll(Collection<?> other) {
	
	for(Object e : other) {
	    if(!contains(e))
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
	return new LinkedSetIterator();
    }

    @Override
    public boolean remove(Object obj) {
	
	Node a = front;
	Node z = rear;
	
	final int limit = (size & 1) == 0 ? size >>> 1 : (size+1) >>> 1;
	
	for(int i = 0;i < limit;i++,a=a.next,z=z.prev) {
	    
	    if(a.equals(obj)) {
		if(a == front) {
		    front = a.next;
		} else {
		    a.prev.next = a.next;
		}
		size--;
		return true;
	    } else if(z.equals(obj)) {
		if(z == rear) {
		    rear = z.prev;
		} else {
		    z.next.prev = z.prev;
		}
		size--;
		return true;
	    }
	    
	    
	}
	return false;
    }

    @Override
    public boolean removeAll(Collection<?> other) {
	
	boolean changed = false;
	
	for(Object e : other) {
	    changed |= remove(e);
	}
	
	return changed;
	
    }

    @Override
    public boolean retainAll(Collection<?> other) {
	boolean changed = false;
	
	for(Object e : this) {
	    if(!other.contains(e)) {
		changed |= remove(e);
	    }
	}
	return changed;
	
    }

    @Override
    public int size() {
	return size;
    }

    @Override
    public Object[] toArray() {
	
	Object[] result = new Object[size];
        
        Node a = front;
        Node z = rear;
        
        final int limit = (size & 1) == 0 ? size >>> 1 : (size+1) >>> 1; 
        
        for(int i = 0;i < limit;i++) {
            result[i] = a.data;
            result[size-1-i] = z.data;
            a = a.next;
            z = z.prev;
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] array) {
	
	if(array.length == size) {
	    return (T[]) Arrays.copyOf(toArray(), size);
	}
	
        return (T[]) toArray();
    }
    
    /**
     * A basic iterator over the elements of the set. The elements are returned like in
     * a Stack (First Input Last Output) 
     * 
     * */
    private class LinkedSetIterator implements Iterator<E> {
	
	private Node node = new Node(null, front);

	@Override
	public boolean hasNext() {
	    node = node.next;
	    return node != null;
	}

	@Override
	public E next() {
	    final E next = node.data;
	    return next;
	}
	
	@Override
	public void remove() {
	    LinkedSet.this.remove(node);
	}
	
    }
 
    /**
     * A Node is basically a 'wrap' for the actual data. Each Node has 2 pointers, which permit
     * go through the structure in both forward and backward 
     * 
     * */
     private class Node {
	
	final E data;
	Node next;
	Node prev;

	Node(E data, Node next) {
	    this.data = data;
	    this.next = next;
	    if(next!=null)next.prev = this;
 	}
	
	@Override
 	public boolean equals(Object other) {
	    
	    if(other == null || other.getClass() != data.getClass())
		return false;
	    
	    return data.hashCode() == other.hashCode();
 	}
	
	// This is only to debug
	@Override
	public String toString() {
	    return (prev==null?"null":prev.data) 
		    + " <- " +data.toString() + " -> "
		    + (next==null?"null":next.data);
 	}
	
    }

}
