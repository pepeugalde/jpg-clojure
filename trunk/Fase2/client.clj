(use 'clojure.contrib.duck-streams)
(:use util.interface)

(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter))

(defn petition
  []
  (let [socket (Socket. *host* *port*)]
    (with-open [input (.getInputStream socket)
                output (.getOutputStream socket)]
      (loop []
        (let [c (.read input)]
          (when (not= c -1)
            (print (char c))
            (recur)))))))

(interface "cliente")
(peticiona)        
