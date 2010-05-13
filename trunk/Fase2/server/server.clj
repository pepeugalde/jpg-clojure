(ns server.server
    "This namespace contains functions for a simple server that 
    receives instructions."
    (:use util.dbread util.dbwrite util.dbget util.csutils
          config.csconfig))

    (use 'clojure.contrib.duck-streams)
    
(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter );InputStreamReader BufferedReader)
        )
        

;---------------DEFS
;;Each time a client connects, it sends an id
;;The server stores the id to send updates
(def clientlist [])

;---------------FUNCTIONS
(defn myagent-action
  [_ socket content]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output content)
    ))

(defn hear
  [input]
  (let msg (loop [recmsg ""]
            (let [c (.read input)]
              (if (not= c -1)
                  (recur (str recmsg (str(char c))))
                  recmsg))))
    
(defn whaaat
  [message]
  (let [instruction (apply str(take 3 message))]
  (cond (= instruction "upd") ((println "Updating...")
                               (println "seaking")
                               (println "Update done."))
        (= instruction "del") ((println "Deleting...")
                               ())
        (= instruction "add") ((println "Adding new row...")
                               ()
                               (println "Row added."))
        (= instruction "rfr") ((println "Refreshing...")
                               ()
                               (println "Refreshing done."))
        (= instruction "cmt") ((println "Committing...")
                               ()
                               (println "Commit done."))
        true (println (str "\"" instruction "\"??? I DUNNO WTF TO DO...")))))
    
;-------------MAIN SERVER FUNCTION
(defn serve
  []
  (let [content      (slurp filename)
        server       (ServerSocket. *port*)
        myagent      (agent nil)]
        
    (loop []
      (println "\nWaiting for connection...")      
      (let [socket (.accept server)]
        (println "Connection accepted")
        ;(println "agent before " @myagent)
        ;(send-off myagent myagent-action socket content)
        ;(println "agent after " @myagent)
        
        ;---HEARING LOOP
        (with-open [input  (BufferedReader. (InputStreamReader. (.getInputStream socket)))
                    output (PrintWriter. (.getOutputStream socket))]
        
          (whaaat (hear input))

        )
        (recur)))))

;-----------TEST
;(serve)
