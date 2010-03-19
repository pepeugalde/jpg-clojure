(ns methods
    "This namespace contains functions that demonstrate how to read a binary 
    file. It specifically allows reading the URLyBird database file."  
    (:import (java.io FileOutputStream DataOutputStream PrintStream BufferedWriter FileWriter PrintWriter)))

(defn set-pad
  "Sets the padding for a new row"
  [data padding]
  (let [missing (- padding (.length (str data)))
  	databytes (concat (str data) (apply str(repeat missing " ")))]
  (.getBytes (str databytes))
  )
)
 	
(defn new-row
"Inserts new registers into the database"
 [file-name info]
 (with-open [printer (FileOutputStream. file-name false)]
   (let [data (for [[data padding] info] (set-pad data padding))]
     (.write printer (firstdata))
    )
  )
)

(new-row "lol.txt" [["lol" 6] ["ja" 10] ["k" 8]])

;(string-from-byte-sequence (set-pad "lol" 6))