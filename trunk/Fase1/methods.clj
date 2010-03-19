(defn set-pad
  "Sets the padding for a new row"
  [data padding]
  (let [missing (- padding (.length (str data)))
  	databytes (concat (str data) (* missing " "))
  	 
  	]
  (.getBytes databytes)
  ))
 	
(defn new-row
"Inserts new registers into the database"
 [file-name info]
 (with-open [file (DataOutputStream. (FileOutputStream. file-name))])
   (let [data (for [[data padding] info] (set-pad data padding))
              
        ]
   println(data)
    ))
 	

