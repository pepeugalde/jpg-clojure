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

(defn str-from-b-seq [coll] (reduce str (map char coll)))

;-------------------------------------------------------------------------------
(defn set-pad
  "Sets the padding for a new row"
  [data numbytes]
  (let [missing     (- numbytes (.length (str data)))
        paddeddata   (concat data (apply str(repeat missing " ")))]
    ;(println (str "----------"))
    ;(println (str "recibo: " data " de " numbytes))
    ;(println (str "missing: " missing))
    ;(println (str "length: " data " " (.length (str data))))
    ;(println (str "paddeddata: " (str"."(apply str paddeddata) ".")))
    ;(println (str "string: " (str-from-b-seq (.getBytes (apply str paddeddata)))))
    ;(println (str "__________"))
    (.getBytes (apply str paddeddata))
  )
)
;-------------------------------------------------------------------------------
(defn new-row
  "Inserts new registers into the database"
  [file-name info]
  (with-open [printer (FileOutputStream. file-name true)]
    (let [padded (for [[data numbytes] info] (set-pad data numbytes))]
      (doall (for [p padded] (.write printer  p)))
      (.flush printer)
    )
  )
)
;-------------------------------------------------------------------------------
;;;;;;;;;;TEST
(defn str-from-b-seq [coll] (reduce str (map char coll)))
;(str-from-b-seq (byte-array [(byte 0x72) (byte 0x75) (byte 0x78)]));(set-pad "lol" 6))
;(str-from-b-seq (set-pad "wat" 4))

(new-row "lol.txt" [["name" 64] ["location" 64] ["size" 4] ["rate" 8]])
;(set-pad "wat" 4)