(ns server.server
    "This namespace contains functions for a simple server that 
    receives instructions."
    (:use util.dbread util.dbwrite util.dbget util.csutils
          config.csconfig))

    (use 'clojure.contrib.duck-streams)
    
(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter InputStreamReader BufferedReader))
        

;---------------DEFS
;;Each time a client connects, it sends an id
;;The server stores the id to send updates
(def clientlist (ref #{}))

"Defines the file that will be readed as a database"
(def database       (read-bin-file filename))

;---------------FUNCTIONS
(defn myagent-action
"Defines an agent's action"
  [_ socket content]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output content)))
    
(defn whaaat
  "Performs an action depending on the message received by the client"
  [[sender perf content]]
  ;;If client is new, add it to list
  (dosync (alter clientlist conj sender))
  ;(println "Clients: " @clientlist)
  ;;act 
  (cond (= perf (get performatives :update)) 
          ((println "Updating...")
          (println "seaking")
          (println "Update done."))
      (= perf (get performatives :delete)) 
          ((println "Deleting...")
          ())
      (= perf (get performatives :add)) 
          ((println "Adding new row...")
          ()
          (println "Row added."))
      (= perf (get performatives :refresh)) 
          ((println "Refreshing...")
          ()
          (println "Refreshing done."))
      (= perf (get performatives :commit)) 
          ((println "Committing...")
          ()
          (println "Commit done."))
      true (println (str "Performative is \"" perf "\"??? I DUNNO WTF TO DO..."))))
    
;-------------MAIN SERVER FUNCTION
(defn serve
"Initializes the server"
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
(serve)
