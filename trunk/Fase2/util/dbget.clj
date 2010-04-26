(ns util.dbget
    "This namespace contains functions that get specific values from a database.")
;-------------------------------------------------------------------------------
(defn get-magic
    "Returns magic number from a database"
    [db]
    (get db :magic))
;-------------------------------------------------------------------------------
(defn get-offset
    "Returns offset from a database"
    [db]
    (get db :offset))  
;-------------------------------------------------------------------------------
(defn get-num-fields
    "Returns number of fields from a database"
    [db]
    (get db :num-fields))
;-------------------------------------------------------------------------------
(defn get-fields
    "Returns fields from a database"
    [db]
    (get db :fields))  
;-------------------------------------------------------------------------------
(defn get-records 
    "Returns records from a database"
    [db]
    (get db :records))
;-------------------------------------------------------------------------------
(defn get-col-names
	"Returns a vector containing column names in CAPS LOCK (FOR CRUISE CONTROL)"
	[db]
	(vec (for [[coln _] (get db :fields)] (.toUpperCase coln))))
;-------------------------------------------------------------------------------
(defn get-field-lengths
	"Returns a vector containing field lengths"
	[db]
	(vec (for [[_ len] (get db :fields)] len)))
;-------------------------------------------------------------------------------
(defn trim-value
  "Returns a trimmed value"
  [val len]
  (apply str (take len val)))
;-------------------------------------------------------------------------------
(defn get-trimmed-values
  "Makes sure the values are of a given length or shorter"
  [values lengths]
  (loop [val values   len lengths   result []]
      (if (empty? val)
        result
        (recur (rest val) (rest len) (conj result (trim-value (first val) (first len)))))))
;-------------------------------------------------------------------------------
(defn fliplr
  "Flips a tuple horizontally"
  [tuple left right exleftval]
  (aset tuple left (aget tuple right))
  (aset tuple right exleftval)
  tuple)
;-------------------------------------------------------------------------------
(defn records-to-array
  "Transforms record dictionary into a 2d array, keeps keywords."
  [records numfields]
  (let [records2d (to-array-2d records)]
    (loop [i 0]
        (if (< i (count records))
            (do   (loop [lelem 0  relem (- numfields 1)] 
                    (if (< lelem relem)
                        (do (aset records2d i (fliplr (aget records2d i) lelem relem (aget records2d i lelem)))
                            (recur (inc lelem) (dec relem)))
                        ()))
                   (recur (inc i)))))
    records2d))
;-------------------------------------------------------------------------------
(defn get-record2d-values
  "Returns 2d array containing only values."
  [records2d]
  (loop [i 0]
      (if (< i (alength records2d))
          (do   (loop [j 0] 
                  (if (< j (alength (aget records2d i)))
                      (do (aset records2d i j (second (aget records2d i j)))
                          (recur (inc j)))
                      ()))
                 (recur (inc i)))))
  records2d)
;-------------------------------------------------------------------------------
(defn filter-non-deleted
  "Receives records and returns only non deleted records"
  [records]
  (for [tuple records :when (= false (:deleted tuple))] tuple))
;-------------------------------------------------------------------------------
;TEST