(ns dbwrite
    "This namespace contains functions that demonstrate how to read a binary 
    file. It specifically allows reading the URLyBird database file."  
    (:import (java.io FileOutputStream DataOutputStream PrintStream BufferedWriter FileWriter PrintWriter  
                    FileInputStream DataInputStream))
    (:use dbread dbwrite dbsearch))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn read-str-len2
  "Read the next n bytes from file and create a string."
  [file n]
  (let [barray (byte-array n)]
    (.readFully file barray)
    (String. barray)))
         
;-------------------------------------------------------------------------------
(defn read-fields-meta2
  "Reads the meta-data of all the fields from the given file. You must specify 
  the total number of fields."
  [file num-fields]
  (loop [n num-fields
         resultado []]
    (if (= n 0)
        resultado
        (let [name-len   (.readShort file)
              field-name (read-str-len file name-len)
              field-len  (.readShort file)]
             (recur (dec n) (conj resultado [field-name field-len]))))))                                     

;-------------------------------------------------------------------------------
(defn read-one-record2
  "Reads one individual record from the given file. You must specify a vector 
  with the fields' meta-data."
  [file fields]
  (loop 
    [result {:deleted (not (zero? (.readShort file)))}
     [[fname fsize] & rest-fields] fields]
     (if fname
         (recur (assoc result (keyword fname) (.trim (read-str-len file fsize)))
                 rest-fields)
         result)))          

;-------------------------------------------------------------------------------
(defn read-records2
  "Reads all the individual records of the file."
  [file fields]
  (loop 
    [result []]
    (if (zero? (.available file))
        result
        (recur (conj result (read-one-record file fields))))))
    
;-------------------------------------------------------------------------------
(defn read-bin-file2
  "Reads a binary file and returns a vector with its information."
  [file-name]
  (with-open [file (DataInputStream. (FileInputStream. file-name))]
    (let [magic-number (.readInt file)
          offset       (.readInt file)
          num-fields   (.readShort file)
          fields-meta  (read-fields-meta file num-fields)
          records      (read-records file fields-meta)]
      {:magic magic-number :offset offset :num-fields num-fields 
       :fields fields-meta :records records})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;-------------------------------------------------------------------------------
(defn set-pad
  "Sets the padding for a new row"
  [data numbytes]
  (let [missing (- numbytes (.length (str data)))
  	databytes (concat (str data) (apply str(repeat missing " ")))]
  (.getBytes (str databytes))
  )
)
;-------------------------------------------------------------------------------
(defn new-row
"Inserts new registers into the database"
 [file-name info]
 (with-open [printer (FileOutputStream. file-name false)]
   (let [data (for [[data padding] info] (set-pad data padding))]
     (.write printer (first data))
     (println (first data))
    )
  )
)
;-------------------------------------------------------------------------------
;;;;;;;;;;TEST
(new-row "lol.txt" [["lol" 6] ["ja" 10] ["k" 8]])

;(string-from-byte-sequence (set-pad "lol" 6))
