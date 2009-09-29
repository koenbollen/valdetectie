package com.tigam.valdetectie.utils;


/**
 * This is a simple array that i'll cycle the beginnen when the end is
 * reached. When calling the {@link #insert} method with full list, the
 * oldest element will be overwritten.
 *
 * @since September 23, 2009
 * @author Koen Bollen <meneer koenbollen nl>
 */
public class CyclicArray<E>
{
	
	private final int capacity;
	private final Object[] data;
	private int size;
	private int pointer;


	/**
	 * Create a CyclicArray with a given capacity.
	 *
	 * @param capacity The maximum size of the array.
	 */
	public CyclicArray( int capacity )
	{
		this.capacity = capacity;
		this.data = new Object[this.capacity];
		this.size = 0;
		this.pointer = 0;
	}


	/**
	 * Add an element to the array, if the array was full the oldest
	 * element is overwritten but returned.
	 *
	 * @param element The new element to add.
	 * @return The removed/overwritten element or null.
	 */
	public synchronized E insert( E element )
	{
		@SuppressWarnings("unchecked")
		E backup = (E)this.data[this.pointer];
		
		this.data[this.pointer] = element;
		this.pointer++;
		this.pointer %= this.capacity;
		this.size = Math.min( this.capacity, this.size+1 );
		return backup;
	}

	/**
	 * Retrieve an element from the array at the given index.
	 *
	 * @param index The index from where to get the element.
	 * @return The element requested.
	 */
	
	public synchronized E get( int index )
	{
		//System.out.printf( "Got index %d with pointer %d: ", index, this.pointer );
		index += this.pointer * (this.size/this.capacity);
		index %= this.capacity;
		
		@SuppressWarnings("unchecked")
		E result = (E)this.data[index];
		
		return result;
	}


	/**
	 * Returns the maximum capacity of this array.
	 * @return The capacity.
	 */
	public int capacity()
	{
		return this.capacity;
	}

	/**
	 * Returns the current size of this array.
	 * @return The size.
	 */
	public int size()
	{
		return this.size;
	}


	/**
	 * A test and example of this class.
	 * @deprecated
	 */
	public static void main( String ... args )
		throws Exception
	{
		CyclicArray<Integer> f = new CyclicArray<Integer>(10);

		for( int x = 0; x < 25; x++ )
		{
			Integer backup = f.insert( x*10 );
			for( int i = 0; i < f.size(); i++ )
				System.out.printf( "%4d ", f.get(i) );
			if( backup != null )
				System.out.printf( "(dumped value: %4d)\n", backup );
			else
				System.out.println();
			Thread.sleep(10);
		}

	}

}

