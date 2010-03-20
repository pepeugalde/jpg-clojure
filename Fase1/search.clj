;JAVA
;public void Search(JTable table){
;
;int i=0;
;//javax.swing.table.TableModel model = table.getModel();
;Enumeration enum = table.getColumnModel().getColumns();
;String phone = phoneField.getText();
;boolean found = false;
;if(!found){
;( (ClientDatabase) (enum.nextElement())).getName().startsWith(phone);
;table.setRowSelectionInterval(i, i);
;}
;else{
;JOptionPane.showMessageDialog(null, "No Match Found.", "Message", JOptionPane.PLAIN_MESSAGE);
;}
;}

(defn search
"Returns the result of a searched value"
 [table]
 (let   [(= i 0)
 	 enum ((table (.getColumnModel .getColumns)))
 	 search (.getText field)
 	 found false]
 	 )
 	 (if (not found)
 	 	 ;(ClientDatabase)
 	 	 (enum (.nextElement .getName .startsWith search))	    
 	 	 (table (.setRowSelectionInterval i i)) 
 	 	 )
 	 (if (found)
 	 ;JOptionPane.showMessageDialog(null, "No Match Found.", "Message", JOptionPane.PLAIN_MESSAGE);

 	 )
)
