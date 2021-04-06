import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.awt.*;

class Compendium
{
   public static final int AMOUNT = 232;
   public static final int ARCANA_AMT = 23;
   public static final int ADV_AMT = 18;
   public static final int TREASURE_AMT = 9;
   private static Arcana[][] fusionChart;
   private Persona[][] two_D_ByArcana;
   private Persona byLevel[];
   private Persona byArcana[];
   private Persona alphabetized[];
   private TreasureDemon treasure[];
   private AdvancedPersona advanced[];
   private int[] personaCount;
   
   public Compendium()
   {
      loadArcanaChart();
      loadPersonas();
      loadAdvanced();
      sort();
      load2_D();
   }
   
   /*--------------------------------|
   Private methods, or helper methods|
   ----------------------------------|*/
   
   private void loadArcanaChart()
   {
      fusionChart = new Arcana[ARCANA_AMT][ARCANA_AMT];
      String value = "";
      try
      {
         Scanner arcanaFile = new Scanner(new File("Arcana Chart.txt"));
         for(int i = 0; i < ARCANA_AMT; i++)
         {
            for(int j = 0; j < ARCANA_AMT; j++)
            {
               value = arcanaFile.next();
               
               if(value.equalsIgnoreCase("null"))
               {
                  fusionChart[i][j] = null;
               }
               else if(value.equalsIgnoreCase("counselor"))
                  fusionChart[i][j] = Arcana.Consultant;
               else
                  fusionChart[i][j] = Arcana.valueOf(value);
            }
         }
      }
      catch(FileNotFoundException e)
      {
         System.out.println("ERROR: FAILED TO LOAD ARCANA'S");
         e.printStackTrace();
      }
   }
   
   private void loadPersonas()
   {
      byLevel = new Persona[AMOUNT];
      byArcana = new Persona[AMOUNT];
      alphabetized = new Persona[AMOUNT];
      advanced = new AdvancedPersona[ADV_AMT];
      treasure = new TreasureDemon[TREASURE_AMT];  
      personaCount = new int[ARCANA_AMT + 1];    
      
      try
      {
         Scanner personas = new Scanner(new File("ListOfPersonas.txt"));
         String name, temp;
         String level;
         Arcana arcana;
         boolean advancedPersona;
         boolean treas;
         int advCount = 0;
         int trCount = 0;
         int[] stats = new int[5];
         
         for(int i = 0; i < AMOUNT; i++)
         {
            treas = false;
            advancedPersona = false;
            level = personas.next();
            name = personas.next();
           //  System.out.println("DEBUG NAME: " + name);
            
            if(name.charAt(0) == '*')
            {
               advancedPersona = true;
            }
            
            temp = personas.next();
            
            while(!isArcana(temp))
            {
               name += (" " + temp);
               temp = personas.next();               
            }
            
            if(name.charAt(name.length() - 1) == '!')
            {
               treas = true;
            }
            
            if(temp.equalsIgnoreCase("Councillor"))
               arcana = Arcana.Consultant;
            else if(temp.equalsIgnoreCase("Hanged"))
               arcana = Arcana.Hanged_Man;
            else
               arcana = Arcana.valueOf(temp);
               
            for(int k = 0; k < 5; k++)
            {
               stats[k] = personas.nextInt();
            }
            
            String resistances = "";
            for(int j = 0; j < 10; j++)
            {
               resistances += personas.next() + " ";
            }
            
            if(advancedPersona)
            {
               name = name.substring(1, name.length());
               alphabetized[i] = byArcana[i] = byLevel[i] = advanced[advCount] = new AdvancedPersona(name, arcana, level);
               advCount++;
            }
            else if(treas)
            {
               name = name.substring(0, name.length() - 1);
               String tierBoost = personas.nextLine();
               int[] tierList = createTierArr(tierBoost);
               alphabetized[i] = byArcana[i] = byLevel[i] = treasure[trCount] = new TreasureDemon(name, arcana, level, tierList);
               trCount++;
            }
            else
               alphabetized[i] = byArcana[i] = byLevel[i] = new Persona(name, arcana, level);
            
            personaCount[byArcana[i].getArcana().ordinal()]++;
            byArcana[i].loadStats(stats[0], stats[1], stats[2], stats[3], stats[4]);
            byArcana[i].loadRes(strToRes(resistances));
            
         }
         
      }
      catch(FileNotFoundException e)
      {
         System.out.println("ERROR: FAILED TO LOAD PERSONA LIST, FILE NOT FOUND");
         e.printStackTrace();
         System.exit(2);
      }
   }
   
   private int[] createTierArr(String tiers)
   {
      Scanner strScan = new Scanner(tiers);
      int[] temp = new int[23];
      
      for(int i = 0; i < 23; i++)
      {
         temp[i] = strScan.nextInt();
      }
      
      return temp;
   }
   
   private void loadAdvanced()
   {
      int i = 0;
      if(advanced == null)
      {
         System.out.println("DEBUG: ADVANCED NULL ENDING PROGRAM");
         System.exit(1);
      }
      try
      {
         
         for(i = 0; i < advanced.length; i++)
         {
            advanced[i].setRecipe(advancedList(advanced[i].getName()));
         }
      }
      catch(NullPointerException e)
      {
         System.out.println("DEBUG: NULL IN ADVANCED AT " + i);
      }
   }
   
   private Persona find(String name)
   {
      Persona temp = new Persona(name, null, 0);
      for(int i = 0; i < AMOUNT; i++)
      {
         if(temp.equals(byArcana[i]))
            return byArcana[i];
      }
      return null;
   }
   
   private void load2_D()
   {
      two_D_ByArcana = new Persona[ARCANA_AMT + 1][0];
      for(int i = 0; i < personaCount.length; i++)
      {
         two_D_ByArcana[i] = new Persona[personaCount[i]];
      }
      
      int count = 0;
      for(int i = 0; i < two_D_ByArcana.length; i++)
      {
         for(int j = 0; j < two_D_ByArcana[i].length; j++, count++)
         {
            two_D_ByArcana[i][j] = byArcana[count];
         }
      }
   }
   
   private Resistance[] strToRes(String word)
   {
      Scanner scan = new Scanner(word);
      Resistance[] arr = new Resistance[10];
      String res;
      for(int i = 0; i < 10; i++)
      {
         res = scan.next();
         
         switch(res)
         {
            case "-":
               arr[i] = Resistance.Normal;
               break;
            case "wk":
               arr[i] = Resistance.Weak;
               break;
            case "rs":
               arr[i] = Resistance.Resist;
               break;
            case "nu": 
               arr[i] = null; //this may cause isuess later
               break;
            case "ab":
               arr[i] = Resistance.Absorb;
               break;
            case "rp": 
               arr[i] = Resistance.Repel;
               break;
            default:
               System.out.println("DEBUG: DON'T KNOW " + res);
               break;
         }
      }
      return arr;
   }
   
   private void sort()
   {
      Persona temp;
      for(int i = 0; i < AMOUNT; i++)
      {
         for(int j = 0; j < AMOUNT; j++)
         {
            if(byArcana[i].compareTo(byArcana[j]) < 0)
            {
               temp = byArcana[i];
               byArcana[i] = byArcana[j];
               byArcana[j] = temp;
            }
            
            if(alphabetized[i].getName().compareTo(alphabetized[j].getName()) < 0)
            {
               temp = alphabetized[i];
               alphabetized[i] = alphabetized[j];
               alphabetized[j] = temp;
            }
         }
      }
   }
   
   private Persona findClosest(int lvl, Arcana fusedArcana, boolean sameArcana, Persona p1, Persona p2)
   {
      Persona max, min, temp;
      int ix = 0, minix, maxix = 0;
      
      try
      {
         Persona[] list = two_D_ByArcana[fusedArcana.ordinal()];
               
         while(list[maxix].getLevel() < lvl)
         {
            maxix++;
         }
         max = findSuitableMax(p1, p2, list[maxix], list, lvl, maxix);
         minix = maxix - 1;
         
         min = findSuitableMin(p1, p2, list[minix], list, lvl, minix);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
         return null;
      }
            
      if(min == null || max == null)
         return null;
      
      if(lvl - min.getLevel() < max.getLevel() - lvl)
      {
         temp = min;
      }
      else
      {
         temp = max;
      }
      
      return temp;
   }
   
   private Persona findSuitableMin(Persona p1, Persona p2, Persona fused, Persona[] list, int lvl, int index)
   {
      Persona temp = fused;
      
      try
      {
         while(temp.equals(p1) || temp.equals(p2) || isAdvanced(temp) || isTreasure(temp))
         {
            index--;
            temp = list[index];
         }
      
         return temp;
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
//          System.out.println("DEBUG: MIN EXCEPTION THROWN");
         return null;
      }
   }
   
   private Persona findSuitableMax(Persona p1, Persona p2, Persona fused, Persona[] list, int lvl, int index)
   {
      Persona temp = fused;
      try
      {
         while(temp.equals(p1) || temp.equals(p2) || isAdvanced(temp) || isTreasure(temp))
         {
            index++;
            temp = list[index];
         }
         return temp;
      }
      catch(IndexOutOfBoundsException e)
      {
//          System.out.println("DEBUG: MAX EXCEPTION THROWN");
         return null;
      }
   }
   
   private Persona treasureFusion(Persona p1, Persona p2)
   {
//       System.out.println("DEBUG: IN TREASURE_FUSION");
      Persona temp, norm = null;
      TreasureDemon demon = null;
      Persona[] list; 
      int tierIncrease = 0;    
      int ix = 0;
      
      if(p1 instanceof TreasureDemon)
      {
         demon = (TreasureDemon)p1;
         norm = p2;
      }
      else if(p2 instanceof TreasureDemon)
      {
         demon = (TreasureDemon)p2;
         norm = p1;
      }
      else
      {
         System.out.println("ERROR: NEITHER ARE A TREASURE DEMON");
         return null;
      }
      try
      {
         list = two_D_ByArcana[norm.getArcana().ordinal()];
         for(ix = 0; ix < list.length; ix++)
         {
            if(norm.equals(list[ix]))
               break;
         }
//          System.out.println("DEBUG: IX AFTER " + ix);
//          System.out.println("DEBUG: IX + TIERINCREASE " + (ix + tierIncrease));
         tierIncrease = demon.getTierIncrease(norm);
               
         temp = list[ix + tierIncrease];
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
//          System.out.println("DEBUG: TREASURE ARRAY INDEX OUT OF BOUNDS");
//          System.out.println("DEBUG: IX " + ix);
//          System.out.println("DEBUG: IX + TIERINCREASE " + (ix + tierIncrease));
         return null;
      }
      return temp;
   }
   
   public Persona[] advancedList(String name) //may make privte later
   {
      Persona[] arr = null;
      name = name.toLowerCase();
      switch(name)
      {
         case "flauros": 
            arr = new Persona[]{find("Orobas"), find("Eligor"), find("Berith") };
            break;         
         case "neko shogun":
            arr = new Persona[]{find("Anzu"), find("Sudama"), find("Kodama") };
            break;
         case "bugs":
            arr = new Persona[]{find("Hariti"), find("Pisaca"), find("Pixie") };
            break;         
         case "seth":
            arr = new Persona[]{find("Horus"), find("Thoth"), find("Anubis"), find("Isis")};
            break;           
         case "trumpeter":
            arr = new Persona[]{find("Black Rider"), find("Pale Rider"), find("Red Rider"), find("White Rider")};
            break;
         case "black frost":
            arr = new Persona[]{find("King Frost"), find("Jack Frost"), find("Jack-o'-Lantern")};
            break;
         case "vasuki" :
            arr = new Persona[]{find("Raja Naga"), find("Ananta"), find("Naga")};
            break;
         case "asura" :
            arr = new Persona[]{find("Bishamonten"), find("Koumokuten"), find("Zouchouten"), find("Jikokuten")};
            break;
         case "kohryu" :
            arr = new Persona[]{find("Seiryu"), find("Byakko"), find("Suzaku"), find("Genbu")};
            break;
         case "sraosha":
            arr = new Persona[]{find("Gabriel"), find("Lilith"), find("Melchizedek"), find("Mithras"), find("Mithra")};
            break;
         case "izanagi-no-okami*" :
            arr = new Persona[]{find("Yamata-no-Orochi"), find("Throne"), find("Inugami"), find("Raja Naga"), find("Barong"), find("Norn")};
            break;
         case "michael" :
            arr = new Persona[]{find("Uriel"), find("Raphael"), find("Gabriel")};
            break;
         case "yoshitsune": 
            arr = new Persona[]{find("Futsunushi"), find("Yatagarasu"), find("Okuninushi"), find("Arahabaki"), find("Shiki-Ouji")};
            break;
         case "chi you":
            arr = new Persona[]{ find("Yoshitsune"), find("Cu Chulainn"), find("Thor"), find("Hecatoncheires"), find("White Rider")};
            break;
         case "metatron":
            arr = new Persona[]{find("Michael"), find("Sandalphon"), find("Dominion"), find("Melchizedek"), find("Power"), find("Principality")};
            break;
         case "izanagi-no-okami picaro*": 
            arr = new Persona[]{find("Okuninushi"), find("Orthrus"), find("Kali"), find("Mithras"), find("Cu Chulainn"), find("Lucifer")};
            break;
         case "lucifer":
            arr = new Persona[]{find("Satan"), find("Metatron"), find("Michael"), find("Trumpeter"), find("Ananta"), find("Anubis")};
            break;
         case "satanael": 
            arr = new Persona[]{find("Lucifer"), find("Satan"), find("Michael"), find("Ishtar"), find("Anzu"), find("Arsene")};
            break;
      }
      return arr;
   }
   
   /*-----------------------------------|
   Public methods for client class' use |
   ------------------------------------*/
   public static boolean isArcana(String word)
   {
      return word.equalsIgnoreCase("Fool") || word.equalsIgnoreCase("Magician") || word.equalsIgnoreCase("Priestess") || word.equalsIgnoreCase("Empress") 
             || word.equalsIgnoreCase("Emperor") || word.equalsIgnoreCase("Hierophant") || word.equalsIgnoreCase("Lovers") || word.equalsIgnoreCase("Chariot")
             || word.equalsIgnoreCase("Justice") || word.equalsIgnoreCase("Hermit") || word.equalsIgnoreCase("Fortune") || word.equalsIgnoreCase("Strength")
             || word.equalsIgnoreCase("Hanged_Man") || word.equalsIgnoreCase("Death") || word.equalsIgnoreCase("Temperance") || word.equalsIgnoreCase("Devil")
             || word.equalsIgnoreCase("Tower") || word.equalsIgnoreCase("Star") || word.equalsIgnoreCase("Moon") || word.equalsIgnoreCase("Sun") 
             || word.equalsIgnoreCase("Judgement") || word.equalsIgnoreCase("Faith") || word.equalsIgnoreCase("Consultant") || word.equalsIgnoreCase("Councillor")
             || word.equalsIgnoreCase("World");
   }
   
   public boolean isAdvanced(Persona p1)
   {
      for(int i = 0; i < advanced.length; i++)
      {
         if(p1.equals(advanced[i]))
            return true;
      }
      
      return false;
   }
   
   public boolean isTreasure(Persona p1)
   {
      for(int i = 0; i < TREASURE_AMT; i++)
      {
         if(p1.equals(treasure[i]))
            return true;
      }
      
      return false;
   }
   
   /*@Param list- will be used later if the user wants the list sorted 
   in any particular order*/   
   public ArrayList<Persona> lookUp(String name, Persona[] list)
   {
      ArrayList<Persona> list2 = new ArrayList<Persona>();
      if (list == null)
         list = byArcana;
      String res = "";
      
      for(int i = 0; i < AMOUNT; i++)
      {
         if(list[i].getName().toLowerCase().contains(name.toLowerCase()))
            list2.add(list[i]);
      }
      
      return list2;
   }
   
   public Persona fuse(Persona p1, Persona p2)
   {      
      if(p1.equals(p2))
         return null;
      
      boolean sameArcana = (p1.getArcana().ordinal() == p2.getArcana().ordinal());     
      Persona closest = null;
      
      Arcana arcana1 = p1.getArcana();
      Arcana arcana2 = p2.getArcana();
      
      if(arcana1.equals(Arcana.World) || arcana2.equals(Arcana.World))
         return null;
      
      Arcana newArcana = fusionChart[arcana1.ordinal()][arcana2.ordinal()];
      
      if(newArcana == null)
         return null;
      
      double calcLevel = (p1.getLevel() + p2.getLevel())/2.0 + 1;
      
      // System.out.println("DEBUG: " + newArcana);
//       System.out.println("DEBUG: " + calcLevel);
//       System.out.println("DEBUG BOTH TREASURE DEMON: " + (p2 instanceof TreasureDemon && !(p1 instanceof TreasureDemon && p2 instanceof TreasureDemon)));
      if((p1 instanceof TreasureDemon || p2 instanceof TreasureDemon) && !(p1 instanceof TreasureDemon && p2 instanceof TreasureDemon))
      {
         int tierIncrease = 0;
         
         if(p2 instanceof TreasureDemon)
            tierIncrease = ((TreasureDemon)p2).getTierIncrease(p1);
         else
            tierIncrease = ((TreasureDemon)p1).getTierIncrease(p2);
                 
         return treasureFusion(p1, p2);
      }
      else
         return findClosest((int)calcLevel, newArcana, sameArcana, p1, p2);
   }
   
      
   public ArrayList<Persona[]> findFusions(Persona pers)
   {
      ArrayList<Persona[]> list = new ArrayList<Persona[]>();
//       String list = "";
      Persona[] match = new Persona[2];
      if(pers instanceof AdvancedPersona)
         return ((AdvancedPersona)pers).getRecipe();
      else if(pers instanceof TreasureDemon)
         return null;
        
      for(int i = 0; i < AMOUNT; i++)
      {
         if(byLevel[i].equals(pers))
            continue;
         for(int j = i; j < AMOUNT; j++)
         {
            if(byLevel[j].equals(pers))
               continue;
            Persona temp = fuse(byLevel[i], byLevel[j]);
            
            if(temp != null && temp.equals(pers))
            {
               match = new Persona[2];
               match[0] = byLevel[i];
               match[1] = byLevel[j];
               list.add(match);
//                System.out.println("DEBUG: P1 " + match[0] + " P2 " + match[1]); 
            }
         }
      }
      return list;
   }
   
   public ArrayList<Persona> getCopyByArcana()
   {
      ArrayList<Persona> list = new ArrayList<Persona>(); 
      
      for(int i = 0; i < AMOUNT; i++)
      {
         list.add(byArcana[i]);
      }
      
      return list;
   }
   
   /*-------------------------------------|
   Display methods for debugging purposes |
   ---------------------------------------*/
   private String displayHeader()
   {
      String val = "LvL  Name\t\t\t\t\t\t\t  Arcana\t\t\tStr Mag End Agl Lu\n";
      val += "---------------------------------------------------------------\n";
      
      return val;
   }
   
   public String displayAlphabetic()
   {
      String s = displayHeader();
      
      for(int i = 0; i < AMOUNT; i++)
      {
         s+= alphabetized[i] + "\n";
      }
      return s;
   }
   
   
   public String displayByArcana()
   {
      String s = displayHeader();
      
      for(int i = 0; i < two_D_ByArcana.length; i++)
      {
         for(int j = 0; j < two_D_ByArcana[i].length; j++)
         {
            s += two_D_ByArcana[i][j] + "\n";
         }
      } 

      return s;
   }
   
   public String displayByLevel()
   {
      String s = displayHeader();
      
      for(int i = 0; i < AMOUNT; i++)
      {
         s += byLevel[i] + "\n";
      }
      return s;
   }
   
   public String displayCount()
   {
      String s = "";
      for(int i = 0; i < personaCount.length; i++)
      {
         s += personaCount[i] + " ";
      }
      
      return s;
   }
   
   public String displayTreasure()
   {
      String s = displayHeader();
      
      for(int i = 0; i < TREASURE_AMT; i++)
      {
         s += treasure[i] + "\n";
      }
      
      return s;
   }
   
   public void displayAdvanced()
   {
   
      System.out.println("Now displaying advanced personas------------------------------------------------------\n");
      displayHeader();
      for(int i = 0; i < advanced.length; i++)
      {
         if(advanced[i] != null)
         {
            System.out.println(advanced[i]);
         }
      }
      System.out.println("Finished displaying advanced--------------------------------------------------");
      
   }
   
   public String toString()
   {
      return displayByArcana();
   }
   
   public Persona[] getList()
   {
      return byArcana;
   }
   
   public Persona[][] get2DList()
   {
      return two_D_ByArcana;
   }
}

class CompendiumGUI extends JFrame
{
   private Compendium compendium;
   private JFrame currentPanel;
   private JPanel northPanel;
   private JPanel middlePanel;
   private JPanel westPanel;
   private JPanel eastPanel;
   private JPanel southPanel;
   private JScrollPane midScroll;
   private JScrollPane eastScroll;
   private BorderLayout mainLayout;
   private ArrayList<Persona> currentList;
   private ArrayList<FramePanels> previous;
   
   public CompendiumGUI()
   {
      compendium = new Compendium();
      previous = new ArrayList<FramePanels>();
      
      setSize(1200, 750);
      mainLayout = new BorderLayout();
      setLayout(mainLayout);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
           
      setUpMiddle();
      setUpNorth();
         
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            
      setVisible(true);
   }
   
   public void setUpNorth()
   {
      //make the north panel
         //make north panel's layout
      northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 7));
      JTextField textField = new JTextField(20);
      JButton clear = new JButton("Clear");
      
      textField.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            try
            {
               Document d = e.getDocument();
               String text = d.getText(0, d.getLength());
               
               if(text.equals(null) || text.equals(""))
                  currentList = compendium.getCopyByArcana();
               else
                  currentList = compendium.lookUp(text, null);
               
               System.out.println("DEBUG: SOMETHING WAS TYPED");
               midScroll.remove(middlePanel);
               remove(midScroll);
               setUpMiddle();
            }
            catch(BadLocationException ex)
            {
               System.out.println("DEBUG: BAD LOCATION THING HAPPENED WITH THE DOCUMENT THING");
               System.exit(3);
            }
         }
         
         public void removeUpdate(DocumentEvent e)
         {
            changedUpdate(e);
         }
         
         public void insertUpdate(DocumentEvent e)
         {  
            changedUpdate(e);
         }  
      });
      
      clear.setText("Clear");
      clear.setSize(80, 20);
      
      clear.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            textField.setText("");
         }
      });
      northPanel.add(textField);
      northPanel.add(clear);
      
      add(northPanel, BorderLayout.NORTH);
   }
   
   private void setUpMiddle()
   {
      middlePanel = new JPanel(new GridBagLayout());
      middlePanel.setDoubleBuffered(true);
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.fill = gbc.HORIZONTAL;
      
      if(currentList == null)
         currentList = compendium.getCopyByArcana();
     
      PersonaPanel persona;
      
      for(int i = 0; i < currentList.size(); i++)
      {
         persona = new PersonaPanel(false, currentList.get(i));
         persona.setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
         middlePanel.add(persona, gbc);
      }
      
      midScroll = new JScrollPane(middlePanel);
      midScroll.getVerticalScrollBar().setUnitIncrement(20);
      
      add(midScroll, BorderLayout.CENTER);
      setVisible(true);
   }
   
   public JFrame getFrame()
   { return this; }
      
   private void personaClicked(Persona p1)
   {
      FramePanels current = new FramePanels();
      current.north = northPanel;
      current.center = midScroll;
      current.east = eastScroll;
      current.west = westPanel;
      current.south = southPanel;
      previous.add(current);
      
      JFrame detailed = new JFrame();
      detailed.setSize(1200, 750);
      BorderLayout layout = new BorderLayout(100, 0);
      detailed.setLayout(layout);
      
      remove(northPanel);
      remove(midScroll);
      
      if(westPanel != null)
         remove(westPanel);
      if(eastScroll != null)
         remove(eastScroll);
         
      //decided to make the top panel here
      northPanel = new JPanel();
      JLabel header = new JLabel();
      header.setFont(new Font("Default", Font.BOLD, 21));
      header.setText(p1.getName() + " (" + p1.getLevel() + "/" + p1.getArcana() + ")");
      
      northPanel.add(header);
      
      westPanel = buildLeft(p1);
      midScroll = new JScrollPane(buildMiddle(p1));
      southPanel = buildBottom(p1);
      eastScroll = new JScrollPane(buildEast(p1));
      
      midScroll.getVerticalScrollBar().setUnitIncrement(20);
      eastScroll.getVerticalScrollBar().setUnitIncrement(20);
      
      add(westPanel, BorderLayout.WEST);
      add(northPanel, BorderLayout.NORTH);
      add(midScroll, BorderLayout.CENTER);
      add(southPanel, BorderLayout.SOUTH);
      add(eastScroll, BorderLayout.EAST);
      
      setVisible(true);
   }
   
   private JPanel buildLeft(Persona p1)
   {
      String[] statHeader = new String[]{"Str", "Mag", "End", "Agi", "Lck"};
      String[] resHeader = new String[]{"Phy", "Gun", "Fire", "Ice", "Elec", "Win", "Psy", "Nclr", "Bls", "Crs"};
      
      int[] stats = p1.getStats();
      Resistance[] res = p1.getResistances();
      
      JPanel statsLabel = new JPanel();
      JPanel resLabel = new JPanel();
      
      statsLabel.setLayout(new GridLayout(2, 5));
      resLabel.setLayout(new GridLayout(2, 10));
      
      for(int i = 0; i < statHeader.length; i++)
      {
         JLabel temp = new JLabel(statHeader[i]);
         temp.setHorizontalAlignment(JLabel.CENTER);
         statsLabel.add(temp);
      }
      
      for(int i = 0; i < stats.length; i++)
      {
         JLabel temp = new JLabel(Integer.toString(stats[i]));
         temp.setHorizontalAlignment(JLabel.CENTER);
         statsLabel.add(temp);
      }
      
      for(int i = 0; i < resHeader.length; i++)
      {
         JLabel temp = new JLabel(resHeader[i]);
         temp.setHorizontalAlignment(JLabel.CENTER);
         resLabel.add(temp);
      }
      
      for(int i = 0; i < res.length; i++)
      {
         JLabel temp;
         String word = "";
         
         if(res[i] == null)
         {
            word = " Nu ";
            continue;
         }
         else
         {
            switch(res[i])
            {
               case Normal:
                  word = " - ";
                  break;
               case Weak:
                  word = " Wk ";
                  break;
               case Resist:
                  word = " Rs ";
                  break;
               case Absorb:
                  word = " Ab ";
                  break;
               case Repel:
                  word = " Rp ";
                  break;
            }
         }
         
         temp = new JLabel(word);
         temp.setHorizontalAlignment(JLabel.CENTER);
         resLabel.add(temp);
      } 
            
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.weighty = 1;
      gbc.fill = gbc.HORIZONTAL;
//       gbc.insets = new Insets(10, 0, 10, 0);
//       gbc.gridheight = gbc.REMAINDER;
      JPanel leftSide = new JPanel();
      leftSide.setMinimumSize(new Dimension(200, 100));
//       leftSide.setSize(150, 650);
      leftSide.setLayout(new GridBagLayout());
      leftSide.add(statsLabel, gbc);
      
      gbc.gridx = 0;
      gbc.gridy = 1;
      
      leftSide.add(resLabel, gbc);
      leftSide.setBackground(Color.BLACK);
      
      gbc.gridx = 0;
      gbc.gridy = 2;      
      return leftSide;
   }
   
   private JPanel buildMiddle(Persona p1)
   {
      middlePanel = new JPanel();
      ArrayList<Persona[]> fusionList = compendium.findFusions(p1);
      Persona[] temp;
      
      if(fusionList != null)
      {
         middlePanel.setLayout(new GridLayout(fusionList.size(), 2));
         PersonaPanel persona1, persona2;
         
         JPanel middleMan = new JPanel();
         JLabel middleManLabel = new JLabel();
         middleMan.setLayout(new FlowLayout());
         middleMan.setBorder(new MatteBorder(1, 0, 1, 0, Color.GRAY));
         
         for(int i = 0; i < fusionList.size(); i++)
         {
            persona1 = new PersonaPanel(true, fusionList.get(i)[0]);
            persona2 = new PersonaPanel(true, fusionList.get(i)[1]);
            
            persona1.setBorder(new MatteBorder(1, 1, 1, 0, Color.GRAY));
            persona2.setBorder(new MatteBorder(1, 0, 1, 1, Color.GRAY));
            
            persona1.color = Color.YELLOW;
            persona2.color = Color.MAGENTA;
            
            persona1.setBackground(Color.YELLOW);
            persona2.setBackground(Color.MAGENTA);
            
            middleManLabel.setBorder(new MatteBorder(1, 0, 1, 0, Color.GRAY));
            middleManLabel.setHorizontalAlignment(JLabel.CENTER);
            
            
            middlePanel.add(persona1);
            middlePanel.add(persona2);
            middlePanel.setBackground(Color.WHITE);
         }
      }
      else
      {
         middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
         middlePanel.add(new JLabel("This is a treasure demon and cannot be fused to"));
      }
      
      return middlePanel;
   }
   
   private JPanel buildBottom(Persona p1)
   {
      JFrame current = this;
      southPanel = new JPanel();
      southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      southPanel.setSize(500, 120);
      
      JButton back = new JButton("Back");
      back.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if(southPanel != null)
               remove(southPanel);
            if(northPanel != null)
               remove(northPanel);
            if(midScroll != null)
            {
               remove(midScroll);
               remove(middlePanel);
               System.out.println("DEBUG: MIDDLE REMOVED");
            }
            
            if(eastScroll != null)
            {
               remove(eastScroll);
//                remove(eastPanel);
            }
            if(westPanel != null)
               remove(westPanel);
            
            FramePanels prevPanels = previous.remove(previous.size() - 1);
            System.out.println("DEBUG: PREVIOUS SIZE " + previous.size());
            
            southPanel = prevPanels.south;
            northPanel = prevPanels.north;
            eastScroll = prevPanels.east;
            westPanel = prevPanels.west;
            midScroll = prevPanels.center;
            
            if(prevPanels.north != null)
               add(northPanel, BorderLayout.NORTH);
            if(prevPanels.south != null)
               add(southPanel, BorderLayout.SOUTH);
            if(prevPanels.east != null)
               add(eastScroll, BorderLayout.EAST);
            if(prevPanels.west != null)
               add(westPanel, BorderLayout.WEST);
            if(prevPanels.center != null)
               add(midScroll, BorderLayout.CENTER);
            
            repaint();
            setVisible(true);
         }
      });
      
      southPanel.add(back);
      
      return southPanel;
   }
   
   private JPanel buildEast(Persona p1)
   {
      JPanel eastPanel = new JPanel();
      ArrayList<Persona> result = new ArrayList<Persona>(); 
      ArrayList<Persona> list = compendium.getCopyByArcana();
      int count = 0;
      
      Persona temp;
      for(count = 0; count < list.size(); count++)
      {
         result.add(compendium.fuse(list.get(count), p1));
         
       //   if(temp != null)
//             result.add(temp);
      }
      
      eastPanel.setLayout(new GridLayout(result.size(), 2));
      
      PersonaPanel panel1, panel2;
      for(int i = 0; i < result.size(); i++)
      {
         temp = result.get(i);
         if(temp != null)
         {
            panel1 = new PersonaPanel(true, list.get(i));
            panel2 = new PersonaPanel(true, temp);
            
            Color panel1Color = new Color(83, 190, 48);
            Color panel2Color = new Color(68, 196, 255);
            
            panel1.setBackground(panel1Color);            
            panel2.setBackground(panel2Color);
            
            panel1.color = panel1Color;
            panel2.color = panel2Color;
            
            panel1.setBorder(new MatteBorder(1, 0, 1, 1, Color.GRAY));
            panel2.setBorder(new MatteBorder(1, 0, 1, 1, Color.GRAY));
            
            eastPanel.add(panel1);
            eastPanel.add(panel2);
         }
      }
      
      return eastPanel;
   }
   
   private class FramePanels
   {
      public JPanel north;
      public JPanel south;
      public JScrollPane east;
      public JPanel west;
      public JScrollPane center;
   }
   
   private class PersonaPanel extends JPanel
   {
      private JLabel name;
      private JLabel lvl;
      private JLabel arcana;
      private JLabel stats;
      private JLabel res;
      private JLabel mainLabel;
      public Color color;
      
      public PersonaPanel(boolean two, Persona p)
      {
         setSize(30, 15);
         color = Color.WHITE;
         if(two)
         {
            setLayout(new GridLayout(1, 1));
            add(new JLabel(p.getName() + "  (" + Integer.toString(p.getLevel()) + "/" + p.getArcana().toString() + ")"));
            setBackground(color);
         }
         else
         {
            int[] stats = p.getStats();
            Resistance[] res = p.getResistances();
            setLayout(new GridLayout(1, 18));
            
            add(new JLabel(Integer.toString(p.getLevel())));
            add(new JLabel(p.getName()));
            add(new JLabel(p.getArcana().toString()));
            
            for(int i = 0; i < stats.length; i++)
            {
               add(new JLabel(Integer.toString(stats[i])));
            }
            
            for(int i = 0; i < res.length; i++)
            {
               if(res[i] == null)
                  add(new JLabel("Nu"));
               else
               {
                  switch(res[i])
                  {
                     case Normal:
                        add(new JLabel(" -  "));
                        break;
                     case Weak:
                        add(new JLabel(" Wk "));
                        break;
                     case Resist:
                        add(new JLabel(" Rs "));
                        break;
                     case Absorb:
                        add(new JLabel(" Ab "));
                        break;
                     case Repel:
                        add(new JLabel(" Rp "));
                        break;
                  }
               }
            }
         }
         setUpListener(p);
      }

      
      public PersonaPanel(Persona p)
      {
         setBackground(Color.WHITE);
         setSize(60, 20);
         GridBagLayout layout = new GridBagLayout();
         // setFont(new Font("SansSeriff", Font.BOLD, 16));
         
         GridBagConstraints constraints = new GridBagConstraints();
//          constraints.weightx = GridBagConstraints.fill;
         constraints.gridwidth = 2;
         constraints.gridy = 0;
         color = Color.WHITE;
         
         setLayout(layout);
         
         String strName = p.getName();
         String strLvL = Integer.toString(p.getLevel());
         String strArcana = p.getArcana().toString();
         
         lvl = new JLabel(strLvL);
         constraints.gridx = 0;
         constraints.ipadx = 15;
         constraints.anchor = GridBagConstraints.WEST;
         add(lvl, constraints);
         
         name = new JLabel(strName);
         int distance = 28 - strName.length();
         constraints.gridwidth = 28;
         constraints.gridx = 2;
         constraints.ipadx = distance;
         constraints.anchor = GridBagConstraints.EAST;
         add(name, constraints);
         
         arcana = new JLabel(strArcana);
         distance = 12 - strArcana.length();
         constraints.gridwidth = 12;
         constraints.ipadx = distance;
         
         
         String label = "";
         int[] sts = p.getStats();
         for(int i = 0; i < sts.length; i++)
         {
            label += sts[i] + " ";
         }
         stats = new JLabel(label);
         label = "";
         
         Resistance[] resistance = p.getResistances();
         for(int i = 0; i < resistance.length; i++)
         {
            if(resistance[i] == null)
            {
               label += " Nu ";
               continue;
            }
            switch(resistance[i])
            {
               case Normal:
                  label += " -  ";
                  break;
               case Weak:
                  label += " Wk ";
                  break;
               case Resist:
                  label += " Rs ";
                  break;
               case Absorb:
                  label += " Ab ";
                  break;
               case Repel:
                  label += " Rp ";
                  break;
            }
         }
         
         res = new JLabel(label);
         
         lvl.setFont(new Font("Default", Font.BOLD, 18));
         
//          add(lvl, constraints);
//          add(name, constraints);
         constraints.gridx = 30;
         constraints.gridwidth = 12;
         add(arcana, constraints);
         constraints.gridx = 42;
         constraints.gridwidth = 13;
         add(stats, constraints);
         constraints.gridx = 57;
         constraints.gridwidth = 50;
         add(res, constraints);
         
         setUpListener(p);
//       setLayout(new FlowLayout(FlowLayout.LEFT));
//       mainLabel = new JLabel(p.toString());
//       System.out.println(p.toString());
//       add(mainLabel);
      }
      private void setUpListener(Persona p)
      {
         addMouseListener(new MouseListener()
         {
            public void mouseClicked(MouseEvent e)
            {
//                previous.add(getFrame());
//                remove(previous.get(previous.size() - 1));
               // JFrame detailed = new JFrame();
      //                temp.setVisible(true);

               setBackground(color);
//                FramePanels panels = new FramePanels();
//                panels.north = northPanel;
//                panels.south = southPanel;
//                panels.east = eastPanel;
//                panels.west = westPanel;
//                panels.center = scroll;
//                
//                previous.add(panels);
               
               personaClicked(p);
            }
            
            public void mouseEntered(MouseEvent e)
            {
               setBackground(Color.CYAN);
               setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mousePressed(MouseEvent e)
            {
               
            }
            
            public void mouseReleased(MouseEvent e)
            {
               
            }
            
            public void mouseExited(MouseEvent e)
            {
               setBackground(color);
            }
         });
      }
   }
}

//-----------------------------------------------------------main

public class FusionCalculator
{
   public static void main(String[] args)
   {
      Compendium c = new Compendium();
      CompendiumGUI gui = new CompendiumGUI();
//       System.out.println(c);
   }   
}