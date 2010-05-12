(ns config.interfaceconfig)

;;;;;DEFS
(def testfilename   "./db/db-1x2.db")
(def windowSX       800)
(def windowSY       600)
(def labelH         25)
(def btnSX          150)
(def btnSY          30)
(def topY           80)
(def defaultboxh    25)
(def tableSX        (- windowSX (+ 30 btnSX)))
(def tableSY        (- windowSY labelH topY))
