import java.util.ArrayList;

enum Arcana { Fool, Magician, Priestess, Empress, Emperor, Hierophant, Lovers, Chariot, Justice, Hermit, Fortune,
              Strength, Hanged_Man, Death, Temperance, Devil, Tower, Star, Moon, Sun, Judgement, Faith, Consultant, 
              World}

enum Resistance {Weak, Resist, Absorb, Normal, Repel}

public class Persona
{
   //basic stuff
   private Arcana arcana;
   private String name;
   private int level;
   
   //stats
   private int strength;
   private int magic;
   private int endurance;
   private int agility;
   private int luck;   
   
   //Resistances
   private Resistance[] resistances;
   
   public Persona(String na, Arcana arc, int lvl)
   {
      name = na;
      arcana = arc;
      level = lvl;
   }
   
   public Persona(String na, Arcana arc, String lvl)
   {
      name = na;
      arcana = arc;
      level = Integer.valueOf(lvl);
   }
   
   public Arcana getArcana()
   { return arcana; }
   
   public int getLevel()
   { return level;}
   
   public String getName()
   { return name; }
   
   public Resistance[] getResistances()
   { return resistances; }
   
   public int[] getStats()
   { return new int[]{strength, magic, endurance, agility, luck}; }
   
   public int compareTo(Persona other)
   {  
      if(equals(other))
         return 0;
      
      if(arcana.ordinal() < other.arcana.ordinal())
         return -1;
      else if(arcana.ordinal() > other.arcana.ordinal())
         return 1;
      else
      {
         if(level < other.level)
            return -1;
          else
            return 1;
      }
   }
   
   public boolean equals(Persona other)
   {
      return name.equalsIgnoreCase(other.name);
   }
   
   public void loadStats(int str, int mag, int end, int ag, int lu)
   {
      strength = str;
      magic = mag;
      endurance = end;
      agility = ag;
      luck = lu;
   }
   
   public void loadRes(Resistance[] res)
   {
      if(res == null)
         System.out.println("DEBUG: res is null");
      resistances = res;
   }
   
   public String toString()
   {
      String word = String.format("%2s  %-28s %-12s ", level, name, arcana);
      word += String.format("%-2s  %-2s  %-2s  %-2s  %-2s ", strength, magic, endurance, agility, luck);
      if(resistances == null)
         System.out.println("DEBUG: RES IS NULL");
      // System.out.println(resistances);
      for(int i = 0; i < resistances.length; i++)
      {
         if(resistances[i] == null)
         {
            word += " Nu ";
            continue;
         }
         switch(resistances[i])
         {
            case Normal:
               word += " -  ";
               break;
            case Weak:
               word += " Wk ";
               break;
            case Resist:
               word += " Rs ";
               break;
            case Absorb:
               word += " Ab ";
               break;
            case Repel:
               word += " Rp ";
               break;
         }
      } 
      
      return word; 
   }
   
   public String toStringShort()
   {
      return String.format("%2s  %-28s %-12s ", level, name, arcana);
   }
   
   public String jsSyntax()
   {
      String temp = "\nnew " + this.getClass().getSimpleName() + "(Arcana." + arcana + ", \"" + name + "\", " + level;
      
      temp += ",\n\t\t\t[";
      temp += strength + ", " + magic + ", " + endurance + ", " + agility + ", " + luck + "],\n\t\t\t";
      
      temp += "[";
      for(int i = 0; i < resistances.length; i++)
      {
         temp += "Resistsnce." + resistances[i] + ", ";
      }
      temp += "], ), ";
      
      return temp;
   }
}

//------------------------------------------------------------------AdvancedPersona
class AdvancedPersona extends Persona
{
   private Persona[] fusionRecipe;
   
   public AdvancedPersona(String na, Arcana arc, int lvl, Persona[] arr)
   {
      super(na, arc, lvl);
      fusionRecipe = arr;
   }
   
   public AdvancedPersona(String na, Arcana arc, String lvl, Persona[] arr)
   {
      super(na, arc, lvl);
      fusionRecipe = arr;
   }
   
   public AdvancedPersona(String na, Arcana arc, int lvl)
   {
      super(na, arc, lvl);
   }
   
   public AdvancedPersona(String na, Arcana arc, String lvl)
   {
      super(na, arc, lvl);
   }
   
   public ArrayList<Persona[]> getRecipe()
   {
      ArrayList<Persona[]> list = new ArrayList<Persona[]>();
      if(fusionRecipe == null) {return null;} //debugging
      // for(int i = 0; i < fusionRecipe.length; i++)
//       {
//          list.add(fusionRecipe);
//       }
//       return list;
      
      list.add(fusionRecipe);
      return list;
   }
   public void setRecipe(Persona[] list)
   {
      fusionRecipe = list;
   }
   
   public String jsSyntax()
   {
      String temp = super.jsSyntax();
      temp = temp.substring(0, temp.lastIndexOf(')'));
      
      temp += "\n\t\t\t[";
      
      for(int i = 0; i < fusionRecipe.length; i++)
      {
         temp += "\"" + fusionRecipe[i].getName() + "\", ";
      }
      temp += "]), \n";
      
      return temp;
   }
}

//---------------------------------------------------Treasure Demon----------------
class TreasureDemon extends Persona
{
   private int[] tierChart;
   
   public TreasureDemon(String na, Arcana arc, int lvl, int[] chart)
   {
      super(na, arc, lvl);
      tierChart = chart;
   }
   
   public TreasureDemon(String na, Arcana arc, String lvl, int[] chart)
   {
      super(na, arc, lvl);
      tierChart = chart;
   }
   
   public int[] getChart()
   {
      return tierChart;
   }
   
   public int getTierIncrease(Persona p1)
   {
      return tierChart[p1.getArcana().ordinal()];
   }
   
   public int getTierIncrease(Arcana arc)
   {
      return tierChart[arc.ordinal()];
   }
   
   public String jsSyntax()
   {
      String temp = super.jsSyntax();
      temp = temp.substring(0, temp.lastIndexOf(')'));
      temp += "\n\t\t\t[";
      
      for(int i = 0; i < tierChart.length; i++)
      {
         temp += tierChart[i] + ", ";
      }
      temp += "]), \n";
      
      return temp;
   }
}