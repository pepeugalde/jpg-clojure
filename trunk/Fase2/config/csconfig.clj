(ns config.csconfig
(:require clojure.contrib.gen-html-docs))

;;;;;DEFS
"Defines the default port that will be used"
;Default port to be used
(def *port*   3003)

;Default host to be used
"Defines the host that will be used"
(def *host*   "localhost")

;Default database file names to be used
"Defines database file names to be used"
(def sfilename "./db/db-1x2.db")
(def cfilename "./db/db-1x2 - copia.db")

;Default ID length
"Defines the default ID length"
(def IDlength 10)

;Default server ID
"Defines server ID"
(def serverID "SERVER----")

;Default performative length
"Defines the default performative length"
(def perflength 3)

;Performative's vector
"Defines the performatives"
(def performatives {:ok "ok!", :no "no!", :outdated "out", :hi "hi!", :update "upd", :delete "del", :add "add", :refresh "rfr"})
