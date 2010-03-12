(import '(javax.swing JFrame JPanel JButton JLabel JTable)
        '(javax.swing.table.DefaultTableModel)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout Dimension FlowLayout)
)

(defn interface
  "Displays a simple GUI window using the Swing API."
  [title]
  (let [frame       (JFrame. title)
  
        panel       (JPanel. )
        
        table       (JTable. )
  
        benjamin    (JButton. "Push me =)")
        btnShowall  (JButton. "Show All")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        
        label       (JLabel. "Something" JLabel/CENTER)
        
        
        
        counter     (ref 0)
        hdlBenjamin     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlShowall     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))]
        
        
        
    ;
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    
    ;
    (.setLayout panel (new FlowLayout))
    
    (.setPreferredSize label (Dimension. 300 50))
    
    ;
    (.add panel table)
    (.add panel benjamin)
    (.add panel btnShowall)
    (.add panel btnUpdate)
    (.add panel btnDelete)
    (.add panel btnFind)
    (.add panel btnLock)
    (.add panel btnUnlock)
    
    (.add frame panel)
    
    (.add frame BorderLayout/SOUTH label)
    ;(.add frame BorderLayout/CENTER table)
    
    (.addActionListener benjamin hdlBenjamin)
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)
    (.addActionListener btnLock hdlLock)
    (.addActionListener btnUnlock hdlUnlock)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame 400 400)
    
    (.setModel table 5 5)
  )
)

(interface "Fase 1")