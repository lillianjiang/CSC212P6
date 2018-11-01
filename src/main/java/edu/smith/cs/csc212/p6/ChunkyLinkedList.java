package edu.smith.cs.csc212.p6;

import edu.smith.cs.csc212.p6.errors.BadIndexError;
import edu.smith.cs.csc212.p6.errors.EmptyListError;

/**
 * This is a data structure that has an array inside each node of a Linked List.
 * Therefore, we only make new nodes when they are full. Some remove operations
 * may be easier if you allow "chunks" to be partially filled.
 * 
 * @author jfoley
 * @param <T>
 *            - the type of item stored in the list.
 */
public class ChunkyLinkedList<T> implements P6List<T> {
	private int chunkSize;
	private SinglyLinkedList<FixedSizeList<T>> chunks;

	public ChunkyLinkedList(int chunkSize) {
		this.chunkSize = chunkSize;
		chunks = new SinglyLinkedList<>();
		chunks.addBack(new FixedSizeList<T>(chunkSize));
	}

	//O(n^3)
	@Override
	public T removeFront() {
		if (this.isEmpty()) {
			throw new EmptyListError();
		} else {
			return removeIndex(0);
		}
	}

	//O(n^3)
	@Override
	public T removeBack() {
		if (this.isEmpty()) {
			throw new EmptyListError();
		} else {
			return removeIndex(size());
		}
	}

	//O(n^3)
	@Override
	public T removeIndex(int index) {
		if (this.isEmpty()) {
			throw new EmptyListError();
		} else {
			T removed = getIndex(index); //O(n)
			int start = 0;
			for (FixedSizeList<T> chunk : this.chunks) { //O(n)
				int end = start + chunk.size();
				if (start <= index && index < end) {
					chunk.removeIndex(index - start); //O(n)
				}
				start = end;
			}
			return removed;
		}
	}

	//O(n^3)
	@Override
	public void addFront(T item) {
		addIndex(item, 0);
	}

	//O(n^3)
	@Override
	public void addBack(T item) {
		addIndex(item, size());
	}

	//O(n^3)
	@Override
	public void addIndex(T item, int index) {
		int start = 0;
		for (FixedSizeList<T> chunk : this.chunks) { //O(n)
			int end = start + chunk.size();
			if (start <= index && index < end) {
				if (chunk.size() < chunkSize) {
					chunk.addIndex(item, index % chunkSize); //O(n)	
					
				} else {
					FixedSizeList<T> newchunk = new FixedSizeList<>(chunkSize);
					newchunk.addFront(item); //O(1)
					chunks.addIndex(newchunk, (index / chunkSize) + 1); //O(n)
				}
			}
			start = end;
		}
	}

	//O(1)
	@Override
	public T getFront() {
		return this.chunks.getFront().getFront();
		//return getIndex(0); 
		//0(n)
	}

	//O(1)
	@Override
	public T getBack() {
		return this.chunks.getBack().getBack();
		//return getIndex(size()); 
		//O(n)
	}

	//O(n)
	@Override
	public T getIndex(int index) {
		if (this.isEmpty()) {
			throw new EmptyListError();
		}
			int start = 0;
			for (FixedSizeList<T> chunk : this.chunks) { //O(n)
				// calculate bounds of this chunk.
				int end = start + chunk.size();
	
				// Check whether the index should be in this chunk:
				if (start <= index && index < end) {
					return chunk.getIndex(index - start); //O(1)
				}
	
				// update bounds of next chunk.
				start = end;
		}
			throw new BadIndexError();
	}

	//O(n)
	@Override
	public int size() {
		int total = 0;
		for (FixedSizeList<T> chunk : this.chunks) {
			total += chunk.size();
		}
		return total;
	}

	//O(1)
	@Override
	public boolean isEmpty() {
		return this.chunks.isEmpty();
	}
}
