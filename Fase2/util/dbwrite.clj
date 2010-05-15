(ns util.dbwrite
    "This namespace contains functions that can overwrite or replace parts of 
    a binary file. It specifically allows reading the URLyBird database file."  
    (:import (java.io FileOutputStream DataOutputStream PrintStream 
                      BufferedWriter FileWriter PrintWriter
                      FileInputStream DataInputStream RandomAccessFile))
    (:use util.dbread))
    (use 'clojure.contrib.duck-streams)

;-------------------------------------------------------------------------------
(defn str-from-b-seq 
  "Transforms a byte array into string"
  [coll]
  (reduce str (map char coll)))
;-------------------------------------------------------------------------------
(defn set-pad
  "Sets the padding for a new row"
  [data numbytes]
  (let [missing     (- numbytes (.length (str data)))
        paddeddata   (concat data (apply str(repeat missing " ")))]
    (.getBytes (apply str paddeddata))))
;-------------------------------------------------------------------------------
(defn write-new-row
  "Inserts new registers into the database"
  [filename infos sizes]
  (with-open [printer (FileOutputStream. filename true)]  
    (.write printer 
            (byte-array [(byte 0x0000)(byte 0x0000)]))
    (loop [info infos   size sizes]
      (if (empty? info)
        ()
        (do (.write printer 
                    (set-pad (first info) (first size)))
            (recur (rest info) (rest size)))))
    (.flush printer))
  (println "ROW ADDED"))

;-------------------------------------------------------------------------------
(defn write-empty-row
  "Inserts new registers into the database"
  [filename sizes]
  (let [totalsize (apply + sizes)
  	   emptystring (apply str (repeat totalsize " "))]
    (with-open [printer (FileOutputStream. filename true)]  
      (.write printer
              (byte-array [(byte 0x0000)(byte 0x0000)]))
      (.write printer  (.getBytes emptystring))
      (.flush printer)))
  (println "ROW ADDED"))
  
;-------------------------------------------------------------------------------
(defn set-del-flag
  "Sets to 0x8000 the :deleted flag"
  [filename offset]
  (with-open [writer  (RandomAccessFile. filename "rw")] 
    (.skipBytes writer 
                offset)
    (.write writer (byte-array [(byte 0x80)(byte 0x00)]) 
                   0 
                   2)))
                   
;-------------------------------------------------------------------------------
(defn delete-record-skip-deleted
  "Deletes a record by changing its deleted flag, doesn't consider deleted records"
  [filename delrow offset rowlen]
  (with-open [reader  (DataInputStream.  (FileInputStream.  filename))]  
    (.skipBytes reader 
                offset)
    (loop [valrow 0 realrow 0]
      (if (= 0 (.readShort reader))
          (do (if (= valrow delrow)
                  (set-del-flag filename 
                                (+ offset (* realrow (+ 2 rowlen))))
                  (do (.skipBytes reader 
                                  rowlen)
                      (recur (inc valrow) (inc realrow)))))
          (do (.skipBytes reader 
                          rowlen)
              (recur valrow (inc realrow))))))
  (println "ROW DELETED"))
;-------------------------------------------------------------------------------
(defn delete-record
  "Deletes a record by changing its deleted flag, counts deleted records"
  [filename delrow offset rowlen]
  (with-open [reader  (DataInputStream.  (FileInputStream.  filename))]  
    (.skipBytes reader offset)
    (loop [realrow 0]
        (if (= realrow delrow)
            (set-del-flag filename (+ offset (* realrow (+ 2 rowlen))))
            (do (.skipBytes reader 
                            rowlen)
                (recur (inc realrow))))))
  (println "ROW DELETED"))
;-------------------------------------------------------------------------------
(defn overwrite-row
  "Overwrites a record beginning at offset"
  [filename offset infos sizes]
  (with-open [writer  (RandomAccessFile. filename "rw")] 
    (.skipBytes writer 
                offset)
    (.write writer (byte-array [(byte 0x00)(byte 0x00)]) 
                   0 
                   2)
    (loop [info infos   size sizes]
      (if (empty? info)
        ()
        (do (.write writer
                    (set-pad (first info) (first size)))
            (recur (rest info) (rest size)))))))
;-------------------------------------------------------------------------------
(defn update-record-skip-deleted
  "Updates a record by overwriting content, doesn't consider deleted records"
  [filename newrowinfo rowsizes updrow offset]
  (with-open [reader  (DataInputStream.  (FileInputStream.  filename))]  
    (.skipBytes reader 
                offset)
    (loop [valrow 0 realrow 0]
      (if (= 0 (.readShort reader))
          (do (if (= valrow updrow)
                  (overwrite-row filename 
                              (+ offset (* realrow (+ 2 (apply + rowsizes))))
                              newrowinfo
                              rowsizes)
                  (do (.skipBytes reader 
                                  (apply + rowsizes))
                      (recur (inc valrow) (inc realrow)))))
          (do (.skipBytes reader 
                          (apply + rowsizes))
              (recur valrow (inc realrow))))))
  (println "ROW UPDATED"))
;-------------------------------------------------------------------------------
(defn rewrite-file
  "Inserts new registers into the database"
  [filename offset newdata]
    (let [header (apply str (take offset (slurp filename)))]
      (spit filename (apply str header newdata))))
   