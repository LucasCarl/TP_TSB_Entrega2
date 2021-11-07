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
import java.util.ArrayList;
import java.util.List;
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

        //Crear tabla hash
        TSBHashTableDA<String, Contador> tablaHashDepartamentos = new TSBHashTableDA<String, Contador>();

        ///Lectura del archivo
        try
        {
            Scanner lectorArchivo = new Scanner(archivo);
            lectorArchivo.nextLine();   //Se saltea la primera linea que son los titulos de las columnas
            Contador contadorTotal = new Contador();
            tablaHashDepartamentos.put("Total", contadorTotal);
            List<String> nombreDepartamentos = new ArrayList<String>();

            for (int i = 0; i < 2000; i++) //TODO Limitado con un for porque el archivo es gigante, dsp pasar a while
            {
                //Tomar una linea del arhivo y crea Scanner para analizarla
                String datoCrudo = lectorArchivo.nextLine();
                Dato dato = new Dato(datoCrudo);
                //Analiza el dato y suma al contador total
                if(dato.getJurisdiccionAplicacion().getNombre().equals("Córdoba"))
                {
                    //Conteo total
                    relizarConteos(tablaHashDepartamentos, "Total", dato);

                    //Contador de departamento
                    String dptoNombre = dato.getDeptoAplicacion().getNombre();

                    //Si no existe el departamento en la tabla lo agrega
                    if(!tablaHashDepartamentos.containsKey(dptoNombre))
                    {
                        tablaHashDepartamentos.put(dptoNombre, new Contador());
                        nombreDepartamentos.add(dptoNombre);
                    }

                    //Conteo Departamento
                    relizarConteos(tablaHashDepartamentos, dptoNombre, dato);

                }
                //else {i--;} //Aca le resto a i para que muestre 2000 entradas de Cordoba para comparar si el contador cuenta bien
            }

            //Muestra en consola Despues borrar
            System.out.println("Lectura Terminada!");
            System.out.println("Conteo: ");
            System.out.println("Masculinos: " + tablaHashDepartamentos.get("Total").getContMasculino() + " -- Femeninos: " + tablaHashDepartamentos.get("Total").getContFemenino() + " -- Otros: " + tablaHashDepartamentos.get("Total").getContOtro());
            System.out.println("Primera: " + tablaHashDepartamentos.get("Total").getContPrimera() + " -- Segunda: " + tablaHashDepartamentos.get("Total").getContSegunda() + " -- Extra: " + tablaHashDepartamentos.get("Total").getContExtra());
            int[] contVacunas = tablaHashDepartamentos.get("Total").getContVacunas();
            String[] nombresVacunas = tablaHashDepartamentos.get("Total").getNombresVacunas();
            for (int i = 0; i < nombresVacunas.length; i++) {
                System.out.print(nombresVacunas[i] + ": " + contVacunas[i] + " -- ");
            }

            //Agrego nombres departamentos a combobox
            //cbxDepartamentos.getItems().clear();
            //cbxDepartamentos.getItems().addAll(nombreDepartamentos);

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

    private void relizarConteos(TSBHashTableDA<String, Contador> tabla, String contadorNombre, Dato dato)
    {
        Contador cont = tabla.get(contadorNombre);
        cont.contarSexo(dato.getSexo());
        cont.contarOrden("" + dato.getOrdenDosis());
        cont.contarVacuna(dato.getVacuna());
    }
}