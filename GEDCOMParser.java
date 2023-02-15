import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GEDCOMParser {

    public static void main(String[] args) {
        String fileName = args[0];
        //String fileName = "C:\\Users\\theb1\\Documents\\CS555\\AgileProject-main\\family.ged"; // replace with actual file name
        List<Individual> individuals = new ArrayList<>();
        List<Family> families = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Individual currentIndividual = null;
            Family currentFamily = null;
            Map<String, Individual> individualsMap = new HashMap<>();
            Map<String, Family> familiesMap = new HashMap<>();
            String tag =null;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");


                if (tokens[0].equals("0")) {
                    if (tokens.length >= 3 && tokens[2].equals("INDI")) {
                        currentIndividual = new Individual(tokens[1]);
                        individualsMap.put(tokens[1], currentIndividual);
                        individuals.add(currentIndividual);
                    } else if (tokens.length >= 3 && tokens[2].equals("FAM")) {
                        currentFamily = new Family(tokens[1]);
                        familiesMap.put(tokens[1], currentFamily);
                        families.add(currentFamily);
                    }
                } else if (tokens[0].equals("1")) {
                    switch (tokens[1]) {
                        case "SEX":
                            if (currentIndividual != null) {
                                currentIndividual.setSex(tokens[2]);
                            }
                            break;
                        case "DEAT":
                            if (currentIndividual != null) {
                                currentIndividual.setAlive("N");

                                tag = "DEAT";

                            }
                            break;
                        case "BIRT":
                            if (currentIndividual != null) {
                                tag = "BIRT";
                            }
                            break;
                        case "MARR":
                            if (currentFamily != null) {
                                tag = "MARR";
                            }
                            break;
                        case "HUSB":
                            if (currentFamily != null) {
                                currentFamily.setHusband(individualsMap.get(tokens[2]));
                            }
                            break;
                        case "WIFE":
                            if (currentFamily != null) {
                                currentFamily.setWife(individualsMap.get(tokens[2]));
                            }
                        case "_CURRENT":
                            if (currentFamily != null) {
                                if(tokens[2]=="Y") {
                                    currentFamily.setDivorce("N");
                                }
                                 else {
                                    currentFamily.setDivorce("Y");
                                }
                            }
                            break;
                         case "CHIL":
                             if (currentFamily != null) {
                             currentFamily.addChild(individualsMap.get(tokens[2]));
                             }
                             break;
                    }
                }
                else if (tokens[0].equals("2")) {
                    switch (tokens[1]) {
                        case "GIVN":
                            if (currentIndividual != null) {
                                currentIndividual.setName(tokens[2] );

                            }
                            break;
                        case "SURN":
                            if (currentIndividual != null) {
                                currentIndividual.setLastName(tokens[2]);
                            }
                            break;
                        case "DATE":
                            if (currentIndividual != null) {

                                if(tag != null) {

                                    if(tag == "BIRT") {
                                        currentIndividual.setBirthday(tokens[2] + "-" + tokens[3] + "-" + tokens[4]);

                                        tag = null;
                                    }
                                    else if(tag == "DEAT"){
                                        currentIndividual.setDeathDate(tokens[2] + "-" + tokens[3] + "-" + tokens[4]);

                                        tag = null;
                                    }
                                    else if(tag == "MARR"){
                                        currentFamily.setMarriageDate(tokens[2] + "-" + tokens[3] + "-" + tokens[4]);

                                        tag = null;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Individuals:");
        for (Individual individual : individuals) {
            System.out.println("INDI:" + individual.getId() + " NAME:" + individual.getName() + " SEX:" + individual.getSex()
            + " BIRTH:" + individual.getBirthday() + " ALIVE:" + individual.getAlive() + " DEATH:" + individual.getDeathDate()+"\n"
            );
        }

        System.out.println("Families:");
        for (Family family : families) {
            System.out.println("FAM:"+family.getId() + " MARR:" + family.getMarriageDate() + " DIV:" + family.getDivorce() +
                    " HUSB:" + family.getHusband().getId() + " " + family.getHusband().getName() + " WIFE:"
                    +family.getWife().getId() + " " +family.getWife().getName() + " CHIL:" + family.getChildren() +"\n");
        }
    }
}

class Individual {
    public String id;
    private String name;
    private String lastName;
    private String sex;
    private String alive = "Y";
    private String birthday;
    private String deathDate = "NA";


    public Individual(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name + " " + lastName;
    }

    public String getSex() {
        return sex;
    }

    public String getAlive() {
        return alive;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAlive(String alive) {
        this.alive = alive;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }
}

class Family {
    private String id;
    private Individual husband;
    private Individual wife;
    private String marriageDate;
    private String divorce;
    private ArrayList<Individual> children = new ArrayList<>();

    public Family(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMarriageDate() {
        return marriageDate;
    }

    public String getDivorce() {
        return divorce;
    }

    public Individual getHusband() {
        return husband;
    }

    public Individual getWife() {
        return wife;
    }

    public String getChildren() {
        String childrenIds = "{";
        for(int i = 0;i < children.size();i++){
            if(i != 0){
                childrenIds = childrenIds +"," + children.get(i).id;
            }
            childrenIds = childrenIds + children.get(i).id;
        }
        childrenIds = childrenIds + "}";
        return childrenIds;
    }

    public void setHusband(Individual husband) {
        this.husband = husband;
    }

    public void setWife(Individual wife) {
        this.wife = wife;
    }

    public void setMarriageDate(String marriageDate) {
        this.marriageDate = marriageDate;
    }

    public void setDivorce(String divorce) {
        this.divorce = divorce;
    }

    public void addChild(Individual child) {
        this.children.add(child);
    }

}