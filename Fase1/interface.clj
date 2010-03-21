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
  (let [filename    "db-1x2.db"
        testfilename "lol.db"
        database    (read-bin-file filename)
        
        frameSizeX  800
        frameSizeY  600
        tableSizeX  500
        tableSizeY  400
        btnSizeX    150
        btnSizeY    150
        
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
                                 (str(to-array-2d [[1 2 3] [4 5 6] [7 8 9]])) )))
                                
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
                                (str "COLNAMS " (get-col-names database)))))
                                
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str(to-array-2d [[1 2 3] [4 5 6] [7 8 9]])))))
                                
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (get (nth (get-records database) (rem @counter (get-num-fields database))) :rate) )))
                                
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (.setValueAt model "LOL" 0 0))))
                                
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))]
    ;;;;;;;;;;;;; END LET
        
    ;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
    ;;;PANEL
    ;(.setLayout tPanel (new FlowLayout))
    ;(.setAutoResizeMode table JTable/AUTO_RESIZE_OFF)
    (.setLayout fPanel (new GridLayout 5 5))
    (.setPreferredSize fPanel (Dimension. 245 200));(+ 45 (* btnSizeY 3))))
    
    (.setLayout bPanel (new GridLayout 10 2 15 15))
    (.setPreferredSize bPanel (Dimension. btnSizeX (- tableSizeY (+ 45 (* btnSizeY 3)))))
    
    (.setLayout abPanel (new GridLayout 5 5))
    (.setPreferredSize abPanel (Dimension. 245 200));(+ 45 (* btnSizeY 3))))
    
    ;(.setBackground tPanel (Color/yellow))
    ;(.setBackground fPanel (Color/cyan))
    ;(.setBackground bPanel (Color/magenta))
    
    ;;;TABLE
    (.setModel table model)
    (.setPreferredSize table (Dimension. tableSizeX tableSizeY))
    (.setPreferredScrollableViewportSize table (Dimension. tableSizeX tableSizeY))
    (.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSizeX tableSizeY))
    
    ;;;BUTTON
    (.setPreferredSize benjamin     (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnShowall   (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnUpdate    (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnDelete    (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnFind      (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnLock      (Dimension. btnSizeX btnSizeY))
    (.setPreferredSize btnUnlock    (Dimension. btnSizeX btnSizeY))
      ;;enable
    ;(.setEnabled btnAdd false)
    
    
    ;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. btnSizeX btnSizeY))
    
    ;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. btnSizeX btnSizeY))
    
    ;;;LABEL
    (.setPreferredSize label        (Dimension. frameSizeX 25))
    
    ;;;ADD
    ;(.add tPanel (.getTableHeader table) BorderLayout/NORTH)
    ;(.add tPanel tScrPane)
    ;(.add tPanel BorderLayout/SOUTH table)
    
    (.add bPanel searchField)
    (.add bPanel searchBox)
    (.add bPanel btnFind)
    
    (.add bPanel benjamin)
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    (.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    (.add bPanel btnLock)
    (.add bPanel btnUnlock)
    
    (.add frame BorderLayout/CENTER tScrPane)
    ;(.add frame BorderLayout/EAST fPanel)
    (.add frame BorderLayout/EAST bPanel)
    (.add frame BorderLayout/PAGE_END label)
    ;(.add frame BorderLayout/CENTER table)
    
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
    (.setSize frame frameSizeX frameSizeY)
  )
)

(interface "Fase 1")