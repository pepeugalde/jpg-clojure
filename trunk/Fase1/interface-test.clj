(ns interface-test
	(:use clojure.test)
	(:use interface)
)

(deftest records-to-array-test
 ({:pollito 1 :gallina 2} records-to-array)
)

(deftest get-col-names-test
	({:nombre 9 :edad 23 :carrera 12} get-col-names)
)

(deftest fliplr-test
     	([1 2 3 4 5] fliplr))

(run-tests)

