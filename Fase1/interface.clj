(ns interface
    (:require clojure.contrib.swing-utils)
    (:use dbread dbwrite dbsearch)
)
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox RowFilter ImageIcon)
        '(javax.swing.table DefaultTableModel TableRowSorter)
        '(javax.swing.event TableModelListener)
        '(java.awt.event ActionListener)
        '(java.util Collections)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)

;;;;;DEFS
(def table          (JTable. ))
(def filename       "db-1x2 - copia.db");"db-1x2.db")
(def testfilename   "db-1x2 - copia.db")
(def database       (read-bin-file filename))


(defn find-data
  "Finds specified data" 
  [sstring column list] 
  (filter #(= ((keyword column) %) sstring) list))

;;GETS
(defn get-magic
    "Returns magic number from a database"
    [db]
    (get db :magic))

(defn get-offset
    "Returns offset from a database"
    [db]
    (get db :offset))  

(defn get-num-fields
    "Returns number of fields from a database"
    [db]
    (get db :num-fields))

(defn get-fields
    "Returns fields from a database"
    [db]
    (get db :fields))  

(defn get-records 
    "Returns records from a database"
    [db]
    (get db :records))

(defn get-col-names
	"Returns a vector containing column names in CAPS LOCK (FOR CRUISE CONTROL)"
	[db]
	(vec (for [[coln _] (get db :fields)] (.toUpperCase coln))))

(defn get-field-lengths
	"Returns a vector containing field lengths"
	[db]
	(vec (for [[_ len] (get db :fields)] len)))
  

(defn fliplr
  "Flips a tuple horizontally"
  [tuple left right exleftval]
  (aset tuple left (aget tuple right))
  (aset tuple right exleftval)
  tuple
)

(defn records-to-array
  "Transforms record dictionary into a 2d array, keeps keywords."
  [records numfields]
  (let [records2d (to-array-2d records)]
    (loop [i 0]
        (if (< i (count records))
            (do   (loop [lelem 0  relem (- numfields 1)] 
                    (if (< lelem relem)
                        (do (aset records2d i (fliplr (aget records2d i) lelem relem (aget records2d i lelem)))
                            (recur (inc lelem) (dec relem)))
                        ()))
                   (recur (inc i))))                        
    )
    records2d
  )
)

(defn get-record2d-values
  "Returns 2d array containing only values."
  [records2d]
  (loop [i 0]
      (if (< i (alength records2d))
          (do   (loop [j 0] 
                  (if (< j (alength (aget records2d i)))
                      (do (aset records2d i j (second (aget records2d i j)))
                          (recur (inc j)))
                      ()))
                 (recur (inc i))))
  )
  records2d
)

        
(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [datamatrix   (agent (get-record2d-values (records-to-array (get-records database) (get-num-fields database))))
        
        windowSX     800
        windowSY     600
        labelH       25
        btnSX        150
        btnSY        40
        topY         80
        tableSX      (- windowSX (+ 30 btnSX))
        tableSY      (- windowSY labelH topY)
        
        frame       (JFrame. title)
        hPanel      (JPanel. )
        fPanel      (JPanel. )
        bPanel      (JPanel. )
        abPanel     (JPanel. )
        
        btnShowall  (JButton. "Show All")
        btnAdd      (JButton. "Add new row")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names database)))
        
        label       (JLabel. "Something")
        imgLabel    (JLabel.)
        
        counter     (ref 0)
        tRowN       (ref (count (get-records database)))
        tColN       (ref (get-num-fields database))

        tScrPane    (JScrollPane. table)
        
        tListener   (proxy [TableModelListener] []

        )
        
        model       (proxy [DefaultTableModel][@datamatrix (into-array(get-col-names database))]
        	  (isCellEditable [row col] true)                 ;All cells are editable
        )
        
        colFilter   (proxy [RowFilter][]
            (include [entry filters]
                (loop [i 0  result true]
                    (if (< i (count filters))
                        (if (and (result) (not (= "" (nth filter i))))
                            (recur (inc i) (.startsWith (.toString (.getValue entry i)) (nth filters i)))
                            ())
                        (result)
                    )
                )
        
                ;for (int i = 0; i < filters.length; i++) (
                ;    if (result && !(filters[i].equals("")) ) (
                ;        String v = entry.getValue(i).toString();
                ;        result =  v.startsWith(filters[i]);
                ;    )
                ;)
            )
        )
        
        
        ;;;;;Handlers
        hdlShowall    (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         
                         (.setText label 
                              (get-col-names database))))
                              ;(str (nth(nth (nth @datamatrix 0) 0)0)))))
                              ;(str "r "(.getSelectedRow table) " c " (.getSelectedColumn table)))))

        hdlAdd        (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setText label "Adding row...")
                         ;(dosync (alter tRowN inc))
                         (.addRow model (into-array (vec (repeat (get-num-fields database) ""))))
                         ;(write-new-row testfilename
                         ;         ["" "" "" "" "" "" ""] (get-field-lengths database))
                         (.setText label "Row added")
                         (.revalidate table)))

        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (if (= -1 (.getSelectedRow table))
                             (.setText label "No row selected.")
                             ())))
                                
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (if (= -1 (.getSelectedRow table))
                             (.setText label "No row selected.")
                             (delete-record testfilename (.getSelectedRow table) (get-offset database) (apply + (get-field-lengths database)))))) 
                                
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (println (.getText searchField))
                         (print (find-data  (.getText searchField) "location" (get-records database)))
                         ;(.setRowFilter sorter (RowFilter/regexFilter "j[a-z]*" (vec 0)))
                         (.setText label 
                                (get (nth (get-records database) (rem @counter (get-num-fields database))) :rate) )))]
                                
    ;;;;;;;;;;;;; END LET
        
;;;;;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
;;;;;;;PANEL
    (.setLayout         hPanel (new BorderLayout))
    (.setPreferredSize  hPanel (Dimension. windowSX topY))
    
    (.setLayout         fPanel (new FlowLayout))
    (.setPreferredSize  fPanel (Dimension. (+ 20 btnSX) (+ 20 (* 3 btnSY))))
    
    (.setLayout         bPanel (new FlowLayout))
    (.setPreferredSize  bPanel (Dimension. (+ 20 btnSX) (- tableSY (+ 20(* btnSY 3)))))
    
    (.setLayout         abPanel (new FlowLayout))
    (.setPreferredSize  abPanel (Dimension. (+ 20 btnSX) tableSY))
    
    ;(.setBackground     hPanel  (Color/yellow))
    ;(.setBackground     fPanel  (Color/cyan))
    ;(.setBackground     bPanel  (Color/magenta))
    ;(.setBackground     abPanel (Color/black))
    
    
;;;;;;;TABLE
    (.setModel table model)
    ;(.setRowSorter table colFilter)
    (.setPreferredSize table (Dimension. tableSX tableSY))
    (.setPreferredScrollableViewportSize table (Dimension. tableSX tableSY))
    (.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSX tableSY))
    (.setAutoCreateRowSorter table true)
    
;;;;;;;BUTTON
    (.setPreferredSize btnShowall   (Dimension. btnSX btnSY))
    (.setPreferredSize btnAdd       (Dimension. btnSX btnSY))
    (.setPreferredSize btnUpdate    (Dimension. btnSX btnSY))
    (.setPreferredSize btnDelete    (Dimension. btnSX btnSY))
    (.setPreferredSize btnFind      (Dimension. btnSX btnSY))

      ;;enable
    ;(.setEnabled btnAdd false)
    
    
;;;;;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. btnSX btnSY))
    
;;;;;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. btnSX btnSY))
    
;;;;;;;LABEL
    (.setPreferredSize label        (Dimension. windowSX 25))
    (.setPreferredSize imgLabel (Dimension. windowSX topY))
    (.setIcon imgLabel (ImageIcon. "urly.jpg"))
    
;;;;;;;ADDS
    (.add hPanel imgLabel BorderLayout/WEST)
    
    (.add fPanel BorderLayout/NORTH searchField)
    (.add fPanel BorderLayout/CENTER searchBox)
    (.add fPanel BorderLayout/SOUTH btnFind)
    
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    (.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    
    (.add abPanel BorderLayout/NORTH fPanel)
    (.add abPanel BorderLayout/SOUTH bPanel)
    
    (.add frame BorderLayout/NORTH hPanel)
    (.add frame BorderLayout/CENTER tScrPane)
    (.add frame BorderLayout/EAST abPanel)
    (.add frame BorderLayout/PAGE_END label)
    
    ;;;ACTION LISTENERS
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnAdd hdlAdd)
    (.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)

    ;(.addTableModelListener model tListener)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame windowSX windowSY)
    
  )
)

(interface "Fase 1")