(ns interface
    (:require clojure.contrib.swing-utils)
    (:use dbread dbwrite dbsearch)
)
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox RowFilter)
        '(javax.swing.table DefaultTableModel TableRowSorter)
        '(javax.swing.event TableModelListener)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)

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
  "Flips an array horizontally"
  [arr]
  arr
)
  
(defn records-to-array
  "Transforms record dictionary into a 2d array"
  [records]
  (fliplr (to-array-2d records))
;  (let [array []]
;    (for [tuple records]
;      
;      (print (loop [filteredrow []   ituple tuple]
;        (if (empty? ituple)
; 
; (filteredrow)
;          (recur (conj filteredrow (str(first ituple)))(rest ituple))
;        )
;      ))
;      
;    )
;  )
)

(def table          (JTable. ))

(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [filename     "db-1x2 - copia.db";"db-1x2.db"
        testfilename "db-1x2 - copia.db"
        database     (read-bin-file filename)
        
        datamatrix   (agent (records-to-array (get-records database)))
        
        windowSX     800
        windowSY     600
        labelH       25
        btnSX        150
        btnSY        30
        tableSX      (- windowSX (+ 30 btnSX))
        tableSY      (- windowSY labelH)
        
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
        
        
        ;;Handlers
        hdlShowall    (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         
                         (.setText label 
                              (str (nth datamatrix 0)))))
                              ;(str "r "(.getSelectedRow table) " c " (.getSelectedColumn table)))))

        hdlAdd        (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setText label "Adding row...")
                         ;(dosync (alter tRowN inc))
                         (.addRow model (into-array ["" "" "" "" "" "" ""]))
                         ;(write-new-row testfilename
                         ;         ["" "" "" "" "" "" ""] (get-field-lengths database))
                         (.setText label "Row Added.")
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
                             (delete-record "db-1x2 - copia.db" (.getSelectedRow table) (get-offset database) (apply + (get-field-lengths database)))))) 
                                
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
    (.setLayout         hPanel (new FlowLayout))
    ;(.setAutoResizeMode table JTable/AUTO_RESIZE_OFF)
    
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
    
;;;;;;;ADDS
    ;(.add hPanel image BorderLayout/CENTER)
    ;(.add hPanel tScrPane)
    ;(.add hPanel BorderLayout/SOUTH table)
    
    (.add fPanel BorderLayout/NORTH searchField)
    (.add fPanel BorderLayout/CENTER searchBox)
    (.add fPanel BorderLayout/SOUTH btnFind)
    
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    (.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    
    (.add abPanel BorderLayout/NORTH fPanel)
    (.add abPanel BorderLayout/SOUTH bPanel)
    
    ;(.add frame BorderLayout/NORTH hPanel)
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