(ns interface
    (:require clojure.contrib.swing-utils)
    (:use dbread dbwrite dbsearch)
)
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox)
        '(javax.swing.table DefaultTableModel)
        '(javax.swing.event TableModelListener)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)

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
	(vec (for [[coln _] (get db :fields)] (.toUpperCase coln)))
)

(defn get-field-lengths
	"Returns a vector containing field lengths"
	[db]
	(vec (for [[_ len] (get db :fields)] len))
)

(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [filename     "db-1x2 - copia.db";"db-1x2.db"
        testfilename "lol.db"
        database    (read-bin-file filename)
        
        windowSX  800
        windowSY  600
        labelH    25
        btnSX     150
        btnSY     30
        tableSX   (- windowSX (+ 30 btnSX))
        tableSY   (- windowSY labelH)
        
        
        frame       (JFrame. title)
        ;tPanel      (JPanel. )
        fPanel      (JPanel. )
        bPanel      (JPanel. )
        abPanel     (JPanel. ) 
        
        benjamin    (JButton. "Push me =O")
        btnShowall  (JButton. "Show All")
        btnAdd      (JButton. "Add new row")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names database)))
        
        label       (JLabel. "Something")
        
        counter     (ref 0)
        tRowN       (ref (count (get-records database)))
        tColN       (ref (get-num-fields database))
        
        table       (JTable. )
        tScrPane    (JScrollPane. table)
        
        tListener   (proxy [TableModelListener] []
            (tableChanged [e]
                    (.setText label "lol")
                    ;(str "tableChanged(" (.getSource e) "), rowCount = " (.getRowCount (.getSource e))))
             ;       (if (< 0 (.getRowCount (.getSource e)))
              ;        (do-swing (preview-mode))
               ;       (do-swing (init-mode)))
            )
            (columnMarginChanged [e] (.setText label "lol2"))
        )

        
        
        model       (proxy [DefaultTableModel][]
        	(isCellEditable [row col] true)                 ;All cells are editable
            (getRowCount []    @tRowN)  ;Gets number of rows from database
            (getColumnCount [] @tColN)  ;Gets number of cols from database
        	;;Sets column names as the string in :fields vector
            (getColumnName [col]
                    (nth (get-col-names database) col))
            ;;Uses column name as keyword to get value in row tuple
            (getValueAt [row col]
                    (get (nth (get-records database) row)
                       (keyword (str (first (nth (get-fields database) col))))))
        )

        hdlBenjamin    (proxy [ActionListener][]
                        (actionPerformed [event]
                       	 (dosync (alter counter inc))
                         (dosync (alter tRowN inc))
                         (println @tRowN)
                         (.addRow model (into-array (repeat (- (get-num-fields database) 1) "ja")))
                         
                         (.setTableModel table model)
                         (.repaint table)
                         (.setText label
                                (str (get-fields database)) )))
                                
        hdlShowall    (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str(get-field-lengths database)))))

        hdlAdd        (proxy [ActionListener][]
                       (actionPerformed [event]
                         (println "adding...")
                         (do (write-new-row testfilename
                                  ["La Jornada" "Aqui" "4" "N" "$1.00" "2000/01/01" ""] (get-field-lengths database)))))

        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (for [pair (get-fields database)] (.addItem searchBox (.toUpperCase (str (first pair)))))
                         (.setText label 
                                (str (get-records database)))))
                                
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (delete-record "db-1x2 - copia.db" 0 (get-offset database) (apply + (get-field-lengths database)))))
                                
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (get (nth (get-records database) (rem @counter (get-num-fields database))) :rate) )))
                                
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str (.getValueAt model 0 0) (.getValueAt model 0 1) (.getValueAt model 0 2)))))
                                
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label
                                (str "Times pushed: " @counter))))]
    ;;;;;;;;;;;;; END LET
        
;;;;;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
;;;;;;;PANEL
    ;(.setLayout         tPanel (new FlowLayout))
    ;(.setAutoResizeMode table JTable/AUTO_RESIZE_OFF)
    
    (.setLayout         fPanel (new FlowLayout))
    (.setPreferredSize  fPanel (Dimension. (+ 20 btnSX) (+ 20 (* 3 btnSY))))
    
    (.setLayout         bPanel (new FlowLayout))
    (.setPreferredSize  bPanel (Dimension. (+ 20 btnSX) (- tableSY (+ 20(* btnSY 3)))))
    
    (.setLayout         abPanel (new FlowLayout))
    (.setPreferredSize  abPanel (Dimension. (+ 20 btnSX) tableSY))
    
    ;(.setBackground     tPanel  (Color/yellow))
    ;(.setBackground     fPanel  (Color/cyan))
    ;(.setBackground     bPanel  (Color/magenta))
    ;(.setBackground     abPanel (Color/black))
    
    
;;;;;;;TABLE
    (.setModel table model)
    (.setPreferredSize table (Dimension. tableSX tableSY))
    (.setPreferredScrollableViewportSize table (Dimension. tableSX tableSY))
    (.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSX tableSY))
    
;;;;;;;BUTTON
    (.setPreferredSize benjamin     (Dimension. btnSX btnSY))
    (.setPreferredSize btnShowall   (Dimension. btnSX btnSY))
    (.setPreferredSize btnAdd       (Dimension. btnSX btnSY))
    (.setPreferredSize btnUpdate    (Dimension. btnSX btnSY))
    (.setPreferredSize btnDelete    (Dimension. btnSX btnSY))
    (.setPreferredSize btnFind      (Dimension. btnSX btnSY))
    (.setPreferredSize btnLock      (Dimension. btnSX btnSY))
    (.setPreferredSize btnUnlock    (Dimension. btnSX btnSY))
      ;;enable
    ;(.setEnabled btnAdd false)
    
    
;;;;;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. btnSX btnSY))
    
;;;;;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. btnSX btnSY))
    
;;;;;;;LABEL
    (.setPreferredSize label        (Dimension. windowSX 25))
    
;;;;;;;ADDS
    ;(.add tPanel (.getTableHeader table) BorderLayout/NORTH)
    ;(.add tPanel tScrPane)
    ;(.add tPanel BorderLayout/SOUTH table)
    
    (.add fPanel BorderLayout/NORTH searchField)
    (.add fPanel BorderLayout/CENTER searchBox)
    (.add fPanel BorderLayout/SOUTH btnFind)
    
    (.add bPanel benjamin)
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    (.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    (.add bPanel btnLock)
    (.add bPanel btnUnlock)
    
    (.add abPanel BorderLayout/NORTH fPanel)
    (.add abPanel BorderLayout/SOUTH bPanel)
    
    (.add frame BorderLayout/CENTER tScrPane)
    (.add frame BorderLayout/EAST abPanel)
    (.add frame BorderLayout/PAGE_END label)
    
    ;;;ACTION LISTENERS
    (.addActionListener benjamin hdlBenjamin)
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnAdd hdlAdd)
    (.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)
    (.addActionListener btnLock hdlLock)
    (.addActionListener btnUnlock hdlUnlock)
    (.addTableModelListener model tListener)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame windowSX windowSY)
  )
)

(interface "Fase 1")