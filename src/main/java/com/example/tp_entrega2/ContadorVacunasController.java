package com.example.tp_entrega2;

import clases.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ContadorVacunasController
{
    @FXML
    Label lblArchivoNombre;
    @FXML
    Button btnElegirArchivo;
    @FXML
    TableView tblDatos;
    @FXML
    ComboBox cbxDepartamentos;
    @FXML
    RadioButton rdbSexo;
    @FXML
    RadioButton rdbOrden;
    @FXML
    RadioButton rdbVacuna;

    @FXML
    void BtnElegirArchivoClick()
    {
        ///Busqueda del archivo
        FileChooser archChooser = new FileChooser();
        FileChooser.ExtensionFilter filtro = new FileChooser.ExtensionFilter("Archivos de Valores Separados por Comas", "*.csv");
        archChooser.getExtensionFilters().add(filtro);
        archChooser.setTitle("Elegir archivo");
        File archivo = archChooser.showOpenDialog(null);
        //Mostrar nombre del archivo
        lblArchivoNombre.setText("Archivo: " + archivo.getName());

        ///Lectura del archivo
        try
        {
            Scanner lectorArchivo = new Scanner(archivo);
            lectorArchivo.nextLine();   //Se saltea la primera linea que son los titulos de las columnas
            Contador contadorTotal = new Contador();

            for (int i = 0; i < 2000; i++) //Limitado con un for porque el archivo es gigante, dsp pasar a while
            {
                //Tomar una linea del arhivo y crea Scanner para analizarla
                String datoCrudo = lectorArchivo.nextLine();
                Dato dato = new Dato(datoCrudo);
                //Analiza el dato y suma al contador total
                if(dato.getJurisdiccionAplicacion().getNombre().equals("Córdoba"))
                {
                    contadorTotal.contarSexo(dato.getSexo());
                    contadorTotal.contarOrden("" + dato.getOrdenDosis());
                    contadorTotal.contarVacuna(dato.getVacuna());
                    System.out.println(dato);   //Borrar
                }
                else {i--;} //Aca le resto a i para que muestre 2000 entradas de Cordoba para comparar si el contador cuenta bien
            }

            //Muestra en consola Despues borrar
            System.out.println("Lectura Terminada!");
            System.out.println("Conteo: ");
            System.out.println("Masculinos: " + contadorTotal.getContMasculino() + " -- Femeninos: " + contadorTotal.getContFemenino() + " -- Otros: " + contadorTotal.getContOtro());
            System.out.println("Primera: " + contadorTotal.getContPrimera() + " -- Segunda: " + contadorTotal.getContSegunda() + " -- Extra: " + contadorTotal.getContExtra());
            int[] contVacunas = contadorTotal.getContVacunas();
            String[] nombresVacunas = contadorTotal.getNombresVacunas();
            for (int i = 0; i < nombresVacunas.length; i++) {
                System.out.print(nombresVacunas[i] + ": " + contVacunas[i] + " -- ");
            }


            /*
            //Crea tabla
            TableColumn<Dato, String> colSexo = new TableColumn<>("Sexo");
            colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

            TableColumn<Dato, String> colGrupoEtario = new TableColumn<>("Grupo Etario");
            colGrupoEtario.setCellValueFactory(new PropertyValueFactory<>("grupoEtario"));

            tblDatos.getColumns().addAll(colSexo, colGrupoEtario);
            tblDatos.getItems().addAll(listaDatos);
            */
        }
        catch (FileNotFoundException exNoEncontrado)
        {
            System.err.println("Error inesperado, archivo no encontrado");
        }
    }
}