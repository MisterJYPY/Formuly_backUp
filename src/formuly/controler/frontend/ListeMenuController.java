/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formuly.controler.frontend;

import formuly.classe.alimentRepasModel;
import formuly.classe.formulyTools;
import formuly.classe.repasModel;
import formuly.entities.FmAliments;
import formuly.entities.FmRepas;
import formuly.entities.FmRepasAliments;
import formuly.entities.FmRetentionNutriments;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.persistence.EntityManagerFactory;

/**
 * FXML Controller class
 *
 * @author Mr_JYPY
 */
public class ListeMenuController implements Initializable {

    /**
     * Initializes the controller class.
     */
EntityManagerFactory     entityManagerFactory;
      @FXML
    private TableColumn<repasModel,Date> date;

    @FXML
    private TableColumn<repasModel,Float> protideTotale;

    @FXML
    private TableColumn<alimentRepasModel, Float> numeroLocale;

    @FXML
    private TableColumn<repasModel,Float> glucideTotale;

    @FXML
    private TableColumn<repasModel, Integer> numero;

    @FXML
    private TableColumn<alimentRepasModel, Float> lipideLocale;

    @FXML private TableColumn<repasModel, String> prendre;

    @FXML
    private TableColumn<alimentRepasModel, Float> protideLocale;

    @FXML
    private TableColumn<alimentRepasModel, Float> energieLocale;

    @FXML
    private TableColumn<repasModel,Float> energieTotale;

    @FXML
    private TableView<repasModel> tableRepas;

    @FXML
    private TableView<alimentRepasModel> tableAliment;

    @FXML
    private TableColumn<alimentRepasModel,String> nom_aliment;

    @FXML
    private TableColumn<alimentRepasModel, Float> glucideLocale;

    @FXML
    private TableColumn<repasModel,Float> lipideTotale;

    @FXML
    private TableColumn<alimentRepasModel, Float> quantite;

    @FXML
    private TableColumn<FmRepas, String> nom_repas;
    ObservableList<repasModel> bilanList;
    ObservableList<alimentRepasModel> detailAliment;

    public ListeMenuController() {
          bilanList=FXCollections.observableArrayList();
          detailAliment=FXCollections.observableArrayList();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initialiserTableauRepas();
    }    
    public void placerBouton()
    {
     prendre.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
  Callback<TableColumn<repasModel, String>, TableCell<repasModel, String>> cellFactory = new Callback<TableColumn<repasModel, String>, TableCell<repasModel, String>>() {      
                    @Override
            public TableCell call(final TableColumn<repasModel, String> param) {
                final TableCell<repasModel, String> cell = new TableCell<repasModel, String>() {

                    final Button btn = new Button("Extraire");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.getStyleClass().add("dark-blue");
                            btn.setOnAction(event -> {
                                try {
                                    repasModel person = getTableView().getItems().get(getIndex());
                                     chargerPanelRepas(btn,person) ;
                                } catch (IOException ex) {
                                    Logger.getLogger(ListeMenuController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
//                            btn.setOnMouseDragOver(event->{
//                             btn.getStyleClass().add("dark-blue-hover"); 
//                            });
                            setGraphic(btn);
                            
                            setText(null);
                        }
                    }
                };
                return cell;
                
            }
        };
         prendre.setCellFactory(cellFactory);

    }
    public ObservableList<repasModel> chargerLists()
    {
           //transformer en caractere
       NumberFormat format=NumberFormat.getInstance();
            format.setMaximumFractionDigits(2); 
      ObservableList<repasModel> model=FXCollections.observableArrayList();
         List<FmRepas> lisRepas=null;
        FmRepasJpaController repaC=new FmRepasJpaController(formulyTools.getEm());
        lisRepas=repaC.findFmRepasEntities();
        System.out.println("nbrrrr :"+lisRepas.size());
          System.out.println("nbrrrr :"+lisRepas.get(lisRepas.size()-1).getLibelle());
           int i=0;
        for(FmRepas repas :lisRepas)
        {
            repasModel rpM=new repasModel();
              rpM.setNumero(i+1);
           //   Double db=Double.valueOf(format.format(Double.valueOf(repas.getGlucide().toString())));
           rpM.setEnergie(repas.getEnergie());
           rpM.setGlucide(repas.getGlucide());
           rpM.setProtide(repas.getProtide());
           rpM.setLipide(repas.getLipide());
           rpM.setLibelle(repas.getLibelle());
           rpM.setId_repas(repas.getId());
           rpM.setDate(repas.getDate());
          
             model.add(rpM);
             i++;
        }
       return model;
    }
    public void initialiserTableauRepas()
    {
      numero.setCellValueFactory(new PropertyValueFactory<>("numero")); 
      nom_repas.setCellValueFactory(new PropertyValueFactory<>("libelle")); 
      glucideTotale.setCellValueFactory(new PropertyValueFactory<>("glucide")); 
      protideTotale.setCellValueFactory(new PropertyValueFactory<>("protide"));
      lipideTotale.setCellValueFactory(new PropertyValueFactory<>("lipide")); 
     energieTotale.setCellValueFactory(new PropertyValueFactory<>("energie"));
     date.setCellValueFactory(new PropertyValueFactory<>("date"));
     
         placerBouton();
        bilanList=chargerLists();
        actionSurTable(tableRepas);
       tableRepas.setItems(bilanList);
       
    }
   
    public void actionSurTable(TableView table)
    {
   table.setOnMousePressed(new EventHandler<MouseEvent>() {
    @Override 
   public void handle(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
           repasModel rpm= (repasModel) table.getSelectionModel().getSelectedItem();
            RemplirTableBas(rpm);
    }}
});
  }
    public ObservableList<alimentRepasModel> listDesAliment(repasModel rpm)
    {
    ObservableList<alimentRepasModel>  list=FXCollections.observableArrayList();
    List<FmRepasAliments> listeRepasAl=null;
    FmRepasAlimentsJpaController repasAl=new FmRepasAlimentsJpaController(formulyTools.getEm());
     listeRepasAl=(  List<FmRepasAliments> ) repasAl.findFmRepasAlimentsByRepas(rpm.getId_repas());
    FmRepasAliments Alrepas=null;
    alimentRepasModel alrpm=null;
     FmRetentionNutriments retentionAliments=null;
     if( listeRepasAl.size()>0)
     {
           FmRetentionNutriments retentionAuCasOuNul=new FmRetentionNutriments();
      for(int i=0;i<listeRepasAl.size();i++)
      {
       Alrepas=( FmRepasAliments)listeRepasAl.get(i);
       alrpm=new alimentRepasModel();
      
       FmAliments aliment=Alrepas.getAliment();
       Float quantite=Alrepas.getQuantite();
       FmRetentionNutrimentsJpaController  ctr=new FmRetentionNutrimentsJpaController(formulyTools.getEm());
       List<FmRetentionNutriments> retF=ctr.findFmRetentionNutrimentsByAliment(aliment);     
                retentionAuCasOuNul.setLipide(0);
                retentionAuCasOuNul.setGlucide(0);
                retentionAuCasOuNul.setProtein(0);
       retentionAliments=(retF.size()>0)?retF.get(0):retentionAuCasOuNul;
        
       Float Lipide=retentionAliments.getLipide();
       Float glucide=retentionAliments.getGlucide();
       Float protide=retentionAliments.getProtein();
      
    // System.out.println("Glucide: "+retentionAliments.getGlucide());
       Float qtGl=(quantite*glucide)/100;
       Float qtPr=(quantite*protide)/100;
       Float qtLp=(quantite*Lipide)/100;
      
      Float energie=(qtGl*4)+(qtPr*4)+(qtLp*9);
       //initialisation
       alrpm.setNumero(i+1);
       alrpm.setEnergie(energie);
       alrpm.setGlucide(qtGl);
       alrpm.setLipide(qtLp);
       alrpm.setProtide(qtPr);
       alrpm.setLibelle(aliment.getNomFr());
       alrpm.setQuantite(quantite);
       alrpm.setId_aliment(aliment.getId());
       
       list.add(alrpm);
       
      }}
    return list;
    }
     
    public void RemplirTableBas(repasModel repas)
    {
        System.out.println("repas: "+repas.getLibelle());
      numeroLocale.setCellValueFactory(new PropertyValueFactory<>("numero")); 
      nom_aliment.setCellValueFactory(new PropertyValueFactory<>("libelle")); 
      glucideLocale.setCellValueFactory(new PropertyValueFactory<>("glucide")); 
      protideLocale.setCellValueFactory(new PropertyValueFactory<>("protide"));
      lipideLocale.setCellValueFactory(new PropertyValueFactory<>("lipide")); 
      energieLocale.setCellValueFactory(new PropertyValueFactory<>("energie"));
      quantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
      
       if(detailAliment.size()>0)
       {
             detailAliment.clear();
       }
  
       // 
        detailAliment=listDesAliment(repas);
        tableAliment.setItems(detailAliment);
    }
     public void chargerPanelRepas(Button faireRepas,repasModel modelRepas) throws IOException
    {
              detailAliment.clear();
            detailAliment=listDesAliment(modelRepas);
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/formuly/view/frontend/make_foods_forMenu.fxml"));
        // loader.setLocation();
         ctrMakeFoods=new Make_foods_forMenuController(modelRepas,detailAliment);
         loader.setController(ctrMakeFoods);
          Parent root = loader.load();
         st=new Stage();
         st.setScene(new Scene(root));
         st.setTitle("formuly Foods Selector");
         st.initOwner(faireRepas.getScene().getWindow());
         st.initModality(Modality.APPLICATION_MODAL);
         
         st.showAndWait();
       //  return st;
       
      }
   private Make_foods_forMenuController ctrMakeFoods;
   private Stage st;
}