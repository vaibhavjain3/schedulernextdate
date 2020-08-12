import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

class Main  extends JPanel
{
  
  public Main()
  {
    String[] columnNames = {"Id", "Name","Contact"};
    Object[][] data =
    {
      {"1",  "Praveen", "9948737349"},
      {"2", "Aman","8743534765"},
      {"3", "Jaideep","7843652331"},
      {"1", "Praveen","9948737349"},
      {"2", "Aman","8743534765"},
      {"3", "Jaideep","7843652331"},
      {"1", "Praveen","9948737349"},
    };
        
    JTable table = new JTable(data,columnNames)
    {
      public boolean isCellEditable(int row, int column){  
        return false; 
      }
    };

    table.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());
    table.setCellSelectionEnabled(false);
    table.setRowSelectionAllowed(true);
    table.setRowHeight(30); 
    table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
   
    JScrollPane pane = new JScrollPane(table);
    add(pane);
  }
  
  private static void createAndShowGUI()
  {
    JFrame frame = new JFrame("Table");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new Main());
    frame.setLocationByPlatform( true );
    frame.pack();
    frame.setVisible( true );
  }
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    { 
      public void run()
      {
        createAndShowGUI();
      }
    });
  }
}

class ImageRenderer extends DefaultTableCellRenderer 
{
  public static final Border focusedCellBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
  public static final Border unfocusedCellBorder = createEmptyBorder();

  private static Border createEmptyBorder() {
      Insets i = focusedCellBorder.getBorderInsets(new JLabel());
      return BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right);
  }

  ImageIcon youtube = new ImageIcon(getClass().getResource("youtube1.png"));
  ImageIcon twitter = new ImageIcon(getClass().getResource("twiiter1.png")); 

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) 
  {
    setText((String) value);
    setIcon(((String)value=="Jaideep")?twitter:youtube);
    setOpaque(true);
    setFont(new FontUIResource("",Font.PLAIN,12));
    setBorder(hasFocus? focusedCellBorder : unfocusedCellBorder);
    setBackground(isSelected?table.getSelectionBackground():(row%2==0)?Color.RED:table.getBackground());
    return this;
  }
}

class ColoredTableCellRenderer extends DefaultTableCellRenderer
{
  public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column)
  {     
    if ((row % 2) == 0)
      setBackground(Color.RED);
      else
      setBackground(table.getBackground());

    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
    return this;
  }
}



