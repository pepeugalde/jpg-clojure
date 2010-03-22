(ns dbwrite
    "This namespace contains functions that demonstrate how to read a binary 
    file. It specifically allows reading the URLyBird database file."  
    (:import (java.io FileOutputStream DataOutputStream PrintStream BufferedWriter FileWriter PrintWriter  
                    FileInputStream DataInputStream RandomAccessFile))
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
    (.getBytes (apply str paddeddata))
  )
)
;-------------------------------------------------------------------------------
(defn write-new-row
  "Inserts new registers into the database"
  [file-name infos sizes]
  (with-open [printer (FileOutputStream. file-name true)]  
    (.write printer (byte-array [(byte 0x0000)(byte 0x0000)]))
    (loop [info infos size sizes]
      (if (empty? info)
        ()
        (do (.write printer (set-pad (first info) (first size)))
            (recur (rest info) (rest size))
        )
      )
    )
    (.flush printer)
  )
  (println "ROW ADDED")
)
;-------------------------------------------------------------------------------
(defn set-del-flag2
  "Sets to 0x8000 the :deleted flag"
  [file-name offset]
  (with-open [writer  (DataOutputStream. (FileOutputStream. file-name true))] 
    (.write writer (byte-array [(byte 0x80)(byte 0x00)]) 0 2)
    (.flush writer)
  )
)
(defn set-del-flag
  "Sets to 0x8000 the :deleted flag"
  [file-name offset]
  (with-open [writer  (RandomAccessFile. file-name "rw")] 
    (.skipBytes writer offset)
    (.write writer (byte-array [(byte 0x80)(byte 0x00)]) 0 2);offset 2)
  )
)
;-------------------------------------------------------------------------------
(defn delete-record
  "Deletes a record by changing its flag"
  [file-name delrow offset rowlen]
  (with-open [reader  (DataInputStream.  (FileInputStream.  file-name))]  
    (.skipBytes reader offset)
    (loop [valrow 0 realrow 0]
      (if (= 0 (.readShort reader))
          (do (println "row " valrow " existe, se cuenta")
              (if (= valrow delrow)
              ;(if (.contains delrows valrow)
                  (do(println "se sobreescribe en byte " (+ offset (* realrow (+ 2 rowlen))))
                     (println "valrow " valrow " realrow " realrow)
                     ;(println (char (.readByte reader)))
                     ;(println (char (.readByte reader)))
                     ;(println (char (.readByte reader)))
                     ;(println (char (.readByte reader)))
                     (set-del-flag file-name (+ offset (* realrow (+ 2 rowlen))))

                     ;(.skipBytes reader rowlen)
                     ;(recur (inc valrow) (inc realrow))
                  )
                  (do (.skipBytes reader rowlen)
                      (recur (inc valrow) (inc realrow)))
              )
          )
          (do (println "row " valrow " borrada, no cuenta")
              (.skipBytes reader rowlen)
              (recur valrow (inc realrow))
          )
      )
    )
  )
  (println "ROW DELETED")
)
;-------------------------------------------------------------------------------
;;;;;;;;;;TEST
;(str-from-b-seq (byte-array [(byte 0x72) (byte 0x75) (byte 0x78)]));(set-pad "lol" 6))
;(str-from-b-seq (set-pad "wat" 4))
;(write-new-row "lol - copia.db" ["name" "location" "size" "rate"] [64 64 4 8])
;(delete-record "lol - copia.db" 0 74 159)
;(set-pad "wat" 4)