(ns util.csutils
    "This namespace contains functions used by clients and servers."
     (:use config.csconfig))

;(import '(java.io PrintWriter InputStreamReader BufferedReader))
    
(defn say
  "Print a string containing sender ID , business and content 
  through the output."
  [output sender perf content]
  (println "Saying: \"" (apply str sender perf content) "\"")
  (.print output (apply str sender perf content)))
    
; (defn hear
  ; "Read a string from the input."
  ; [input]
  ; (loop [recmsg ""]
    ; (let [c (.read input)]
      ; (if (not= c -1)
          ; (recur (str recmsg (str(char c))))
          ; recmsg))))

(defn hear
  "Waits for a message sent by client or server.
  Returns a vector containing sender, performative and content"
  [input]
  (let [wholemsg  (loop [recmsg ""]
                    (let [c (.read input)]
                      (if (not= c -1)
                          (recur (str recmsg (str(char c))))
                          recmsg)))
        sender    (apply str (take IDlength wholemsg))
        perf      (apply str (take perflength (drop IDlength wholemsg)))
        content   (apply str (drop (+ IDlength perflength) wholemsg))]
        
    (println "Heard message from: " sender " Performative: " perf)
    (println "Content: " content)
    
    [sender perf content]
    ))

;----TEST
