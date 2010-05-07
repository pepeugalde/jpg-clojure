;;; Ejemplo de uso de sockets. 
;;; Script cliente.

(use 'clojure.contrib.duck-streams)
(:use util.interface)

(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter))

(def *port* 3003)
(def *host* "localhost")

(defn peticiona
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
