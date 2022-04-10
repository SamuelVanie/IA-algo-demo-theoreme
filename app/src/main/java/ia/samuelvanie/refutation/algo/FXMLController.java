package ia.samuelvanie.refutation.algo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import static ia.samuelvanie.refutation.algo.Refutation.solve;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class FXMLController {

    @FXML
    private TextField textAnswer;

    @FXML
    private TextArea textArea;

    public void quit(ActionEvent event) {
        System.out.println("QUIT");
    }

    public void ancien(ActionEvent event) {
        fetchResultFile();
    }

    public void aide(ActionEvent event) {
        System.out.println("AIDE");        
    }

    public void fetchResultFile(){
        try {
            FileInputStream fis = new FileInputStream("result");
            Scanner sc = new Scanner(fis);
            List<String> text = new ArrayList<String>();
            while(sc.hasNext()){
                textArea.setText(textArea.getText() + "\n" + sc.nextLine());
            }
            sc.close();
        } catch(Exception e){
            textArea.setText("Erreur le fichier de résultats n'a pas pu être lu");
        }
    }

    public void nouveau(ActionEvent event) {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Resoudre un nouveau problème");

        textInput.getDialogPane().setContentText("Combien de clauses sont dans votre base de connaissance?");

        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();

        if (input.getText() != null && input.getText().toString().length() != 0) {
            Integer n = Integer.parseInt(input.getText().toString());
            List<String> clauses = new ArrayList<String>();
            for (int i=0; i<n; i++) {
                textInput.getDialogPane().setContentText("Entrez la clause numéro " + (i+1));
                result = textInput.showAndWait();
                input = textInput.getEditor();
                if (input.getText() != null && input.getText().toString().length() != 0) {
                   clauses.add(input.getText().toString().split(" ")[0]); 
                }else{
                    textInput.setResult("Clause vide");
                    textInput.close();
                }
            }
            textInput.getDialogPane().setContentText("Quelle propriété voulez-vous vérifier?");
            result = textInput.showAndWait();
            input = textInput.getEditor();
            String query = new String();
            if (input.getText() != null && input.getText().toString().length() != 0) {
                query = input.getText().toString().split(" ")[0];
            }else{
                textInput.setResult("Champ vide");
                textInput.close();
            }

            textInput.getDialogPane().setContentText("Voulez-vous afficher la trace du raisonnement ?");
            result = textInput.showAndWait();
            input = textInput.getEditor();
            if (input.getText() != null && input.getText().toString().length() != 0) {
                Integer m = Integer.parseInt(input.getText().toString().split(" ")[0]);
                solve(m, clauses, query);
                fetchResultFile();
            }else{
                textInput.setResult("Champ vide");
                textInput.close();
            }
            
        }else{
            textInput.setResult("Champ vide");
            textInput.close();
        }
    }

}
