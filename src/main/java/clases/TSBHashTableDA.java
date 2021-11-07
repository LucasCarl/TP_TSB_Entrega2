package clases;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class TSBHashTableDA<K,V> implements Map<K,V>, Cloneable, Serializable {

    // Tamaño máximo que podrá tener el arreglo.
    private final static int MAX_SIZE = Integer.MAX_VALUE;
    
    // Atributos privados.
    
    // la tabla hash: el arreglo que contiene las casillas con Entries.
    private Entry<K,V> table[];
    
    // el tamaño inicial de la tabla (tamaño con el que fue creada).
    private int initial_capacity;
    
    // la cantidad de objetos que contiene el array de entries...
    private int count;
    
    // el factor de carga para calcular si hace falta un rehashing...
    private float load_factor;
	
    
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;
    protected transient int modCount;
    
    // Constructores
	public TSBHashTableDA() {
		this(11, 0.8f);
	}
    
	public TSBHashTableDA(int initial_capacity, float load_factor) {
        if(load_factor <= 0 || load_factor >= 1) { load_factor = 0.8f; }
        if(initial_capacity <= 0) { initial_capacity = 11; }
        else
        {
            if(initial_capacity > TSBHashTableDA.MAX_SIZE)
            {
                initial_capacity = TSBHashTableDA.MAX_SIZE;
            }
        }
        
        this.table = new Entry[initial_capacity];
        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
	}

	public TSBHashTableDA(int initial_capacity) {
		this(initial_capacity, 0.8f);
	}

//************************ Implementación de métodos especificados por Map.
    
    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     * @return la cantidad de elementos de la tabla.
     */
    @Override
    public int size() 
    {
        return this.count;
    }

    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     * @return true si la tabla está vacía.
     */
    @Override
    public boolean isEmpty() 
    {
        return (this.count == 0);
    }

    /**
     * Determina si la clave key está en la tabla.
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */
    @Override
    public boolean containsKey(Object key) 
    {
        return (this.get((K)key) != null);
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a contains().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */    
    @Override
    public boolean containsValue(Object value)
    {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null
     * si la tabla no contiene ningún objeto asociado a esa clave.
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o 
     *         null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException si la clase de key no es compatible con la 
     *         tabla.
     */
    @Override
    public V get(Object key) 
    {
       if(key == null) throw new NullPointerException("get(): parámetro null");
       
       int indice = this.h(key.hashCode());
       int indiceAux = 0; 
       indiceAux = indice;
       
       // Busqueda cuadratica de entry. Se busca hasta encontrar un equals o casilla null.
       Boolean encontrado = false;
       Integer base = 1;
       while(encontrado == false) {
    	   Entry<K, V> bucket = this.table[indiceAux];    	
    	   
    	   if(bucket == null || bucket.getBorrado()) return null; // No se encontró nada en esa posición.
    	   
    	   if(key.equals(bucket.getKey())) { // Se encontró un objeto y comparamos si es el que buscamos.
    		   encontrado = true;
    		   return bucket.getValue();
    	   }
    	   else {
    		   // No se encuentra y se cambia indice de busqueda.
    		   indiceAux = (int) (indice + Math.pow(base++, 2));
    		   
    		   // Buscamos indice si excede el tama�o.
    		   while(indiceAux >= this.table.length) {
    			   indiceAux -= this.table.length;
    		   }
    	   }
       }
       return null;
    }

    /**
     * Asocia el valor (value) con la clave (key) en esta tabla.
     * Si la tabla contenía previamente un valor asociado para la
     * clave, entonces el valor anterior es reemplazado por el nuevo
     * @param key la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya 
     *         estaba asociada con alguno, o null si la clave no estaba antes 
     *         asociada a ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value) 
    {
       if(key == null || value == null) throw new NullPointerException("put(): parámetro null");
       
       int indice = this.h(key.hashCode());
       int indiceAux = 0; 
       indiceAux = indice;
       
       // Busqueda cuadratica de lugar vacio. Se busca hasta encontrar casilla vacia.
       Boolean encontrado = false;
       Integer base = 1;
       while(encontrado == false) {
    	   Entry<K, V> bucket = this.table[indiceAux];    	
    	   
    	   if(bucket == null || bucket.getBorrado()) {
    		   Entry<K, V> old = bucket;
    		   this.table[indiceAux] = new Entry<K, V> (key, value);
    		   this.count++;
    		   return old != null ? old.getValue() : null;
    	   }
    	   
    	   if(key.equals(bucket.getKey())) { // Se encontró un objeto y comparamos si es el que buscamos.
    		   encontrado = true;
               Entry<K, V> old = bucket;
               this.table[indiceAux] = new Entry<K, V> (key, value);
    		   return old.getValue();
    	   }
    	   else {
    		   // No se encuentra y se cambia indice de busqueda.
    		   indiceAux = (int) (indice + Math.pow(base++, 2));
    		   
    		   // Buscamos indice si excede el tama�o.
    		   while(indiceAux >= this.table.length) {
    			   indiceAux -= this.table.length;
    		   }
    	   }
       }
       
       // Validamos ocupación de la tabla.
       float porc = this.count / this.table.length;
       if(porc >= this.load_factor) {
    	   this.rehash();
       }

       return null;
    }

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).  
     * El método no hace nada si la clave no está en la tabla.
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     *         estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */
    @Override
    public V remove(Object key) 
    {
       if(key == null) throw new NullPointerException("remove(): parámetro null");
       
       int indice = this.h(key.hashCode());
       int indiceAux = 0; 
       indiceAux = indice;
       
       // Busqueda cuadratica de entry. Se busca hasta encontrar un equals o casilla null.
       Boolean encontrado = false;
       Integer base = 1;
       while(encontrado == false) {
    	   Entry<K, V> bucket = this.table[indiceAux];    	
    	   
    	   if(bucket == null || bucket.getBorrado()) return null; // No se encontro nada en esa posicion.
    	   
    	   if(key.equals(bucket.getKey())) { // Se encontro un objeto y comparamos si es el que buscamos.
    		   encontrado = true;
    		   bucket.setBorrado(true);
    		   this.count--;
    		   this.modCount++;
    		   return bucket.getValue();
    	   }
    	   else {
    		   // No se encuentra y se cambia indice de busqueda.
    		   indiceAux = (int) (indice + Math.pow(base++, 2));
    		   
    		   // Buscamos indice si excede el tamaño.
    		   while(indiceAux >= this.table.length) {
    			   indiceAux -= this.table.length;
    		   }
    	   }
       }
       return null;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla
     * asociados a las mismas claves (si se repitiese alguna).
     * @param m el map cuyos objetos serán copiados en esta tabla.
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) 
    {
        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina todo el contenido de la tabla.
     * El arreglo vuelve a tener el tamaño con el que se creo.
     */
    @Override
    public void clear() 
    {
        this.table = new Entry[this.initial_capacity];
        this.count = 0;
        this.modCount++;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa.
     * @return Set a modo de vista de todas las claves mapeadas en la tabla.
     */
    @Override
    public Set<K> keySet() 
    {
        if(keySet == null) 
        {
            keySet = new KeySet();
        }
        return keySet;  
    }
        
    /**
     * Retorna una Collection a modo de vista de todos los valores
     * contenidos en la tabla. La coleccion esta respaldada por la tabla,
     * por lo que los cambios realizados en la tabla seran reflejados en
     * la coleccion, y viceversa.
     * @return Collection a modo de vista de todas los valores mapeados en la tabla.
     */
    @Override
    public Collection<V> values() 
    {
        if(values==null)
        {
            values = new ValueCollection();
        }
        return values;    
    }

    /**
     * Retorna un Set a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto esta respaldado por la tabla, por lo
     * que los cambios realizados en la tabla seran reflejados en el conjunto, y
     * viceversa.
     * @return Set a modo de vista de todos los objetos mapeados en la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() 
    {
        if(entrySet == null) 
        {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    
    //************************ Redefinicion de metodos heredados desde Object.
    
    /**
     * Retorna una copia superficial de la tabla.
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.    
     */ 
    
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        TSBHashTableDA<K, V> t = (TSBHashTableDA<K, V>)super.clone();
        t.table = new Entry[table.length];
        for (int i = table.length ; i-- > 0 ; ) 
        {
            t.table[i] = this.table[i];
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj) 
    {
        if(!(obj instanceof Map)) { return false; }
        
        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try 
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();
            while(i.hasNext()) 
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null) { return false; }
                else 
                {
                    if(!value.equals(t.get(key))) { return false; }
                }
            }
        } 
        
        catch (ClassCastException | NullPointerException e) 
        {
            return false;
        }

        return true;    
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode() 
    {
        //calcula un numero dependiendo de los elementos en la tabla
        int c = 1;
        if(size() > 0)
        {
            for (Entry<K, V> bucket : this.table)
            {
                if(bucket != null && !bucket.getBorrado())
                {
                    c += bucket.hashCode();
                }
            }
        }
        //calcula hashcode
        int hc = (this.table.length + size()) * (c);
        return hc;
    }
    
    /**
     * Devuelve el contenido de la tabla en forma de String.
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString() 
    {
        StringBuilder cad = new StringBuilder("");
        for(int i = 0; i < this.table.length; i++)
        {
            cad.append(":\n\t").append(this.table[i].toString());
        }
        return cad.toString();
    }
    
    
    //************************ Metodos especificos de la clase.

    /**
     * Determina si el value ingresado tiene una clave asociada.
     * Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave esta asociada efectivamente a ese value.
     */
    public boolean contains(Object value)
    {
        if(value == null) return false;

        for(Entry<K, V> bucket : this.table)
        {
            if(bucket != null && !bucket.getBorrado())
            {
                if(value.equals(bucket.getValue())) return true;
            }
        }
        return false;
    }
    
    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca
     * automaticamente cuando se detecta que la cantidad ocupada de la lista
     * supera el factor de carga ingresado
     */
    protected void rehash()
    {
        int old_length = this.table.length;

        int new_length = old_length * 2 + 1;
        
        // no permitir que la tabla tenga un tamaño mayor al limite maximo.
        if(new_length > TSBHashTableDA.MAX_SIZE)
        { 
            new_length = TSBHashTableDA.MAX_SIZE;
        }

        // Nuevo arreglo con new_length.
        Entry<K, V> temp[] = new Entry[new_length];

        this.modCount++;  
       
        // Recorrer el viejo arreglo y redistribuir los objetos que tenia.
        for(int i = 0; i < this.table.length; i++)
        {
        	if(this.table[i] != null && !this.table[i].getBorrado()) {
                // Obtener su nuevo valor hash
                K key = this.table[i].getKey();
                int indice = this.h(key, temp.length);

                temp[indice] = table[i];
        	}
        }
       
        // Cambiar la referencia table para que apunte a temp.
        this.table = temp;
    }

    //************************ Metodos Hash.
    
    /*
     * Toma una clave entera k y calcula y retorna un indice
     * valido para esa clave para entrar en la tabla.
     */
    private int h(int k)
    {
        return h(k, this.table.length);
    }
    
    /*
     * Toma un objeto key que representa una clave y calcula y
     * retorna un indice valido para esa clave para entrar en la tabla.
     */
    private int h(K key)
    {
        return h(key.hashCode(), this.table.length);
    }
    
    /*
     * Toma un objeto key que representa una clave y un tama�o de
     * tabla t, y calcula y retorna un indice valido para esa clave dedo ese
     * tamaño.
     */
    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }
    
    /*
     * Toma una clave entera k y un tamaño de tabla t, y calcula y
     * retorna un indice valido para esa clave dado ese tamaño.
     */
    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;        
    }
      
    //************************ Clases Internas.
    
    /*
     * Clase interna que representa los pares de objetos que se almacenan en la tabla hash.
     * Lanzara una IllegalArgumentException si alguno de los dos parametros es null.
     */
    private class Entry<K, V> implements Map.Entry<K, V>, Cloneable
    {
        private K key;
        private V value;
        private Boolean borrado;
        
        public Entry(K key, V value) 
        {
            if(key == null || value == null)
            {
                throw new IllegalArgumentException("Entry(): par�metro null...");
            }
            this.key = key;
            this.value = value;
            this.borrado = false;
        }
        
        @Override
        public K getKey() 
        {
            return key;
        }

        @Override
        public V getValue() 
        {
            return value;
        }

        @Override
        public V setValue(V value) 
        {
            if(value == null) 
            {
                throw new IllegalArgumentException("setValue(): parametro null...");
            }
                
            V old = this.value;
            this.value = value;
            return old;
        }
       
        public Boolean getBorrado() {
			return borrado;
		}

		public void setBorrado(Boolean borrado) {
			this.borrado = borrado;
		}

		@Override
        public int hashCode() 
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);            
            return hash;
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }
            
            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) { return false; }
            if (!Objects.equals(this.value, other.value)) { return false; }            
            return true;
        }       
        
        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la tabla.
     * La vista es stateless y sus metodos gestionan en forma directa el contenido de la tabla.
     */
    private class KeySet extends AbstractSet<K> 
    {
        @Override
        public Iterator<K> iterator() 
        {
            return new KeySetIterator();
        }
        
        @Override
        public int size() 
        {
            return TSBHashTableDA.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSBHashTableDA.this.containsKey(o);
        }
        
        @Override
        public boolean remove(Object o) 
        {
            return (TSBHashTableDA.this.remove(o) != null);
        }
        
        @Override
        public void clear() 
        {
            TSBHashTableDA.this.clear();
        }
        
        private class KeySetIterator implements Iterator<K>
        {
            // indice de la entry actual
            private int current_entry;
            // indice de la entry anterior (si se requiere en remove())
            private int last_bucket;
            // flag para controlar si remove() esta bien invocado
            private boolean next_ok;
            // el valor que deberia tener el modCount de la tabla completa
            private int expected_modCount;

            //Crea un iterador comenzando en la primera entry. Activa el mecanismo fail-fast.
            public KeySetIterator()
            {
                current_entry = 0;
                last_bucket = 0;
                next_ok = false;
                expected_modCount = TSBHashTableDA.this.modCount;
            }

            //Determina si hay al menos un elemento en la tabla que no haya sido retornado por next().
            @Override
            public boolean hasNext() 
            {
                //Variable auxiliar t para simplificar accesos
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                //Comprueba si la tabla esta vacia
                if(TSBHashTableDA.this.isEmpty()) { return false; }

                if(current_entry >= t.length - 1){ return false; }
                else {
                    int next_entry = current_entry + 1;
                    while(next_entry < t.length && (t[next_entry] == null || t[next_entry].getBorrado()))
                    {
                        next_entry++;
                    }
                    if(next_entry >= t.length)  return false; 
                }
                return true;
            }

            //Retorna el siguiente elemento disponible en la tabla.
            @Override
            public K next() 
            {
                //Control
                if(TSBHashTableDA.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificaci�n inesperada de tabla...");
                }
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                
                //Variable auxiliar t para simplificar accesos
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                //Avanza hasta encontrar el next entry
                int next_entry = current_entry + 1;
                while((t[next_entry] == null || t[next_entry].getBorrado()))
                {
                    next_entry++;
                }
                
                this.current_entry = next_entry;
                next_ok = true;
                
                return t[next_entry].getKey();
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posicion anterior al que fue removido. El elemento removido es el
             * que fue retornado la ultima vez que se invoco a next(). El metodo
             * solo puede ser invocado una vez por cada invocacion a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                
                //Eliminar el objeto que retorna next() la ultima vez.
                TSBHashTableDA.this.table[current_entry] = null;

                //Quedar apuntando al anterior al que se retorna.
                if(last_bucket != current_entry)
                {
                    current_entry--;
                }

                // avisar que el remove() valido para next() ya se activo.
                next_ok = false;

                TSBHashTableDA.this.count--;
                TSBHashTableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }

    /*
     * Clase interna que representa una vista de todos los Pares mapeados en la tabla.
     * La vista es stateless y sus metodos gestionan en forma directa el contenido de la tabla.
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> 
    {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() 
        {
            return new EntrySetIterator();
        }

        @Override
        public boolean contains(Object o) 
        {
            if(o == null) { return false; }
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K,V>)o;
            V value = entry.getValue();

            if(TSBHashTableDA.this.contains(value)) { return true; }
            return false;
        }

        @Override
        public boolean remove(Object o) 
        {
            if(o == null) { throw new NullPointerException("remove(): par�metro null");}
            if(!(o instanceof Entry)) { return false; }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            
            return (TSBHashTableDA.this.remove(key) != null);
        }

        @Override
        public int size() 
        {
            return TSBHashTableDA.this.count;
        }

        @Override
        public void clear() 
        {
            TSBHashTableDA.this.clear();
        }
        
        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            // Indice de la lista actualmente recorrida
            private int current_bucket;
            
            // Indice de la lista anterior (si se requiere en remove())
            private int last_bucket;
                        
            // Indice del elemento actual en el iterador (el que fue retornado
            // la ultima vez por next() y sera eliminado por remove())
            private int current_entry;
            // flag para controlar si remove() esta bien invocado
            private boolean next_ok;
            // Valor que deberia tener el modCount de la tabla completa
            private int expected_modCount;
            
            /*
             * Crea un iterador comenzando en la primera lista. Activa el 
             * mecanismo fail-fast.
             */
            public EntrySetIterator()
            {
                current_bucket = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSBHashTableDA.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya 
             * sido retornado por next(). 
             */
            @Override
            public boolean hasNext() 
            {
                //Variable auxiliar t para simplificar accesos
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                //Comprueba si la tabla esta vacia
                if(TSBHashTableDA.this.isEmpty()) { return false; }

                if(current_entry >= t.length - 1){ return false; }
                else {
                    int next_entry = current_entry + 1;
                    while(next_entry < t.length && (t[next_entry] == null || t[next_entry].getBorrado()))
                    {
                        next_entry++;
                    }
                    if(next_entry >= t.length)  return false;
                }
                return true;
            }

            //Retorna el siguiente elemento disponible en la tabla.
            @Override
            public Entry<K, V> next()
            {
                // control: fail-fast iterator...
                if(TSBHashTableDA.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificaci�n inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t para simplificar accesos...
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                int next_entry = current_entry + 1;
                while((t[next_entry] == null || t[next_entry].getBorrado()))
                {
                    next_entry++;
                }

                this.current_entry = next_entry;
                next_ok = true;

                return t[next_entry];
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posicion anterior al que fue removido. El elemento removido es el
             * que fue retornado la ultima vez que se invoco a next(). El metodo
             * solo puede ser invocado una vez por cada invocacion a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }

                // Eliminar el objeto que retorna next() la ultima vez.
                TSBHashTableDA.this.table[current_entry] = null;

                // Quedar apuntando al anterior al que se retorno.
                if(last_bucket != current_entry)
                {
                    current_entry--;
                }

                // avisar que el remove() valido para next() ya se activo.
                next_ok = false;

                // la tabla tiene un elemento menos.
                TSBHashTableDA.this.count--;

                // fail_fast iterator: todo en orden.
                TSBHashTableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }    

    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la tabla.
     * La vista es stateless y sus metodos gestionan en forma directa el contenido de la tabla.
     */
    private class ValueCollection extends AbstractCollection<V> 
    {
        @Override
        public Iterator<V> iterator() 
        {
            return new ValueCollectionIterator();
        }
        
        @Override
        public int size() 
        {
            return TSBHashTableDA.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSBHashTableDA.this.containsValue(o);
        }
        
        @Override
        public void clear() 
        {
            TSBHashTableDA.this.clear();
        }
        
        private class ValueCollectionIterator implements Iterator<V>
        {
            // Indice de la lista actualmente recorrida
            private int current_bucket;
            
            // Indice de la lista anterior (si se requiere en remove())
            private int last_bucket;
                        
            // Indice del elemento actual en el iterador (el que fue retornado
            // la ultima vez por next() y sera eliminado por remove())
            private int current_entry;
                        
            // flag para controlar si remove() esta bien invocado
            private boolean next_ok;
            
            // el valor que deberia tener el modCount de la tabla completa
            private int expected_modCount;

            // Crea un iterador comenzando en la primera lista. Activa el mecanismo fail-fast.
            public ValueCollectionIterator()
            {
                current_bucket = 0; 
                last_bucket = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSBHashTableDA.this.modCount;
            }

            // Determina si hay al menos un elemento en la tabla que no haya sido retornado por next().
            @Override
            public boolean hasNext() 
            {
                //Variable auxiliar t para simplificar accesos
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                //Comprueba si la tabla esta vacia
                if(TSBHashTableDA.this.isEmpty()) { return false; }

                if(current_entry >= t.length - 1){ return false; }
                else {
                    int next_entry = current_entry + 1;
                    while(next_entry < t.length && (t[next_entry] == null || t[next_entry].getBorrado()))
                    {
                        next_entry++;
                    }
                    if(next_entry >= t.length)  return false;
                }
                return true;
            }

            // Retorna el siguiente elemento disponible en la tabla.
            @Override
            public V next() 
            {
                // control: fail-fast iterator...
                if(TSBHashTableDA.this.modCount != expected_modCount)
                {    
                    throw new ConcurrentModificationException("next(): modificaci�n inesperada de tabla...");
                }
                
                if(!hasNext()) 
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t para simplificar accesos...
                Entry<K, V> t[] = TSBHashTableDA.this.table;

                int next_entry = current_entry + 1;
                while((t[next_entry] == null || t[next_entry].getBorrado()))
                {
                    next_entry++;
                }

                this.current_entry = next_entry;
                next_ok = true;

                return t[next_entry].getValue();
            }
            
            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posicion anterior al que fue removido. El elemento removido es el
             * que fue retornado la ultima vez que se invoco a next(). El metodo
             * solo puede ser invocado una vez por cada invocacion a next().
             */
            @Override
            public void remove() 
            {
                if(!next_ok) 
                { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }

                // Eliminar el objeto que retorna next() la ultima vez.
                TSBHashTableDA.this.table[current_entry] = null;

                // Quedar apuntando al anterior al que se retorno.
                if(last_bucket != current_entry)
                {
                    current_entry--;
                }

                // Avisar que el remove() valido para next() ya se activo.
                next_ok = false;

                TSBHashTableDA.this.count--;

                TSBHashTableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }
}
