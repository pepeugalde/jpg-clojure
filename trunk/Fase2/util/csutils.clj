(ns util.csutils
    "This namespace contains functions used by clients and servers."
     (:use config.csconfig))
     
(defn say
  "Print a string containing sender ID , performative and content 
  through the output."
  [output sender perf content]
  (println "SENDING:  " perf)
  (.print output (apply str sender perf content  "\n"))
  (.flush output))

(defn hear
  "Waits for a message sent by client or server.
  Returns a vector containing sender, performative and content"
  [input]
   (let [wholemsg  (.readLine input)
        sender    (apply str (take IDlength wholemsg))
        perf      (apply str (take perflength (drop IDlength wholemsg)))
        content   (apply str (drop (+ IDlength perflength) wholemsg))
        ]
    (println "RECEIVED: " perf)
    
    [sender perf content]))

;----TEST
