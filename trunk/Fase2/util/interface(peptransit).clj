(ns interface
(:require clojure.contrib.swing-utils)
(:use db))
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox)
        '(javax.swing.table DefaultTableModel)
        '(javax.swing.event TableModelListener)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)



(defn fillOutTableModel
	[dict model]
	(let [counter (count (get dict :records))]

		(println "Entre!")
		(loop [i 0]
		  (when (< i counter)
		  	  (doto model (.addRow (to-array ["jojo" "" "" "" "" "" ""])))
		    (recur (inc i))))

    	)
    	;;(get (nth (get dict :records) row) (keyword (str (first (nth (get dict :fields) col))))))
(println "Sali!")
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
  (let [dict        (read-bin-file "db-1x2.db")
        
  	  
        frameSizeX  800
        frameSizeY  600
        tableSizeX  500
        tableSizeY  400
        buttonSizeX 50
        buttonSizeY 150
        
        frame       (JFrame. title)
        tPanel      (JPanel. )
        fPanel      (JPanel. )
        bPanel      (JPanel. )  
        
        btnNewRow    (JButton. "New Row")
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
        ;; Implementacion de model antigua!
        ;;model   (proxy [DefaultTableModel] [] 
        ;;	(isCellEditable [row col] true)                 ;All cells are editable
        ;;	(getRowCount [] (count (get dict :records)))    ;Gets number of rows from database
        ;;	(getColumnCount [] (get dict :num-fields))      ;Gets number of cols from database
        ;;	;;Sets column names as the string in :fields vector
        ;; 	(getColumnName [col]                            
        ;;            (.toUpperCase (str (first (nth (get dict :fields) col)))))
            ;;Uses column name as keyword to get value in row tuple
        ;;    (getValueAt [row col]                           
        ;;            (get (nth (get dict :records) row) 
        ;;                (keyword (str (first (nth (get dict :fields) col))))))
        ;;)
        
        model   (proxy [DefaultTableModel] [] 
        	(isCellEditable [row col] true)     
        )
        
        
        ;;(dosync (alter counter inc))
        ;;(.setText label (str "listeners: " (str (.getTableModelListeners model))) )
        
        hdlNewRow     (proxy [ActionListener][]
                       (actionPerformed [event]
                       	       (doto model (.addRow (to-array ["" "" "" "" "" "" ""])))
                       	       (doto table (.setTableModel model))
        ))
        
                                
        hdlShowall     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText searchField 
                                (str (get dict :fields)))))
                                
        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (for [pair (get dict :fields)] (.addItem searchBox (.toUpperCase (str (first pair)))))
                         (.setText label 
                                (str "COLNAMS " (doall(take 5 (get-col-names (get dict :fields))))))))
                                
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "PSEUDO: " (keyword (str(first (nth (get dict :fields) (rem @counter (get dict :num-fields))))))))))
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (get (nth (get dict :records) (rem @counter (get dict :num-fields))) :rate) )))
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "KEYS" (str(class(nth(keys (nth (get dict :records) (rem @counter (get dict :num-fields)) ))0)))))))
                                
                                
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))]
    ;;;;;;;;;;;;; END LET
        
      	;;Probando el fillOutTableModel
  	
    
    ;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
    ;;;PANEL
    ;(.setLayout tPanel (new FlowLayout))
    ;(.setAutoResizeMode table JTable/AUTO_RESIZE_OFF)
    (.setLayout fPanel (new GridLayout 5 5))
    (.setPreferredSize fPanel (Dimension. 245 200));(+ 45 (* buttonSizeY 3))))
    	
    (.setLayout bPanel (new GridLayout 10 2 15 15))
    (.setPreferredSize bPanel (Dimension. 245 (- tableSizeY (+ 45 (* buttonSizeY 3)))))
    
    ;(.setBackground tPanel (Color/yellow))
    ;(.setBackground fPanel (Color/cyan))
    ;(.setBackground bPanel (Color/magenta))
    
    (fillOutTableModel dict model)
    (doto table (.setTableModel model))
    ;;;TABLE
    (.setModel table model)
    (.setPreferredSize table (Dimension. tableSizeX tableSizeY))
    (.setPreferredScrollableViewportSize table (Dimension. tableSizeX tableSizeY))
    (.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSizeX tableSizeY))
    
    ;;;BUTTON
    (.setPreferredSize btnNewRow     (Dimension. buttonSizeX buttonSizeY))
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
    
    (.add bPanel btnNewRow)
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
    (.addActionListener btnNewRow hdlNewRow)
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