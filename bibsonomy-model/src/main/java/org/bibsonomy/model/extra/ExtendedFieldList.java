package org.bibsonomy.model.extra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author philipp
 * @version $Id$
 */
public class ExtendedFieldList {
    
    private static class TupleList extends ArrayList<ExtendedField> {

		private static final long serialVersionUID = 8239869762782414009L;
		private final Map<String,List<String>> metadata;
        
        public TupleList(Map<String, List<String>> metadata) {
            super();
            this.metadata = metadata;
        }

        @Override
        public boolean add(ExtendedField e) {
            final String key = e.getKey();
            if (metadata.containsKey(key)) {
                    metadata.get(key).add(e.getValue());
            } else {
                    final LinkedList<String> list = new LinkedList<String>();
                    list.add(e.getValue());
                    metadata.put(key, list);
            }
            return true;
        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean addAll(Collection<? extends ExtendedField> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            metadata.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof ExtendedField) {
            		ExtendedField tuple = (ExtendedField) o;
                    return metadata.containsKey(tuple.getKey());    
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Iterator<ExtendedField> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }  
    }
    
    private final Map<String,List<String>> metadata = new HashMap<String, List<String>>();
    
    private final TupleList extendedFieldsList = new TupleList(metadata);
    
    /**
     * @return metadata
     */
    public Map<String, List<String>> getMetaData() {
    	return metadata;
    }
    
    /**
     * @return extendedFields list
     */
    public Collection<ExtendedField> getExtendedFieldsList() {
        return extendedFieldsList;
    }
    
    /**
     * necessary for ibatis injection
     * 
     * @param a
     */
    public void setExtendedFieldsList(Object a) {
    }
}
