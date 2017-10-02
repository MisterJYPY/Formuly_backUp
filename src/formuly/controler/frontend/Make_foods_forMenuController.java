/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formuly.controler.frontend;

import formuly.classe.Fx_formuly;
import formuly.classe.RetentionAlments;
import formuly.classe.formulyTools;
import formuly.entities.FmGroupeAliment;
import formuly.model.frontend.mainModel;
import formuly.model.frontend.modelFoodSelect;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import formuly.classe.TooltipTableRow ;
import formuly.classe.alimentRepasModel;
import formuly.classe.bilanMacroNut;
import formuly.classe.repasModel;
import formuly.entities.FmAliments;
import formuly.entities.FmAlimentsPathologie;
import formuly.entities.FmRepas;
import formuly.entities.FmRepasAliments;
import formuly.entities.FmRetentionMineraux;
import formuly.entities.FmRetentionNutriments;
import formuly.entities.FmRetentionVitamines;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mr_JYPY
 */
public class Make_foods_forMenuController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML private TableView<mainModel> table_aliment_a_choisir;
    @FXML private TableView<mainModel> table_aliment_deja_choisi;
    @FXML private TableColumn<mainModel,String> nomAlimentsChoisi;
    @FXML private TableColumn<mainModel,String> nomAliment;
    @FXML private TableColumn<mainModel,String> quantite;
    @FXML private TableColumn<mainModel,String> quantiteChoisi;
    @FXML private TableColumn<mainModel,Integer> number;
    @FXML private ComboBox categorie_Foods;
    @FXML private ComboBox  pays_foods;
    @FXML private ComboBox  mode_cuisson;
    @FXML private TextField  nom_aliment;
    @FXML private TextField  code_aliment;
    @FXML private Button  envoi;
    @FXML private Button  fermerFentre;
    @FXML private Button reinitialiser;
    @FXML private Button validerMenu;
    @FXML private Button buttonExpert;
    @FXML private PieChart pieCharts;
    @FXML private Label labelAttention;
    @FXML private Tooltip toolTipAttention;
    ObservableList<Data> piecharList;
    ObservableList<bilanMacroNut> bilanList;
    Thread VerificationPathologie;
    private boolean travailEnregistre;
    private final modelFoodSelect model;
    ObservableList<alimentRepasModel> liste;

    public Make_foods_forMenuController(repasModel repasM,ObservableList<alimentRepasModel> list)
    {
     travailEnregistre=false;
        model=new modelFoodSelect();
        piecharList=FXCollections.observableArrayList();
        bilanList=FXCollections.observableArrayList();
        data0=new Data("",0);
        data1=new Data("",0);
        data2=new Data("",0);
    Double[] val={EnergieTotalePrEnregister,retentionGlucide,retentionLipide,retentionProtide};
         initialisationValeurs(val);
        idRepasAliment=formulyTools.TrouverDernierIdentifiant_Repas_aliment()+1;
        idRepas=formulyTools.TrouverDernierIdentifiant_Repas()+1;
        libelle="";
        repasAlCtr=new FmRepasAlimentsJpaController(formulyTools.getEm());
        repasCont=new FmRepasJpaController(formulyTools.getEm());
        alimenCtr=new FmAlimentsJpaController(formulyTools.getEm());
       // initialiserLesElementsDepuisLeRepas(list);
     //   liste=FXCollections.observableArrayList();
         liste=list;
    }
    public   ObservableList<mainModel> chargerTableListeAliments(ObservableList<alimentRepasModel> liste)
    {
      ObservableList<mainModel> obsL=FXCollections.observableArrayList();
        mainModel model=null;
        for(int i=0;i<liste.size();i++)
        {
            alimentRepasModel alRepas=liste.get(i);
            int idAliment=alRepas.getId_aliment();
            model=getobservableListMainModel(idAliment);
            model.setQte(alRepas.getQuantite().toString());
            obsL.add(model);
        }
        return obsL;
    }
    public void initialiserLesElementsDepuisLeRepas(ObservableList<alimentRepasModel> liste)
    {
      ObservableList<mainModel> obsL=chargerTableListeAliments(liste);
      faireActionExtraction(obsL);
    }
     public mainModel getobservableListMainModel(int idAliment)
  {   
      mainModel mainM=null;
     List<RetentionAlments> retentionAliment= RetentionAlments.getAlimentRetentionForAliment(idAliment);
      if(retentionAliment!=null)
      {
            int cpt=1;
            RetentionAlments   ret=retentionAliment.get(0);
            FmAliments aliment=ret.getAliments();
            FmRetentionMineraux mine=ret.getRm();
            FmRetentionNutriments nutr=ret.getRn();
            FmRetentionVitamines vit=ret.getRvtm();
//             mainM=new mainModel(cpt,aliment.getNomFr(),null,(double)nutr.getGlucide(),null,(double)nutr.getLipide(),null, (double)nutr.getProtein(),null,(double)nutr.getEnergieKcal(),(double)mine.getFe(),(double)mine.getMg(),(double)mine.getNa(),(double)mine.getPota(), (double)vit.getVitc(),(double)vit.getVite(),null,(double)vit.getVita());
           // System.out.println(aliment.getSurnom());
            mainM=new mainModel(cpt,("aucun".equals(aliment.getSurnom()))?aliment.getNomFr():aliment.getSurnom(),(nutr==null)?0.0:nutr.getGlucide(),0.0,(nutr==null)?0.0:nutr.getLipide(),0.0,(nutr==null)?0.0:nutr.getProtein(),0.0,(nutr==null)?0.0:nutr.getEnergieKcal(),(mine==null)?0.0:mine.getFe(),(mine==null)?0.0:mine.getMg(),(mine==null)?0.0:mine.getNa(),(mine==null)?0.0:mine.getPota(),(vit==null)?0.0:vit.getVitc(),(vit==null)?0.0:vit.getVite(),(vit==null)?0.0:vit.getFolates(),(vit==null)?0.0:vit.getVita());
            mainM.setIdAliment(aliment.getId());
            mainM.setNumero(cpt);
            mainM.setNom_aliment(aliment.getNomFr());
            mainM.setPays(aliment.getPays());
        } 
     return mainM;
  }  
    public Make_foods_forMenuController() {
        travailEnregistre=false;
        model=new modelFoodSelect();
        piecharList=FXCollections.observableArrayList();
        bilanList=FXCollections.observableArrayList();
        data0=new Data("",0);
        data1=new Data("",0);
        data2=new Data("",0);
    Double[] val={EnergieTotalePrEnregister,retentionGlucide,retentionLipide,retentionProtide};
        initialisationValeurs(val);
        idRepasAliment=formulyTools.TrouverDernierIdentifiant_Repas_aliment()+1;
        idRepas=formulyTools.TrouverDernierIdentifiant_Repas()+1;
        libelle="";
        repasAlCtr=new FmRepasAlimentsJpaController(formulyTools.getEm());
        repasCont=new FmRepasJpaController(formulyTools.getEm());
        alimenCtr=new FmAlimentsJpaController(formulyTools.getEm());
        liste.clear();
    }
    public void visibiliteBoutonAnalyse()
    {
        if(table_aliment_deja_choisi.getItems().size()>0)
        {
        buttonExpert.setVisible(true);
        }
        else
        {
        buttonExpert.setVisible(false);
        }
    }
      public String formatageInterdi(ObservableList<FmAlimentsPathologie> list)
      {
        String content="";
        if(list.size()>0)
        {
       content=content.concat(" aliments Interdits: \n");
          for(FmAlimentsPathologie liste :list)
          {
           String nom=(!"aucun".equals(liste.getAliment().getSurnom()))?liste.getAliment().getSurnom():liste.getAliment().getNomFr();
           String pathologie=liste.getPathologie().getLibelle();
           String ligne=nom.concat(" :pat: "+pathologie);
          content=content.concat(ligne+"\n");
          }
        }
        return content;
      }
         public void TraiterInterdi()
         {
   ObservableList<FmAlimentsPathologie>  list=verificationPathologie(table_aliment_deja_choisi.getItems());
             System.out.println("taille: "+list.size());
             String info="";
        info = formatageInterdi(list);
    String lesElement="";
        if(list.size()>0)
         {      
             System.out.println("on est la");
              Image image = new Image(
    getClass().getResourceAsStream("/formuly/image/war.jpg")
     );
         labelAttention.setText("Attention");
         labelAttention.setGraphic(new ImageView(image));
         labelAttention.setStyle(" -fx-background-color:linear-gradient(to top right,white,red,red,white);");
         toolTipAttention.setText(info );
         toolTipAttention.setGraphic(new ImageView(image));  
          labelAttention.setVisible(true);
         }
       else
              {
         labelAttention.setText("");
         labelAttention.setStyle(" -fx-background-color:white");
         labelAttention.setVisible(false);
              }
         }
              
      @Override
      public void initialize(URL url, ResourceBundle rb) {
   
          Button[] btn={envoi,fermerFentre,validerMenu,buttonExpert};
        visibiliteBoutonAnalyse();
          formulyTools.mettreEffetButton(btn);
          mettreLesToolTip(table_aliment_a_choisir, table_aliment_deja_choisi,tableBilan);
          actionBoutonFermer();
          table_aliment_a_choisir.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
          initialisationCombobox();
         initialiserLeTableauAchoisir();
          if(liste.size()>0)
          {
              System.out.println("JY SUIS ");
        initialiserLesElementsDepuisLeRepas(liste);
          }
          else{
           System.out.println("JY SUIS PAS"); 
          }
       nom_aliment.setOnKeyReleased(
     event->{
          String sql="";
             String nom_ali=nom_aliment.getText().replaceAll("'", "_");
           String sqlnomA= "select f.id,f.nom_fr ,f.code,f.pays from fm_aliments f WHERE (f.nom_fr LIKE "+"'%"+nom_ali+"%' or f.nom_eng LIKE "+"'%"+nom_ali+"%' or f.surnom LIKE "+"'%"+nom_ali+"%')  "; 
        String sqlcate="";
        String sqlmodec="";
        String sqlpays="";
         String sqlcode="";
        String sql1="";
                    if(categorie_Foods.getValue()!=null)
                    {
                String groupe=categorie_Foods.getValue().toString();
             FmGroupeAlimentJpaController gp=new FmGroupeAlimentJpaController(model.emf);
             FmGroupeAliment grpe=gp.findFmGroupeAlimentByName(groupe);
             int idGrouep=grpe.getId();
                sqlcate="and f.groupe="+idGrouep+" ";   
                    }
                     if(!code_aliment.getText().isEmpty())
                    {      
                          String val=code_aliment.getText().replaceAll("'", "_"); 
                sqlcode= "and f.code LIKE "+"'%"+val+"%' ";
                    }
                      if(pays_foods.getValue()!=null)
                    {
                   String pays=pays_foods.getValue().toString();
                sqlpays="and f.pays LIKE "+"'%"+pays+"%' ";   
                    }
                       if(mode_cuisson.getValue()!=null)
                    {
                   String mode_cuiss=mode_cuisson.getValue().toString();
                sqlmodec="and f.mode_cuisson="+"'"+mode_cuiss+"'";   
                    }
                   sql= sqlnomA.concat(sqlcate).concat(sqlcode).concat(sqlpays).concat(sqlmodec);
                   //System.out.println(sql1);
             
             if(!sql.isEmpty())
             {
                 //System.out.println(sql);
                initialiserLeTableauAchoisir(sql,"");   
             }
     //  }
     } );
       
       code_aliment.setOnKeyReleased(
        event->{
        //  if(!code_aliment.getText().isEmpty()){
            String sql="";
          String val=code_aliment.getText().replaceAll("'", "_");
           String sqlcode= "select f.id,f.nom_fr ,f.code,f.pays from fm_aliments f WHERE f.code LIKE "+"'%"+val+"%' ";
        
        String sqlcate="";
        String sqlmodec="";
        String sqlpays="";
        String sqlnomA="";
        String sql1="";
                    if(categorie_Foods.getValue()!=null)
                    {
                String groupe=categorie_Foods.getValue().toString();
             FmGroupeAlimentJpaController gp=new FmGroupeAlimentJpaController(model.emf);
             FmGroupeAliment grpe=gp.findFmGroupeAlimentByName(groupe);
             int idGrouep=grpe.getId();
                sqlcate="and f.groupe="+idGrouep+" ";   
                    }
                     if(!nom_aliment.getText().isEmpty())
                    {
                        String nom_ali=nom_aliment.getText().replaceAll("'", "_");
                sqlnomA="and (f.nom_fr LIKE "+"'%"+nom_ali+"%' or f.nom_eng LIKE "+"'%"+nom_ali+"%' or f.surnom LIKE "+"'%"+nom_ali+"%') ";   
                    }
                      if(pays_foods.getValue()!=null)
                    {
                   String pays=pays_foods.getValue().toString();
                sqlpays="and f.pays LIKE "+"'%"+pays+"%' ";   
                    }
                       if(mode_cuisson.getValue()!=null)
                    {
                   String mode_cuiss=mode_cuisson.getValue().toString();
                sqlmodec="and f.mode_cuisson="+"'"+mode_cuiss+"'";   
                    }
                   sql= sqlcode.concat(sqlcate).concat(sqlnomA).concat(sqlpays).concat(sqlmodec);
                   System.out.println(sql1);
             
             if(!sql.isEmpty())
             {
               initialiserLeTableauAchoisir(sql,"");   
             }
         // }
        });
    }    
      public void mettreLesToolTip(TableView<mainModel> table_aliment_a_choisir,TableView<mainModel> table_aliment_deja_choisi,TableView<bilanMacroNut>... table)
      {
         table_aliment_a_choisir.setRowFactory((tableView) -> {
      return new  TooltipTableRow<mainModel>((mainModel model) -> {
        return model.getNom_aliment();
      });
});
          table_aliment_deja_choisi.setRowFactory((tableView) -> {
      return new  TooltipTableRow<mainModel>((mainModel model) -> {
        return model.getNom_aliment();
      });
});
           table[0].setRowFactory((tableView) -> {
      return new  TooltipTableRow<bilanMacroNut>((bilanMacroNut model) -> {
        return ""+model.getAliment()+" ,Energie: "+model.getValeurEnergie();
      });
});
      }
      public void initialisationCombobox()
    {
       //initialisation des pays
       
     List<String> list = new ArrayList<String>();
         list.add("General");
        list.add("Cote Ivoire");
        list.add("Mali");
        list.add("Nigeria");
        list.add("Ghana");
        list.add("Gambie");
        list.add("Nigeria");
        list.add("Niger");
        list.add("Senegal");
        list.add("Guinee");
        list.add("Ghana");
        list.add("Benin");
        ObservableList obList = FXCollections.observableList(list);
        pays_foods.getItems().clear();
        pays_foods.setItems(obList);
        
        //initialisation du mode de cuisson
        ObservableList<String> groupe=model.listeDesGroupesAliments();
        ObservableList<String> ModeCuisson=model.listeDesMode_cuisson();
        
        categorie_Foods.getItems().clear();
        mode_cuisson.getItems().clear();
        categorie_Foods.setItems(groupe);
        mode_cuisson.setItems(ModeCuisson);
         
    }
      public void initialiserLeTableauAchoisir()
      {
          
      nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
      quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
      number.setCellValueFactory(new PropertyValueFactory<>("numero")); 
      
        table_aliment_a_choisir.setEditable(true);
      
       quantite.setCellFactory(TextFieldTableCell.forTableColumn());
       quantite.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<mainModel, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<mainModel, String> t) {
                        String qte=formulyTools.preformaterChaine(t.getNewValue());
                    if(Double.parseDouble(qte)>0.0)
                 {
                     
                    ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(formulyTools.preformaterChaine(t.getNewValue()));
                }   
                    else
                    {
                 
                ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte("0");    
                    }
                int ligne= t.getTablePosition().getRow();
               mainModel md= table_aliment_a_choisir.getItems().get(ligne);
               table_aliment_a_choisir.getItems().set(ligne, md);
                    int i=0;
                     for (mainModel run :  table_aliment_a_choisir.getItems()) {
                         if(Double.parseDouble(run.getQte())>0.0)
                         {                           
                      table_aliment_a_choisir.getSelectionModel().select(i);  
                         }
                         i++;
                }
      }
            }
        );
       //mettre les toolTip
       
       //Fin
        table_aliment_a_choisir.setOnMousePressed(new EventHandler<MouseEvent>() {
    @Override 
   public void handle(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (node instanceof TableRow) {
                row = (TableRow) node;
            } else {
                // clicking on text part
                row = (TableRow) node.getParent();
            }
          if(row.getItem() instanceof mainModel)
          {
           int index=table_aliment_a_choisir.getSelectionModel().getSelectedIndex();  
         
            int i=0;
                     for (mainModel run :  table_aliment_a_choisir.getItems()) {
                         if(Double.parseDouble(run.getQte())>0.0)
                         {                           
                      table_aliment_a_choisir.getSelectionModel().select(i);  
                         }
                         i++;
                         //  table_aliment_a_choisir.getSelectionModel().clearSelection(index);
                         
                }
          }
        }
    }
});
         

       
        table_aliment_a_choisir.setItems(formulyTools.getobservableListMainModel(1));
      }
      public void rechercher(ActionEvent e)
    {
        Object obj=e.getSource();
        if(e.getSource().equals(categorie_Foods) && categorie_Foods.getValue()!=null)
        {   
             String groupe=categorie_Foods.getValue().toString();
             FmGroupeAlimentJpaController gp=new FmGroupeAlimentJpaController(model.emf);
             FmGroupeAliment grpe=gp.findFmGroupeAlimentByName(groupe);
             int idGrouep=grpe.getId();
          String sqlcate="select f.id,f.nom_fr ,f.code,f.pays from fm_aliments f WHERE f.groupe="+idGrouep+" ";
          String sql="";
         // String val=code_aliment.getText();
          String sqlcode= " ";     
          String sqlmodec="";
          String sqlpays="";
          String sqlnomA="";
          String sql1="";
                    if(!code_aliment.getText().isEmpty())
                    {
                  String code=code_aliment.getText().replaceAll("'", "_");
         sqlcode= "and f.code LIKE "+"'%"+code+"%' "; 
                    }
                     if(!nom_aliment.getText().isEmpty())
                    {
                        String nom_ali=nom_aliment.getText().replaceAll("'", "_");
                sqlnomA="and (f.nom_fr LIKE "+"'%"+nom_ali+"%' or f.nom_eng LIKE "+"'%"+nom_ali+"%' or f.surnom LIKE "+"'%"+nom_ali+"%') ";   
                    }
                      if(pays_foods.getValue()!=null)
                    {
                   String pays=pays_foods.getValue().toString();
                sqlpays="and f.pays LIKE "+"'%"+pays+"%' ";   
                    }
                       if(mode_cuisson.getValue()!=null)
                    {
                   String mode_cuiss=mode_cuisson.getValue().toString();
                sqlmodec="and f.mode_cuisson="+"'"+mode_cuiss+"'";   
                    }
                   sql= sqlcate.concat(sqlcode).concat(sqlnomA).concat(sqlpays).concat(sqlmodec);
                   System.out.println(sql1);
             
             if(!sql.isEmpty())
             {
               initialiserLeTableauAchoisir(sql,"");   
             }
        }
        
         if(e.getSource().equals(mode_cuisson) && mode_cuisson.getValue()!=null)
         {
           String mode_cuiss=mode_cuisson.getValue().toString();
          String   sqlmodec="select f.id,f.nom_fr ,f.code,f.pays from fm_aliments f WHERE f.mode_cuisson="+"'"+mode_cuiss+"' ";   
          String sqlcate="";
          String sql="";
         // String val=code_aliment.getText();
          String sqlcode= "";     
          String sqlpays="";
          String sqlnomA="";
          String sql1="";
                    if(!code_aliment.getText().isEmpty())
                    {
          String code=code_aliment.getText().replaceAll("'", "_");
         sqlcode= "and f.code LIKE "+"'%"+code+"%' "; 
                    }
                     if(!nom_aliment.getText().isEmpty())
                    {
          String nom_ali=nom_aliment.getText().replaceAll("'", "_");
                sqlnomA="and (f.nom_fr LIKE "+"'%"+nom_ali+"%' or f.nom_eng LIKE "+"'%"+nom_ali+"%' or f.surnom LIKE "+"'%"+nom_ali+"%') ";   
                    }
                      if(pays_foods.getValue()!=null)
                    {
          String pays=pays_foods.getValue().toString();
                sqlpays="and f.pays LIKE "+"'%"+pays+"%' ";   
                    }
                       if(categorie_Foods.getValue()!=null)
                    {
          String groupe=categorie_Foods.getValue().toString();
             FmGroupeAlimentJpaController gp=new FmGroupeAlimentJpaController(model.emf);
             FmGroupeAliment grpe=gp.findFmGroupeAlimentByName(groupe);
             int idGrouep=grpe.getId();
          sqlcate="and f.groupe="+idGrouep+" "; 
                    }
                   sql=sqlmodec.concat(sqlcode).concat(sqlnomA).concat(sqlpays).concat(sqlcate);
                 
             
             if(!sql.isEmpty())
             {
               initialiserLeTableauAchoisir(sql,"");   
             }
         }
           if(e.getSource().equals(pays_foods) && pays_foods.getValue()!=null)
         {
          String pays=pays_foods.getValue().toString();
          String sqlpays="select f.id,f.nom_fr ,f.code,f.pays from fm_aliments f WHERE f.pays LIKE "+"'%"+pays+"%' ";         
          String   sqlmodec="";   
          String sqlcate="";
          String sql="";
          String sqlcode= "";     
          String sqlnomA="";
          String sql1="";
                    if(!code_aliment.getText().isEmpty())
                    {
                  String code=code_aliment.getText().replaceAll("'", "_");
         sqlcode= "and f.code LIKE "+"'%"+code+"%' "; 
                    }
                     if(!nom_aliment.getText().isEmpty())
                    {
                 String nom_ali=nom_aliment.getText().replaceAll("'", "_");
                sqlnomA="and (f.nom_fr LIKE "+"'%"+nom_ali+"%' or f.nom_eng LIKE "+"'%"+nom_ali+"%' or f.surnom LIKE "+"'%"+nom_ali+"%') ";   
                    }
                      if(mode_cuisson.getValue()!=null)
                    {
                 String mode_cuiss=mode_cuisson.getValue().toString();
              sqlmodec="and f.mode_cuisson="+"'"+mode_cuiss+"' ";  
                    }
                       if(categorie_Foods.getValue()!=null)
                    {
                 String groupe=categorie_Foods.getValue().toString();
             FmGroupeAlimentJpaController gp=new FmGroupeAlimentJpaController(model.emf);
             FmGroupeAliment grpe=gp.findFmGroupeAlimentByName(groupe);
             int idGrouep=grpe.getId();
          sqlcate="and f.groupe="+idGrouep+" "; 
                    }
                   sql=sqlpays.concat(sqlcode).concat(sqlnomA).concat(sqlmodec).concat(sqlcate);
             if(!sql.isEmpty())
             {
               initialiserLeTableauAchoisir(sql,"");   
             }
        }
    }
      public void initialiserLeTableauAchoisir(String NameQuery,String champ ,Object parametre)
      {
          
      nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
      quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
      number.setCellValueFactory(new PropertyValueFactory<>("numero")); 
       table_aliment_a_choisir.setEditable(true);
       quantite.setCellFactory(TextFieldTableCell.forTableColumn());
       quantite.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<mainModel, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<mainModel, String> t) {
                    ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(formulyTools.preformaterChaine(t.getNewValue()));
                }
            }
        );
        table_aliment_a_choisir.setItems(retournerObservableListNonDoublon(formulyTools.getobservableListMainModel(NameQuery, champ, parametre),table_aliment_deja_choisi.getItems(),""));
      }
      public void initialiserLeTableauAchoisir(String NameQuery,String champ)
      {
          
      nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
      quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
      number.setCellValueFactory(new PropertyValueFactory<>("numero")); 
      
       table_aliment_a_choisir.setEditable(true);
       quantite.setCellFactory(TextFieldTableCell.forTableColumn());
       quantite.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<mainModel, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<mainModel, String> t) {
                     ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(formulyTools.preformaterChaine(t.getNewValue()));
                }
            }
        );
        table_aliment_a_choisir.setItems(retournerObservableListNonDoublon(formulyTools.getobservableListMainModel(NameQuery,model,""),table_aliment_deja_choisi.getItems(),""));
      }
       public void faireActionExtraction(ObservableList<mainModel> obsL)
     {
         if(obsL.size()>0)
          {
                
        nomAlimentsChoisi.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
        quantiteChoisi.setCellValueFactory(new PropertyValueFactory<>("qte")); 
        nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
        quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
          rendreCelluleEditable(table_aliment_deja_choisi,quantiteChoisi,"");
          initialisationCombobox();
          initialiserJtextField();
           table_aliment_deja_choisi.setItems(obsL);
         //  table_aliment_a_choisir.setItems(retournerObservableListNonDoublon(table_aliment_a_choisir.getItems(),table_aliment_deja_choisi.getItems()));
                supprimerElementDeLaListeen2click(table_aliment_deja_choisi);
                LoadObservableList(obsL,bilanList,tableBilan);
                bilanGeneral(bilanList, pieCharts,piecharList);
                 TraiterInterdi();
                visibiliteBoutonAnalyse();   
           
      }
         else{
             System.out.println("les elements st vides");
         }
        
     }
     public void faireActionEnvoi(ObservableList<mainModel> obsL)
     {
         if(obsL.size()>0)
          {
            if(table_aliment_deja_choisi.getItems().size()==0)
            {
                    
        nomAlimentsChoisi.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
        quantiteChoisi.setCellValueFactory(new PropertyValueFactory<>("qte")); 
        nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
        quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
          rendreCelluleEditable(table_aliment_deja_choisi,quantiteChoisi,"");
          initialisationCombobox();
          initialiserJtextField();
           table_aliment_deja_choisi.setItems(obsL);
           table_aliment_a_choisir.setItems(retournerObservableListNonDoublon(table_aliment_a_choisir.getItems(),table_aliment_deja_choisi.getItems()));
           supprimerElementDeLaListeen2click(table_aliment_deja_choisi);
                LoadObservableList(obsL,bilanList,tableBilan);
                bilanGeneral(bilanList, pieCharts,piecharList);
                 TraiterInterdi();
                visibiliteBoutonAnalyse();
         }
            else{
         //  table_aliment_a_choisir 
                obsL.clear();
                 for (mainModel run :  table_aliment_a_choisir.getItems()) {
                         if(Double.parseDouble(run.getQte())>0.0)
                         {                           
                       obsL.add(run);
                         }
                     }
                 nomAlimentsChoisi.setCellValueFactory(
            new PropertyValueFactory<mainModel,String>("nom_aliment")
        );
                 quantiteChoisi.setCellValueFactory(
            new PropertyValueFactory<mainModel,String>("qte")
        );
            nomAliment.setCellValueFactory(new PropertyValueFactory<>("nom_aliment")); 
        quantite.setCellValueFactory(new PropertyValueFactory<>("qte")); 
        table_aliment_a_choisir.setEditable(true);
       rendreCelluleEditable(table_aliment_deja_choisi,quantiteChoisi,"");
        table_aliment_deja_choisi.getItems().addAll(retournerObservableListNonDoublon(obsL,table_aliment_deja_choisi.getItems(),""));    
       table_aliment_a_choisir.setItems(retournerObservableListNonDoublon(table_aliment_a_choisir.getItems(),table_aliment_deja_choisi.getItems()));
          supprimerElementDeLaListeen2click(table_aliment_deja_choisi);
             LoadObservableList(obsL,bilanList,tableBilan);
                bilanGeneral(bilanList, pieCharts,piecharList);
                  TraiterInterdi();
             visibiliteBoutonAnalyse();
          
            }
      }
        else{
          Alert alert = new Alert(AlertType.WARNING); 
          alert.setHeaderText("Aucun Aliment selectionner");
          alert.setContentText("\3s Veuillez selectionner des aliments avant");
          alert.show();
//          VerificationPathologie.start();
          
          }
     }
      public void envoi(ActionEvent event)
      {
             ObservableList<mainModel> obsL=FXCollections.observableArrayList();
              for (mainModel run :  table_aliment_a_choisir.getItems()){
                         if(Double.parseDouble(run.getQte())>0.0)
                         {                           
                       obsL.add(run);
                         }
                     }
                       faireActionEnvoi(obsL);
      }
      public ObservableList<mainModel>  retournerObservableListNonDoublon(ObservableList<mainModel> ob1,ObservableList<mainModel> ob2)
      {
          if(ob2.size()>0)
          {
             ob1.removeAll(ob2);
          }
            return ob1;
      }
       public ObservableList<mainModel>  retournerObservableListNonDoublon(ObservableList<mainModel> ob1,ObservableList<mainModel> ob2,String s)
      {
         
          if(ob2.size()>0)
          {
              int i=0;
               for(int k=0;k<ob1.size();k++)   
           {      
               mainModel md1=ob1.get(k);
               for(int l=0;l<ob2.size();l++)
           {
               mainModel md2=ob2.get(l);
             if(md1.getIdAliment()==md2.getIdAliment())
             { 
              ob1.remove(k);
             }
            
           } 
           }
          }
            return ob1;
      }
       public void supprimerElementDeLaListeen2click(TableView<mainModel> table_aliment_deja_choisi)
       {
            table_aliment_deja_choisi.setOnMousePressed(new EventHandler<MouseEvent>() {
    @Override 
   public void handle(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            Node node = ((Node) event.getTarget()).getParent();
            TableRow row;
            if (node instanceof TableRow) {
                row = (TableRow) node;
            } else {
                // clicking on text part
                row = (TableRow) node.getParent();
            }
          if(row.getItem() instanceof mainModel)
          {
           int index=table_aliment_deja_choisi.getSelectionModel().getSelectedIndex(); 
           String selection = table_aliment_deja_choisi.getSelectionModel().getSelectedItem().getNom_aliment();
           String quantite= table_aliment_deja_choisi.getSelectionModel().getSelectedItem().getQte();
               Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Dialogue de confirmation");
            alert.setHeaderText("Informations supplementaires relatif à la suppression");
            alert.setContentText("Nom aliment: "+selection+" Quantite :"+quantite+"\n"
                    + "VOULEZ VOUS VRAIMENT LE SUPPRIMER ?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
       if (alert.getResult() == ButtonType.YES) {
          mainModel md=table_aliment_deja_choisi.getItems().get(index);
           table_aliment_deja_choisi.getItems().remove(index);
                md.setQte("0");
           table_aliment_a_choisir.getItems().add(md);
              bilanList.clear();
              LoadObservableList(table_aliment_deja_choisi.getItems(),bilanList,tableBilan);
              bilanGeneral(bilanList, pieCharts,piecharList);
              TraiterInterdi();
              visibiliteBoutonAnalyse();
      }
          }
        }
    }
});
       }
      public void initialiserJtextField()
      {
           int i=0;
        for (mainModel run :  table_aliment_a_choisir.getSelectionModel().getSelectedItems()) {
                     // System.out.println(run);
                    
                      table_aliment_a_choisir.getSelectionModel().select(run);
                      i++;
               }
          code_aliment.setText("");
          nom_aliment.setText("");
          //initialiserLeTableauAchoisir();
      
      }
      public void rendreCelluleEditable(TableView<mainModel> table ,TableColumn<mainModel,String> colonne )
      {
      table.setEditable(true);
        colonne.setCellFactory(TextFieldTableCell.forTableColumn());
             colonne.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<mainModel, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<mainModel, String> t) {
                    String qte=formulyTools.preformaterChaine(t.getNewValue());
                     int ligne= t.getTablePosition().getRow();
               mainModel md= table.getItems().get(ligne);
               table.getItems().set(ligne, md);
                     t.getTableView().getItems().get(ligne).setQte(qte);
                     ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(qte);
                     
                }
            }
        );
      }
        public void rendreCelluleEditable(TableView<mainModel> table ,TableColumn<mainModel,String> colonne ,String valeurFictive)
      {
      table.setEditable(true);
        colonne.setCellFactory(TextFieldTableCell.forTableColumn());
             colonne.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<mainModel, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<mainModel, String> t) {
                    String qte=formulyTools.preformaterChaine(t.getNewValue());
                     int ligne= t.getTablePosition().getRow();
                     double qt=Double.valueOf(qte);
                            mainModel md= table.getItems().get(ligne);
               if(qt>0){
        
               table.getItems().set(ligne, md);
                   //  t.getTableView().getItems().get(ligne).setQte(qte);
                     ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(qte);
                }
              else{
       //alert pour dire la valeur est nulle nous allons la supprimer ?  
                   Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Suppression Aliment Choisi");
            alert.setHeaderText("la suppression d'un aliment pour valeur Quantite Interdite: "+t.getNewValue()+" \n");
            alert.setContentText("Nom aliment: "+md.getNom_aliment()+" Quantite :"+qte+"\n"
                    + "OK Supprimera la ligne ,decliner pour conserver l'encienne valeur");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
       if (alert.getResult() == ButtonType.YES) {
         table_aliment_deja_choisi.getItems().remove(ligne);
         table_aliment_a_choisir.getItems().add(md);
        }
       else{
         table.getItems().set(ligne, md);
                    // t.getTableView().getItems().get(ligne).setQte(qte);
                     ((mainModel) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setQte(md.getQte());
       }
               }
               bilanList.clear();
              LoadObservableList(table.getItems(),bilanList,tableBilan);
              bilanGeneral(bilanList, pieCharts,piecharList);
               TraiterInterdi();
               visibiliteBoutonAnalyse();
                
                }
            }
        );
      }

    public Button getValiderMenu() {
        return validerMenu;
    }

    public TableView<mainModel> getTable_aliment_deja_choisi() {
        return table_aliment_deja_choisi;
    }
    
  public void initialisationPieChart()
  {
   piecharList.addAll(new PieChart.Data("qte Glucide (189)", 189),
            new PieChart.Data("qte protide(890)",890),
            new PieChart.Data("qte Lipide(400)",400)
           
            ); 
          pieCharts.setTitle("Composition en Macro Nutriment(quantite)");
          pieCharts.setLegendVisible(true);
          pieCharts.setData(piecharList);
          }
  /**
   * methode permettant de faire le Bilan des elements
   * elle calcul les valeurs energetiques totales et affiche dans les labels voulu
   * @param bilanElements la liste pleine du bilan deja Pre-enregistré
   * @param pchart   le peChart qui acceuille le resulat
   * @param PieChartData  la liste devant contenir les valeurs du Piechart
   */
   public void bilanGeneral(ObservableList<bilanMacroNut> bilanElements,PieChart pchart,ObservableList<Data> ... PieChartData)
   {
      
       double sommeLipide=0;
       double sommeGlucide=0;
       double sommeProtide=0;
       double sommeEnergie=0;
      for(bilanMacroNut main: bilanElements)
         {
        sommeLipide=sommeLipide+main.getValeurLipide();
        sommeGlucide=sommeGlucide+main.getValeurGlucide();
        sommeProtide=sommeProtide+main.getValeurProtide();
        sommeEnergie=sommeEnergie+main.getValeurEnergie();
         }
       double EnergieTotaleLipide= (sommeLipide*9);
       double EnergieTotalGlucide= (sommeGlucide*4);
       double EnergieTotalProtide= (sommeProtide*4);
       double EnergieTotale=((EnergieTotaleLipide)+(EnergieTotalGlucide)+(EnergieTotalProtide));
       double aetLipide=(EnergieTotaleLipide/EnergieTotale)*100;
       double aetProtide=(EnergieTotalProtide/EnergieTotale)*100;
       double aetGlucide=(EnergieTotalGlucide/EnergieTotale)*100;
         //transformer en caractere
       NumberFormat format=NumberFormat.getInstance();
            format.setMaximumFractionDigits(2); 
        String energiteTot=(bilanElements.size()>0 && EnergieTotale>0)?format.format(EnergieTotale):"0";
        String aetLip=(bilanElements.size()>0 && aetLipide>0)?format.format(aetLipide):"0";
        String aetGl=(bilanElements.size()>0 && aetGlucide>0)?format.format(aetGlucide):"0";
        String aetPr=(bilanElements.size()>0 && aetProtide>0)?format.format(aetProtide):"0";
        double  prttGl=0;
        double  prttLp=0;
        double  prPrtd=0;
       if(sommeProtide+sommeLipide+sommeGlucide!=0){
          prttGl=(sommeGlucide/(sommeProtide+sommeLipide+sommeGlucide))*100;
        prttLp=(sommeLipide/(sommeProtide+sommeLipide+sommeGlucide))*100;
        prPrtd=(sommeProtide/(sommeProtide+sommeLipide+sommeGlucide))*100;
           System.out.println("lip:"+aetLipide+"prt: "+aetProtide+"Gl:"+aetGlucide+" enr: "+EnergieTotale);
            System.out.println("---------------------------------------------");
            System.out.println("lip:"+aetLip+"prt: "+aetPr+"Gl:"+aetGl+" enr: "+energiteTot);
           data0.setName("Gl");
           data0.setPieValue( prttGl);
           data1.setName("Pr");
           data1.setPieValue(prPrtd);
           data2.setName("Lp");
           data2.setPieValue(prttLp);
           
        if( PieChartData[0].size()==0)
        {
 PieChartData[0].addAll(data0,data1,data2);
  pchart.setTitle("Composition en Macro Nutriment(%)");
        }else{
             ObservableList<PieChart.Data> date=FXCollections.observableArrayList();
              date.addAll(new PieChart.Data("%Gl", prttGl),
            new PieChart.Data("%Pr",prPrtd),
            new PieChart.Data("%li",prttLp)
           
            );   
       pchart.setTitle("Composition en Macro Nutriment(%)");
       PieChartData[0].setAll(date);
        }
         
          }
          else{
           System.out.println("entrer");
         PieChartData[0].clear();
           pchart.setTitle("");
          }
        pchart.setData(PieChartData[0]);
        //assignation des valeurs pour les enregistrement
      EnergieTotalePrEnregister=EnergieTotale;
      retentionGlucide=prttGl;
      retentionLipide=prttLp;
      retentionProtide=prPrtd;
      //mise des elements dans les Label
        aetGlucides.setText(aetGl+" %");
        aetLipides.setText(aetLip+" %");
        aetProtides.setText(aetPr+" %");
        energieTotale.setText(energiteTot+" Kcal");
     // % des lipides ormatages des element
        String prcenProtide=(bilanElements.size()>0)?format.format(prPrtd) :"0";
        String prcenGlucide=(bilanElements.size()>0)?format.format(prttGl):"0";
        String prcenLipide=(bilanElements.size()>0)?format.format(prttLp):"0";
        prcentGlucide.setText( prcenGlucide+" %");
        prcentProtide.setText(prcenProtide+" %");
        prcentLipide.setText(prcenLipide+" %");
        // % des valeurs Energetiques
        String prcM=format.format((EnergieTotale/1000)*100);
        String prcMC=format.format((EnergieTotale/1500)*100);
        String prcDM=format.format((EnergieTotale/2000)*100);
        String prcDMC=format.format((EnergieTotale/2500)*100);
        String prcTM=format.format((EnergieTotale/3000)*100);
        String prcTMC=format.format((EnergieTotale/3500)*100);
        pcentMilCinq.setText(prcMC+" %");
        pcentTroisMillCinq.setText(prcMC+" %");
        pcentDeuxMill.setText(prcDM+" %");
        pcentDeuxMillCinq.setText(prcDMC+" %");
        pcentTroisMill.setText(prcTM+" %");
        pcentTroisMillCinq.setText(prcTMC+" %");
   }
   public void actionBoutonFermer()
   {
              fermerFentre.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
              if(table_aliment_deja_choisi.getItems().size()>0)
              {
               if(travailEnregistre)
               {
         Stage stage = (Stage) fermerFentre.getScene().getWindow();
    // do what you have to do
               stage.close();
               }
               else{
         Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Travail non enregistrer");
            alert.setHeaderText("Repas Non enregistrer \n");
            alert.setContentText("Le Travail n'as pas ete Enregistrer \n"
                    + "VOULEZ-VOUS VRAIMENT QUITTEZ ?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
       if (alert.getResult() == ButtonType.YES) {
           Stage stage = (Stage) fermerFentre.getScene().getWindow();
    // do what you have to do
               stage.close();
        }                
               }
              }
              
              else
              {
             Stage stage = (Stage) fermerFentre.getScene().getWindow();
    // do what you have to do
               stage.close();      
              }
             }});
              
               validerMenu.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
                 EnregistrerRepas();
                 travailEnregistre=true;
                 TextInputDialog dialog = new TextInputDialog("");
    dialog.setTitle("Insertion de Nom de MENU");
    dialog.setHeaderText("Veuillez renseigner ce champ");
    dialog.setContentText("Donner le nom de ce Menu :");

// Traditional way to get the response value.
    Optional<String> result = dialog.showAndWait();
     if (result.isPresent()){
      libelle=result.get();
 
               Alert alert = new Alert(AlertType.NONE);
               alert.setTitle("Enregistrement des repas");
               ProgressBar  progressBar =new ProgressBar(0);
               progressBar.prefWidth(100.0);
               alert.setGraphic( progressBar);
               alert.show();
               Task copyWorker = createWorker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());
        
        copyWorker.messageProperty().addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observable,
              String oldValue, String newValue) {
              System.out.println("new value: "+newValue);
           if("terminer".equals(newValue))
           {
              Label[] label={aetGlucides,aetLipides,aetProtides,prcentGlucide,prcentLipide,prcentProtide,pcentDeuxMill,pcentDeuxMillCinq,pcentMilCinq,pcentTroisMill,pcentTroisMillCinq};
               intialiserLesLabelsEnPourcentage(label);
               bilanList.clear();
               table_aliment_deja_choisi.getItems().clear();
               table_aliment_a_choisir.getItems().clear();
               tableBilan.getItems().clear();
               initialisationCombobox();
               initialiserJtextField();
               initialiserLeTableauAchoisir();
                labelAttention.setVisible(false);
                buttonExpert.setVisible(false);
                travailEnregistre=false;
                piecharList.clear();
                pieCharts.getData().clear();
                idRepas++;
                idRepasAliment++;
               alert.setTitle("Menu enregisterer");
              alert.setContentText("Votre Menu a ete Enregistré :");
              alert.setAlertType(AlertType.INFORMATION);
            alert.close();
            alert.getButtonTypes().setAll(ButtonType.FINISH);  
                        alert.showAndWait();
//   alert.close();
            
           }
                    }
        });
        new Thread(copyWorker).start();
             
               
             }
else{
    System.out.println("yessssssssssssss");
   }     
             }
         });
               
              
   }
     public Task createWorker() {
    return new Task() {
      @Override
      protected Object call() throws Exception {
           java.util.Date date= new java.util.Date();
      //  System.out.println());
           FmRepas repas=new FmRepas(idRepas);
           repas.setEnergie(Float.parseFloat(EnergieTotalePrEnregister.toString()));
           repas.setLipide(Float.parseFloat(retentionLipide.toString()));
           repas.setProtide(Float.parseFloat(retentionProtide.toString()));
           repas.setGlucide(Float.parseFloat(retentionGlucide.toString()));
           repas.setLibelle(libelle);
           repas.setDate(new Timestamp(date.getTime()));
           formulyTools.getEm().createEntityManager().getTransaction().begin();
           repasCont.create(repas);
           //appel du controller de Jpa 
          List<mainModel> liste=table_aliment_deja_choisi.getItems();
          int tailleDonnee=liste.size();
          System.out.println("id :");
          int  j=4;
        for (int i = 0; i <tailleDonnee; i++) {
          Thread.sleep(200);
          //nous allonslancer le Proccess
            System.out.println("id repas aliment: "+idRepasAliment);
          mainModel ls=liste.get(i);
          FmAliments al=alimenCtr.findFmAliments(ls.getIdAliment());
          FmRepasAliments repaAlmt=new FmRepasAliments(idRepasAliment);
          repaAlmt.setAliment(al);
          repaAlmt.setQuantite(Float.valueOf(ls.getQte()));
          repaAlmt.setRepas(repas);
          repaAlmt.setDate(new Timestamp(date.getTime()));
          repasAlCtr.create(repaAlmt);
          idRepasAliment++;
          updateProgress(j + 1, 10);
          j=(100/tailleDonnee);
          if(i!=tailleDonnee-1)
          {
          updateMessage(""+i+1);
          }
          else{
         updateMessage("terminer");
          }
         
        }
         formulyTools.getEm().createEntityManager().getTransaction().commit();
        return true;
      }
    };
  }
     public void intialiserLesLabelsEnPourcentage(Label [] labels)
     {
         for(int i=0;i<labels.length;i++)
           {
      labels[i].setText("O %");
           }
     }
   public static void lancerExecution()
   {
     
    
   }
   public List<FmAlimentsPathologie>  ListePathologie()
   {
       // EntityManager entityM=formulyTools.getEm("fx_formulyPU" ).createEntityManager(); 
 FmAlimentsPathologieJpaController es=new FmAlimentsPathologieJpaController(formulyTools.getEm("fx_formulyPU" ));
        List<FmAlimentsPathologie> list = null;
 try {
           list=es.findFmAlimentsPathologieEntities();
//             es.getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            Logger.getLogger(Fx_formuly.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
   }
   public   ObservableList<FmAlimentsPathologie>   verificationPathologie(ObservableList<mainModel> main)
   {
       ObservableList<FmAlimentsPathologie> listInterdit=FXCollections.observableArrayList();
       List<FmAlimentsPathologie> listeAlPa=ListePathologie();
       for(FmAlimentsPathologie listP:listeAlPa)
       {
       int idA1=listP.getAliment().getId();
             for(int i=0;i<main.size();i++)
             {
           int idA2=main.get(i).getIdAliment();
                 System.out.println("idA1: "+idA1+" idA2: "+idA2);
            if(idA2==idA1)
            {
             listInterdit.add(listP);
            }
             }
       }
       return listInterdit;
   }
   public void EnregistrerRepas()
   {
       System.out.println("ok");
   }
   /**
    * methode permettant de remplir les resultatts des calculs dans le
    * tableau des resultats par aliment.
    * cette methode devra etre appeller avant la methode  bilanGeneral
     * @param obListElmt la liste contenant les aliments selectionner
    * @param bilanElements  la liste vide des elements du bilan (par nature vide )
    *  @param resultatMacro le tableau des resultats pour le bilan
    */
    public void LoadObservableList(ObservableList<mainModel> obListElmt,ObservableList<bilanMacroNut> bilanElements,TableView<bilanMacroNut> resultatMacro)
   {
     
        for(mainModel main: obListElmt)
        {
        bilanElements.add(new bilanMacroNut(main));
         }
      aliment.setCellValueFactory(new PropertyValueFactory<>("aliment"));
      quantites.setCellValueFactory(new PropertyValueFactory<>("quantites")); 
      lipide.setCellValueFactory(new PropertyValueFactory<>("valeurLipide")); 
      protide.setCellValueFactory(new PropertyValueFactory<>("valeurProtide"));
      glucide.setCellValueFactory(new PropertyValueFactory<>("valeurGlucide"));
      energie.setCellValueFactory(new PropertyValueFactory<>("valeurEnergie"));
      pays.setCellValueFactory(new PropertyValueFactory<>("pays"));
        
      resultatMacro.setItems(bilanElements);
   }
    @FXML private TableView<bilanMacroNut> tableBilan;
    @FXML private TableColumn<bilanMacroNut, String>   aliment;
    @FXML private TableColumn<bilanMacroNut, String>   quantites;
    @FXML private TableColumn<bilanMacroNut, Double>   glucide;
    @FXML private TableColumn<bilanMacroNut, Double>   protide;
    @FXML private TableColumn<bilanMacroNut, Double>   lipide;
    @FXML private TableColumn<bilanMacroNut, Double>  energie;
      @FXML private TableColumn<bilanMacroNut, String>  pays;
      @FXML private Label aetLipides;
      @FXML private Label aetProtides;
      @FXML private Label aetGlucides;
      @FXML private Label energieTotale;
      @FXML private Label prcentGlucide;
      @FXML private Label prcentLipide;
      @FXML private Label prcentProtide;
      @FXML private Label pcentMilCinq;
      @FXML private Label pcentDeuxMill;
      @FXML private Label pcentDeuxMillCinq;
      @FXML private Label pcentTroisMill;
      @FXML private Label pcentTroisMillCinq;
      PieChart.Data data0;
      PieChart.Data data1;
      PieChart.Data data2;
      
     private Double EnergieTotalePrEnregister;
     private Double retentionLipide;
     private Double retentionProtide;
     private Double retentionGlucide;
     private String libelle;
    private int idRepas;
    private  int idRepasAliment;
     private FmRepasJpaController repasCont;
     private FmRepasAlimentsJpaController repasAlCtr;
     private FmAlimentsJpaController alimenCtr;
     
 public void initialisationValeurs(Double [] valeurs)
 {
   for(int i=0;i<valeurs.length;i++)
   {
   valeurs[i]=0.0;
   }
 }

      // definition de la tache de Fond
        
  
        //  Button[] btn={fermerFentre,envoi,reinitialiser,validerMenu};
}
