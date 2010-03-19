(ns interface
(:require clojure.contrib.swing-utils)
(:use db))
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox)
        '(javax.swing.table DefaultTableModel)
        '(javax.swing.event TableModelListener)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)

(defn get-col-names
	"Returns a vector containing column names in CAPS LOCK (FOR CRUISE CONTROL)"
	[colpairs]
	(to-array 
		(vec (for [[coln _] colpairs] (.toUpperCase coln)) ) 
	)
)

(defn interface
  "Displays the interface that will be uswd in the urlybird project"
  [title]
  (let [fileName    "db-1x2.db"
        dict        (read-bin-file fileName)
        
        frameSizeX  800
        frameSizeY  600
        tableSizeX  500
        tableSizeY  400
        buttonSizeX 150
        buttonSizeY 150
        
        frame       (JFrame. title)
        ;tPanel      (JPanel. )
        fPanel      (JPanel. )
        bPanel      (JPanel. )
        abPanel      (JPanel. ) 
        
        benjamin    (JButton. "Push me =O")
        btnShowall  (JButton. "Show All")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (get-col-names (get dict :fields)))
        
        label       (JLabel. "Something")
        
        counter     (ref 0)
        tRowN   (ref (count (get dict :records)))
        tColN   (ref (get dict :num-fields))
        
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
            (getColumnName [col]                            ;
                    (nth (vec (get-col-names (get dict :fields))) col))
            ;;Uses column name as keyword to get value in row tuple
            (getValueAt [row col]                           
                    (get (nth (get dict :records) row) 
                       (keyword (str (first (nth (get dict :fields) col))))))
        )

        hdlBenjamin     (proxy [ActionListener][]
                       (actionPerformed [event]
                       	 (dosync (alter counter inc))
                         (dosync (alter tRowN inc))
                         (println @tRowN)
                         (.addRow model (into-array (repeat (- (get dict :num-fields) 1) "ja")))
                         
                         (.setTableModel table model)
                         (.repaint table)
                         (.setText label 
                                 (str(to-array-2d [[1 2 3] [4 5 6] [7 8 9]])) )))
                                ;(str  (get dict :fields)))))
                                ;(str "listeners: " (str (.getTableModelListeners model))) )))
                                
        hdlShowall     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str(get dict :fields)))))
                                ;(apply str(repeat 5 "ja")))))
                                
        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (for [pair (get dict :fields)] (.addItem searchBox (.toUpperCase (str (first pair)))))
                         (.setText label 
                                (str "COLNAMS " (vec(get-col-names (get dict :fields)))))))
                                
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str(to-array-2d [[1 2 3] [4 5 6] [7 8 9]])))))
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (get (nth (get dict :records) (rem @counter (get dict :num-fields))) :rate) )))
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
    (.setPreferredSize fPanel (Dimension. 245 200));(+ 45 (* buttonSizeY 3))))
    
    (.setLayout bPanel (new GridLayout 10 2 15 15))
    (.setPreferredSize bPanel (Dimension. buttonSizeX (- tableSizeY (+ 45 (* buttonSizeY 3)))))
    
    (.setLayout abPanel (new GridLayout 5 5))
    (.setPreferredSize abPanel (Dimension. 245 200));(+ 45 (* buttonSizeY 3))))
    
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
    (.setPreferredSize benjamin     (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnShowall   (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnUpdate    (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnDelete    (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnFind      (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnLock      (Dimension. buttonSizeX buttonSizeY))
    (.setPreferredSize btnUnlock    (Dimension. buttonSizeX buttonSizeY))

    ;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. buttonSizeX buttonSizeY))
    
    ;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. buttonSizeX buttonSizeY))
    
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