(ns util.csutils
    "This namespace contains functions used by clients and servers."
     (:use config.csconfig))


(defn say
  "Print a string containing sender ID , performative and content 
  through the output."
  [output sender perf content]
  (println "Saying: \"" sender "\" Perf: \""perf "\"");content "\"")
  (.print output (apply str sender perf content  "\n"))
  (.flush output))

(defn hear
  "Waits for a message sent by client or server.
  Returns a vector containing sender, performative and content"
  [input]
   (let [wholemsg  (.readLine input);(loop [recmsg ""]
                    ; (let [c (.read input)]
                      ; (print (char c))
                      ; (if (not= c "\n")
                          ; (recur (str recmsg (str(char c))))
                          ; recmsg)))
        sender    (apply str (take IDlength wholemsg))
        perf      (apply str (take perflength (drop IDlength wholemsg)))
        content   (apply str (drop (+ IDlength perflength) wholemsg))
        ]
    ;(println "whole: " wholemsg)
    (println "Heard message from: " sender " Performative: " perf)
    ;(println "Content: " content)
    
    [sender perf content]))

;----TEST
