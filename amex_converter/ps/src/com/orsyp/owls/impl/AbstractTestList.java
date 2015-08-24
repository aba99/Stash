/*
 * AbstractTestList.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */

package com.orsyp.owls.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.orsyp.SyntaxException;
import com.orsyp.UniverseException;
import com.orsyp.api.Item;
import com.orsyp.api.ItemList;
import com.orsyp.api.security.Operation;

/**
 * Base class for list tests.
 *
 * @author jjt
 * @version $Revision: 1.2 $
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractTestList<I extends Item>
        extends AbstractTest {

    protected abstract ItemList<I> initList() throws SyntaxException;

    protected abstract void print(I item);


    protected void test() {
        test(true);
    }


    protected void test(boolean content) {

        long duration = 0;
        try {
            ItemList<I> list = initList();
            list.setContext(this.makeContext());
            duration = -System.currentTimeMillis();
            list.extract(getOperation());
            duration += System.currentTimeMillis();
            print(list, content, duration);
        } catch (UniverseException e) {
            e.printStackTrace(System.out);
        }
    }

    protected Operation getOperation() {
        return Operation.DISPLAY;
    }

    private void print(final ItemList<I> list, boolean content, long duration) {
    	final int count = list.getCount();
    	class It implements Iterator<I> {
    		private int pos = 0;
    		
			public boolean hasNext() { 
				return pos<count; 
			}
			
			public I next() {
				if( !hasNext()) {
					throw new NoSuchElementException();
				} else try {
					return list.get(pos);
				} finally {
					pos++;
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
    	
        println();
        
        if (count > 0) {
            if (content) {
            	print(new Iterable<I>() {
					public Iterator<I> iterator() {
						return new It();
					}
            	}, count);
//                for (int i = 0; i < list.getCount(); i++) {
//                    I item = list.get(i);
//                    if (item != null) {
//                        print(item);
//                    }
//                }
            }
            printf("\n%d item(s) - %d ms.\n", count, duration);
        } else {
            printf("list empty - %d ms.\n", duration);
        }
    }
    
    protected void print(Iterable<I> list, int count) {
        for( I item : list ) {
        	if (item != null) print(item);
        }
    }

}
